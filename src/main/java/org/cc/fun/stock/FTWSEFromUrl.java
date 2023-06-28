package org.cc.fun.stock;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiFunction;

import org.cc.data.CCData;
import org.cc.data.CCHttpData;
import org.cc.json.CCJSON;
import org.cc.json.JSONObject;
import org.cc.model.CCActObject;
import org.cc.model.CCCMParams;
import org.cc.model.CCModule;
import org.cc.model.CCProcObject;
import org.cc.model.CCTemplate;
import org.cc.util.CCDateUtils;

/**
 *
 * @author william
 */
public class FTWSEFromUrl implements BiFunction<CCProcObject, String, Boolean> {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Override
    public Boolean apply(CCProcObject proc, String line) {
        System.out.println(proc.base());
        CCCMParams cmp = CCCMParams.newInstance(line);
        CCModule cm = proc.module(cmp.mid());
        CCActObject act = cm.act(cmp.aid());
        Date dp1 = proc.params().optDate("dp1");
        Date dp2 = proc.params().optDate("dp2");
        dp2 = (dp2 == null) ? new Date() : dp2;
        Date d = (Date) dp1.clone();
        System.out.println(d);
        System.out.println(dp2);
        while (d.before(dp2)) {
            try {
                proc_date(proc, act, d);

            } catch (Exception e) {
                e.printStackTrace();
            }
            
            d = CCDateUtils.addDate(d, 1);
        }
        return true;
    }

    public void proc_date(CCProcObject proc, CCActObject act, Date d) throws Exception {
        String tp = act.cfg().optString("$targetPath");
        JSONObject pp = proc.optJSONObject("$$");
        tp = tp.replace("$gstock", pp.optString("$gstock"));
        File f = new File(tp, sdf.format(d) + ".json");
        act.cfg().put("dp1", sdf.format(d));
        //if (!(f.exists() && f.length()>4096)) {
        if (!f.exists()) {
            //System.out.println("===== fetch file ::: "+f);
            String url = (String) CCTemplate.eval(act.cfg().optString("$url"), act.cfg());
            System.out.println("===== fetch : "+url);
            String content = CCHttpData.text(url, "UTF-8");
            CCData.saveText(f,content, "UTF-8");
            Thread.sleep(10000);
        } else {
            System.out.println("===== skip "+ f);
        }
    }

}
