package org.cc.stock;

import org.cc.App;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;

/**
 * 資料匯入DB
 *
 * @author william
 */
public class PushTWSEData {

    public static void main(String[] args)  {
        String base = System.getProperty("$base", App.base); 
        try(CCProcObject proc = new CCProcObject(base,false);){
            proc.put("$$", App.$app);
            proc.params().put("dp1", "20241213");
            //proc.params().put("dp2", "20221230");
           CCProcUtils.exec(proc, "stock.FTWSEPush@twse");
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
