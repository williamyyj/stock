package org.cc.stock.ta4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.cc.App;
import org.cc.model.CCProcObject;
import org.cc.stock.model.TA4JModel;
import org.cc.stock.strategy.TA4JDCAStrategy;


public class TA4JTrading {
    
	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		String base = System.getProperty("$base", App.base);
		String stockId = "00757";
		try (CCProcObject proc = new CCProcObject(base, false)) {
            TA4JModel tm = new TA4JModel(proc, stockId);
            TA4JDCAStrategy strategy = new TA4JDCAStrategy(stockId,tm.series(),5,2500);
            strategy.evaluatetion();
            strategy.show();
           
        }  catch (Exception e) {
			e.printStackTrace();
		}
    }
}
