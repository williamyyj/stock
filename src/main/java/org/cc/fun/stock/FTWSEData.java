package org.cc.fun.stock;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;


import org.cc.json.CCJSON;
import org.cc.json.JSONArray;
import org.cc.json.JSONObject;
import org.cc.model.CCCMParams;
import org.cc.model.CCProcObject;
import org.cc.model.ICCModule;
import org.cc.text.TextUtils;


/**
 * @author william
    proc.params().put("sd","20051214");
    List<JSONObject> rows =  (List<JSONObject>) CCProcUtils.exec(proc, "stock.FTWSEData@twse");
 */
public class FTWSEData implements BiFunction<CCProcObject, String, List<JSONObject>> {
    
    private NumberFormat nf2 = new DecimalFormat("#,###,###,###,###");
    private NumberFormat nf1 = new DecimalFormat("######.00");
    

    @Override
    public List<JSONObject> apply(CCProcObject proc, String line) {
        CCCMParams cmp = CCCMParams.newInstance(line);
        ICCModule cm = proc.module(cmp.mid()); 
        JSONArray data = loadData(proc,cm,cmp);
        List<JSONObject> rows = new ArrayList<>();
        Date sdate = proc.params().optDate("sd");
        for(int i=0; i<data.length();i++){
            JSONObject row = proc_item(proc,cm,data.optJSONArray(i));
            row.put("sdate",sdate);
            rows.add(row);
        }
        return rows;
    }

    private JSONArray loadData(CCProcObject proc, ICCModule cm, CCCMParams cmp) {
        JSONObject pp = proc.optJSONObject("$$");
        String tp = cm.cfg().optString("$targetPath").replace("$gstock",pp.optString("$gstock") );
        String fileId = TextUtils.df("yyyyMMdd",proc.params().opt("sd"));
        System.out.println(fileId);
        File f = new File(tp,fileId+".json");
        System.out.println("===== loading ... "+f);
        if(f.exists()){
            JSONObject jo = CCJSON.load(f);
            String pattern = cm.cfg().optJSONArray("fields").toString();
            if("OK".equals(jo.optString("stat"))){
            	
                for(String name : jo.keySet()){
                	System.out.println("===== name:"+name);
                    if(name.contains("fields")){
                        Object ja = jo.opt(name);
                        if(pattern.equals(ja.toString())){
                            String dataId = name.replace("fields", "data");
                            return jo.optJSONArray(dataId);
                        }                   
                    }
                }
                // 20240703 後的資料
                JSONArray  tbs = jo.optJSONArray("tables");
				for (int i = 0; i < tbs.length(); i++) {
					JSONObject tb = tbs.optJSONObject(i);
					if (pattern.equals(tb.optJSONArray("fields").toString())) {
						return tb.optJSONArray("data");
					}
				}
                
            }
        }
        return new JSONArray();
    }

    private JSONObject proc_item(CCProcObject proc, ICCModule cm, JSONArray item) {
        JSONObject ret = new JSONObject();
        JSONArray flds = cm.cfg().optJSONArray("$fields");
        for (int i = 0; i < flds.length(); i++) {
            JSONObject fld = flds.optJSONObject(i);
            ret.put(fld.optString("id"), value(fld, item));
        }
        return ret;
    }

    private Object value(JSONObject fld, JSONArray row) {
        Object o = row.get(fld.optInt("idx"));
        String cast = fld.optString("cast");
        try {
            if ("num1".equals(cast)) {
                return nf1.parse(o.toString()).doubleValue() ;
            } else if ("num2".equals(cast)) {
                return nf2.parse(o.toString()).longValue();
            }
        } catch (Exception e) {
            return 0;
        }
        return o;
    }


}
