package org.cc.stock;

import java.io.IOException;

import org.cc.App;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;

/**
 *
 * @author william
 */
public class PullTWOTCData {

    public static void main(String[] args) throws IOException {
        String base = System.getProperty("$base", App.base);
        CCProcObject proc = new CCProcObject(base,false);
        try {
            proc.put("$$", App.$app);
            proc.params().put("dp1", "20250101");
            
            Object ret = CCProcUtils.exec(proc, "stock.FStockFromUrl@twse,TWOTC");
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            proc.close();
        }
    }
    
}
