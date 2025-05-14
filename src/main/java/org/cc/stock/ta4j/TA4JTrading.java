package org.cc.stock.ta4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.stock.model.TA4JModel;
import org.cc.stock.strategy.TA4JDCABBStrategy;
import org.cc.stock.strategy.TA4JDCARateStrategy;
import org.cc.stock.strategy.TA4JDCAStrategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;


public class TA4JTrading {
    
	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		String dp = App.google+"\\mydata\\stock\\twotc";
		System.out.println("===== dp : "+dp);
		String base = System.getProperty("$base", App.base);

		String stockId = "00713";
		try (CCProcObject proc = new CCProcObject(base, false)) {
            TA4JModel tm = new TA4JModel(proc, stockId);
            //TA4JModel tm = new TA4JModel(dp,stockId);

                                           //TA4JDCARateStrategy
			TA4JDCAStrategy strategy = new TA4JDCAStrategy(stockId,tm.series(),5,10000);
			strategy.evaluatetion();
			strategy.show();
			System.out.println("------------------------------------");
			//strategy = new TA4JDCAStrategy(stockId,tm.series(),22,10000);
			//strategy.eval_scale();
			//strategy.show();

			

			
			//System.out.println("=============== begin : 100 ======================");
			//strategy.evaluatetion(100,100);
            //strategy.show();
			
           
        }  catch (Exception e) {
			e.printStackTrace();
		}
    }
}
