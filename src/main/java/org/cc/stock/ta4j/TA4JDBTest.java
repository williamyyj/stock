package org.cc.stock.ta4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.cc.App;
import org.cc.model.CCProcObject;
import org.cc.stock.model.TA4JModel;
import org.cc.stock.ta4j.indicators.BBHomeWidthIndicator;
import org.cc.stock.ta4j.indicators.BiasIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandWidthIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

public class TA4JDBTest extends TA4JBase {

	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		String base = System.getProperty("$base", App.base);
		String stockId = "00878";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			TA4JModel tm = new TA4JModel(proc,stockId);
			int range = 610;
			ClosePriceIndicator closePrice = new ClosePriceIndicator(tm.series());
			BiasIndicator bias = new BiasIndicator(closePrice,200);
			TA4JBase.bb(stockId+"-bias200",bias,range);
			TA4JBase.bb(stockId+"-close",closePrice,range);
			//TA4JBase.bb(stockId+"-close",closePrice,55);
			//TA4JBase.bb(stockId+"-close",closePrice,21);

			//TA4JBase.bb("0050-bias-233",bias,range);
			//stockId = "006208";
			//TA4JModel tm2 = new TA4JModel(proc,stockId);
			//closePrice = new ClosePriceIndicator(tm2.series());
			//bias = new BiasIndicator(closePrice,233);
			//TA4JBase.bb(stockId+"-bais",bias,range);
			//TA4JBase.bb("006208-bias-233",bias,range);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
