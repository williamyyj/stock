package org.cc.stock;


import java.io.File;

import org.cc.App;
import org.cc.data.CCData;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.stock.col.SOutputColumns;
import org.cc.stock.model.MonthDataList;
import org.cc.stock.model.StockModel;

public class GenCSVMonthData {
    public static void main(String[] args){
        String base = System.getProperty("$base", App.base);
        String dp = App.google+"\\mydata\\stock\\twotc";
		String stockId = "00701";
        try (CCProcObject proc = new CCProcObject(base, false)) {
            StockModel sm = new StockModel(proc, stockId);
            //StockModel sm = new StockModel(dp,stockId);
            MonthDataList mList = new MonthDataList(sm);
             for(JSONObject item:mList){
                System.out.println(item);
            }
 	        SOutputColumns out = new SOutputColumns(new String[] {"sdate:string","paym:string","sa","sc","so","sh","sl","vol"});			
			StringBuilder sb = out.toCSVString(mList);
			CCData.saveText(new File(App.google+"\\stock\\data\\" + stockId + "-mem.csv"), sb.toString(),"UTF-8");
        } catch (Exception e) {
			e.printStackTrace();
		}
    }
}
