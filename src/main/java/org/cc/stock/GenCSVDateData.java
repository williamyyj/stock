package org.cc.stock;

import java.io.File;
import java.util.List;

import org.cc.App;
import org.cc.data.CCData;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.stock.col.FStatBase;
import org.cc.stock.col.SOutputColumns;
import org.cc.stock.model.StockModel;

public class GenCSVDateData {
	public static void main(String[] args) {
		String base = System.getProperty("$base", App.base);
		String dp = App.google + "\\mydata\\stock\\twotc";
		String stockId = "00680L";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			StockModel sm = new StockModel(proc, stockId);
			List<JSONObject> rows = sm.data();
			
			int idx  = 0;
			double acc = 0.0;
			for(JSONObject row:rows) {
				JSONObject st233 = FStatBase.stat(rows, FStatBase.fnSC, 610, idx);
				System.out.println(st233);
				idx++;
			}
			// StockModel sm = new StockModel(dp,stockId);
		
			SOutputColumns out = new SOutputColumns(
					new String[] { "sdate:date", "sc", "av23", "av55", "av233" ,"acc" });
			StringBuilder sb = out.toCSVString(sm.data());
			CCData.saveText(new File(App.google + "\\stock\\data\\" + stockId + "-mem.csv"), sb.toString(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
