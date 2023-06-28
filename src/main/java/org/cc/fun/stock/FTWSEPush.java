package org.cc.fun.stock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import org.cc.fun.proc.dao.BiDaoBacthInsert;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;
import org.cc.model.ICCModule;


/**
 *
 * @author william
 */
public class FTWSEPush extends FStockBase implements BiFunction<CCProcObject, String, Boolean> {
    private Set<String> names = new HashSet<>();
    @Override
    public Boolean apply(CCProcObject proc, String cmdString) {

        return proc_range(proc, cmdString);
    }

    @Override
    public void proc_date(CCProcObject proc, ICCModule cm, Date d) {
        proc.params().put("sd",d);
        List<JSONObject> rows =  (List<JSONObject>) CCProcUtils.exec(proc, "stock.FTWSEData@twse");
        rows = filter(proc, rows);
        JSONObject row = (JSONObject) CCProcUtils.exec(proc, "row@twse,chkDate");
        int num = row!=null ? row.optInt("num") : 0 ; 
        if(!rows.isEmpty() && rows.size()!=num){
            System.out.println("===== add rows :" + rows.size());
            BiDaoBacthInsert bat = new BiDaoBacthInsert(proc, rows ,"twse","mstock" );
            int[] ret = bat.executeBatch();
        }
    
    }

    private List<JSONObject> filter(CCProcObject proc,List<JSONObject> rows){
        List<JSONObject> ret = new ArrayList<>();
        init_names(proc);
        for(JSONObject row : rows) {
            //System.out.println(row);
            String stockId = row.optString("stockid");
            if (names.contains(stockId)){
                ret.add(row);
            }
        }
        return ret;
    }

    private void init_names(CCProcObject proc) {
        if(names.size()==0){
            List<JSONObject> rows = (List<JSONObject>) CCProcUtils.exec(proc, "rows@twse,stockList");
            for(JSONObject row:rows){
                names.add(row.optString("stockId"));
            }
        }
        System.out.println("===== stock names size:" + names.size());
    }

}
