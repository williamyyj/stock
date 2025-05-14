package org.cc.stock;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.BiFunction;

import org.cc.App;
import org.cc.data.CCData;
import org.cc.fun.stock.BiPhaseRule1;
import org.cc.fun.stock.BiPhaseRule2;
import org.cc.fun.stock.BiPhaseRule3;
import org.cc.fun.stock.BiPhaseRule4;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.stock.col.IFNValue;
import org.cc.stock.col.SKDColumns;
import org.cc.stock.col.SOutputColumns;
import org.cc.stock.model.StockModel;
import org.cc.stock.strategy.SBalanceStrategy;
import org.cc.stock.strategy.SHoldStrategy;
import org.cc.stock.strategy.SPhaseStrategy;

public class SPVTest {

	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		NumberFormat nf0 = new DecimalFormat("0.0");
		IFNValue fnRV233 = (row) -> row.optDouble("rv233");
		// IFNValue fnK55 = (row)->row.optDouble("k55",50);
		String base = System.getProperty("$base", App.base);
		String stockId = "2603";
		int range = 240;
		//int range = 233;
		try (CCProcObject proc = new CCProcObject(base, false)) {
			StockModel sm = new StockModel(proc, stockId);		
			//new SKDColumns(sm, 55, 5, 5);
			//SBalanceStrategy bs = new SBalanceStrategy(sm);
			//bs.evaluation();
			SHoldStrategy hold = new SHoldStrategy(sm);
			hold.evaluation();
			
			BiFunction<SPhaseStrategy, JSONObject, Integer> rule1 = new BiPhaseRule1(97.5,2.5);
			BiFunction<SPhaseStrategy, JSONObject, Integer> rule2 = new BiPhaseRule2(95,5,10,90);
			BiFunction<SPhaseStrategy, JSONObject, Integer> rule3 = new BiPhaseRule3(40);
			BiFunction<SPhaseStrategy, JSONObject, Integer> rule4 = new BiPhaseRule4(range);
			SPhaseStrategy ps = new SPhaseStrategy(sm, range, 1000000, rule4);	
			//ps.evaluation();
			//System.out.println("high:"+ps.getRvHighStat().toString());
			//System.out.println("low :"+ps.getRvLowStat().toString());
			
		
			
	
			

			
		       

	
			// new SPhaseColumns(sm,233);

			// STaijiStrategy taiji = new STaijiStrategy(sm,55,233,1000000);
			// taiji.evaluation();

			// JSONObject jo610= FStatBase.stat(sm.data(),FStatBase.fnSC , 610,
			// sm.data().size()-1);
			// System.out.println(jo610.toString());

			// new SBBandColumns(sm,20,2);
			// new STaijiBIASColumns(sm,720,20);
			// new SFatFaceSimulationColumns(sm,500000,500000);
			// new SSARColumns(sm);
			// int range=144;
			// int smoothK = 34;
			// int smoothD = 34;

			// SBaseStrategy bs = new SBaseStrategy();

			// bs.evaluation(sm.data(),"tradek"+range , 10, 200000);
			// System.out.println("===== stockId :"+ stockId);
			// System.out.println("===== sm.data().size() :"+ sm.data.size());
			// System.out.println("===== trade num :"+bs.report().size());
			// System.out.println("===== KD ("+range+","+smoothK+","+smoothD+")");
			// for(JSONObject row: bs.report()) {
			// //System.out.println(row);
			// }

		
			
			
			
			
			// SOutputColumns out = new SOutputColumns(new String[]
			// {"sdate:date","so","sh","sl","sc","vol","paym","k21","d21","k55","k233"});
			// SOutputColumns out = new SOutputColumns(new String[]
			// {"sdate:date","sc","k21","d21","k55","k233"});
			// SOutputColumns out = new SOutputColumns(new String[]
			// {"sdate:date","sc","sa21","sah21","sal21","sa55","sah55","sal55","sa233","sah233","sal233","sa610","sah610","sal610"});
			SOutputColumns out = new SOutputColumns(
					new String[] { "sdate:date", "sc", "avg"+range, "rvHigh"+range, "rvLow"+range });
			StringBuilder sb = out.toCSVString(sm.data());
			CCData.saveText(new File(App.google + "\\stock\\data\\" + stockId + "-mem.csv"), sb.toString(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
