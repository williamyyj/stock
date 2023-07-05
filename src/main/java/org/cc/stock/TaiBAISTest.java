package org.cc.stock;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.cc.App;
import org.cc.data.CCData;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.stock.col.FStatBase;
import org.cc.stock.col.IFNValue;
import org.cc.stock.col.SKDColumns;
import org.cc.stock.col.SMACDColumns;
import org.cc.stock.col.STaijiBIASColumns;
import org.cc.stock.col.STaijiCircleColumns;
import org.cc.stock.model.SMonthModel;
import org.cc.text.TextUtils;

public class TaiBAISTest {


	
	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		String base = System.getProperty("$base", App.base);
		String stockId = "006208";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			SMonthModel sm = new SMonthModel(proc, stockId);
			// new STaijiCircleColumns(sm);
			new STaijiBIASColumns(sm,720,20);
			new STaijiBIASColumns(sm,720,60);
			new STaijiBIASColumns(sm,720,240);
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<sm.data().size();i++) {
				JSONObject row = sm.data().get(i);
				JSONObject b20 = row.optJSONObject("b20");
				JSONObject b60 = row.optJSONObject("b60");
				JSONObject b240 = row.optJSONObject("b240");
				sb.append(TextUtils.df("yyyy-MM-dd", row.optString("sdate")) + "," + row.optDouble("sc"));
				double g20 = b20.optDouble("grade");
				double g60 = b60.optDouble("grade");
				double g240 =b240.optDouble("grade");
				sb.append(",").append(g20);
				sb.append(",").append(g60);
				sb.append(",").append(g240);
				sb.append("\n");
				System.out.println(b20);
				System.out.println(b60);
				System.out.println(b240);
			}
			
			CCData.saveText(new File(App.google+"\\stock\\data\\" + stockId + "-mem.csv"), sb.toString(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
