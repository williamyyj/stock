package org.cc.stock;

import java.util.List;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;

/**
 *
 * @author william
 */
public class SyncData {
    public static void main(String[] args)  {
        String base = System.getProperty("$base", App.base);
       
        try(CCProcObject proc = new CCProcObject(base,false);){
            List<JSONObject> rows = (List<JSONObject>) CCProcUtils.exec(proc, "rows@twse,stockList");
            for (JSONObject row:rows){
                //System.out.println(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
