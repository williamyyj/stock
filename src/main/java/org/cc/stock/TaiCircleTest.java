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
import org.cc.stock.col.SOutputColumns;
import org.cc.stock.col.STaijiBIASColumns;
import org.cc.stock.col.STaijiCircleColumns;
import org.cc.stock.model.SMonthModel;
import org.cc.text.TextUtils;

public class TaiCircleTest {


	
	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		String base = System.getProperty("$base", App.base);
		String stockId = "00752";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			SMonthModel sm = new SMonthModel(proc, stockId);
			// new STaijiCircleColumns(sm);
			new STaijiCircleColumns(sm,((row)->row.optDouble("sc")));
			new SMACDColumns(sm);
			new SKDColumns(sm);
			for(int i=0; i<sm.data().size();i++) {
				//System.out.println(sm.data().get(i));
			}
			SOutputColumns out = new SOutputColumns(new String[] {"sdate:date","so","sh","sl","sc","vol","k","d","kd:string"});
			StringBuilder sb = out.toCSVString(sm.data());
			
			
			CCData.saveText(new File(App.google+"\\stock\\data\\" + stockId + "-mem.csv"), sb.toString(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
