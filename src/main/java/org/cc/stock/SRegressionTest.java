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
import org.cc.stock.col.SRegressionColumns;
import org.cc.stock.col.SStatLogColumns;
import org.cc.stock.col.STaijiBIASColumns;
import org.cc.stock.col.STaijiCircleColumns;
import org.cc.stock.col.SVPColumns;
import org.cc.stock.col.SWaveColumns;
import org.cc.stock.model.SMonthModel;
import org.cc.text.TextUtils;

public class SRegressionTest {


	
	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		String base = System.getProperty("$base", App.base);
		String stockId = "00878";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			SMonthModel sm = new SMonthModel(proc, stockId);
			//new SVPColumns(sm);
			int range = 610  ;
			new SRegressionColumns(sm,1,range);
			//new SRegressionColumns(sm,20);
			//new SKDColumns(sm,20,5,5);
			//new SStatLogColumns(sm,240,20);
			new SWaveColumns(sm,27);
			JSONObject p = null ;
			for(int i=0; i<sm.data().size();i++) {
				JSONObject row = sm.data().get(i);

				System.out.println(TextUtils.df("yyyy-MM-dd",row.opt("sdate"))+":"+row.optDouble("sc")+","
					+row.optDouble("ra"+String.valueOf(range))+","+row.optDouble("rb"+String.valueOf(range))
				);
				//double r20 = row.optDouble("rb20")-row.optDouble("rb200");
				//row.put("r20", r20);
			}
			//SOutputColumns out = new SOutputColumns(new String[] {"sdate:date","so","sh","sl","sc","vol","k","d","kd:string"});
			SOutputColumns out = new SOutputColumns(new String[] {"sdate:date","sc","ra55","rb55"});
			//SOutputColumns out = new SOutputColumns(new String[] {"sdate:date","sc","bsa20","bsh20","bsl20","bsv20"});
			StringBuilder sb = out.toCSVString(sm.data());	
			CCData.saveText(new File(App.google+"\\stock\\data\\" + stockId + "-mem.csv"), sb.toString(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
