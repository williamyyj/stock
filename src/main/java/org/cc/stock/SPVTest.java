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
import org.cc.stock.col.SBBandColumns;
import org.cc.stock.col.SFatFaceSimulationColumns;
import org.cc.stock.col.SKDColumns;
import org.cc.stock.col.SOBVColumns;
import org.cc.stock.col.SOutputColumns;
import org.cc.stock.col.SSARColumns;
import org.cc.stock.model.StockModel;
import org.cc.stock.strategy.SBaseStrategy;
import org.cc.text.TextUtils;

public class SPVTest {
	
	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		IFNValue fnK21 = (row)->row.optDouble("k21",50);
		IFNValue fnK55 = (row)->row.optDouble("k55",50);
		IFNValue fnK233 = (row)->row.optDouble("k233",50);
		String base = System.getProperty("$base", App.base);
		String stockId = "00878";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			StockModel sm = new StockModel(proc, stockId);
			System.out.println(sm.avg(610,sm.data().size()-1));
			JSONObject jo610= FStatBase.stat(sm.data(),FStatBase.fnSC , 610, sm.data().size()-1);
			System.out.println(jo610.toString(4));
			//new SBBandColumns(sm,20,2);
			//new STaijiBIASColumns(sm,720,20);
			//new SFatFaceSimulationColumns(sm,500000,500000);
			//new SSARColumns(sm);
			//int range=144;
			//int smoothK = 34;
			//int smoothD = 34;
			//new SKDColumns(sm,range,smoothK,smoothK).evaluation();
			//SBaseStrategy bs = new SBaseStrategy();
			
			//bs.evaluation(sm.data(),"tradek"+range , 10, 200000);
			//System.out.println("===== stockId :"+ stockId);
			//System.out.println("===== sm.data().size() :"+ sm.data.size());
			//System.out.println("===== trade num        :"+bs.report().size());
			//System.out.println("===== KD ("+range+","+smoothK+","+smoothD+")");
			//for(JSONObject row: bs.report()) {
			//	//System.out.println(row);
			//}
			
			for(int i=0; i<sm.data().size();i++) {
				//JSONObject row = sm.data().get(i);
				//System.out.println(row);
				//System.out.println(row.optString("tradek21"));
				//System.out.println(row.opt("hh21"));
				//System.out.println(row.opt("hh55"));
				//System.out.println(row.opt("hh233"));
				//double obv10 = sm.avg("sobv", 21, i);
				//row.put("obv1",obv10);
			}
			//SOutputColumns out = new SOutputColumns(new String[] {"sdate:date","so","sh","sl","sc","vol","paym","k21","d21","k55","k233"});
			//SOutputColumns out = new SOutputColumns(new String[] {"sdate:date","sc","k21","d21","k55","k233"});			
			//StringBuilder sb = out.toCSVString(sm);
			//CCData.saveText(new File(App.google+"\\stock\\data\\" + stockId + "-mem.csv"), sb.toString(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
