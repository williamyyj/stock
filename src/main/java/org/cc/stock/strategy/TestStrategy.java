package org.cc.stock.strategy;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.cc.App;
import org.cc.data.CCData;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.stock.col.FStatBase;
import org.cc.stock.col.SATRColumns;
import org.cc.stock.col.SBBandColumns;
import org.cc.stock.col.SOutputColumns;
import org.cc.stock.model.StockModel;

public class TestStrategy {
	
	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		String base = System.getProperty("$base", App.base);
		String stockId = "00878";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			StockModel sm = new StockModel(proc,stockId);
			//new SATRColumns(sm,2);
			new SBBandColumns(sm,21,2);

			for(JSONObject row: sm.data()) {
				double sd = row.optDouble("bsd");
				double sa = row.optDouble("bsa");
				row.put("l5",  sa + 2*sd);
				row.put("l4",  sa + sd);
				row.put("l3",  sa );
				row.put("l2",  sa - sd);
				row.put("l1",  sa - 2*sd);
			}
			
			//JSONObject tai = FStatBase.stat(sm.data(), ((row)->row.optDouble("sc")),610,sm.data().size()-1);
			//System.out.println(tai.toString(4));
			
			SOutputColumns out = new SOutputColumns(new String[] {"sdate:date","sc","l1","l2","l3","l4","l5"});
			StringBuilder sb = out.toCSVString(sm);
			CCData.saveText(new File(App.google+"\\stock\\data\\" + stockId + "-mem.csv"), sb.toString(),"UTF-8");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
