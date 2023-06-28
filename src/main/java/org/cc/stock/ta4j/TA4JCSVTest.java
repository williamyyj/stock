package org.cc.stock.ta4j;

import org.cc.App;
import org.cc.stock.model.TA4JModel;
import org.cc.stock.ta4j.indicators.BiasIndicator;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;



public class TA4JCSVTest extends TA4JBase {

	public static void main(String[] args) {
		// Create bar series
		String dp = App.google+"\\mydata\\stock\\twotc";
		System.out.println("===== dp : "+dp);
		TA4JModel ta = new TA4JModel(dp,"00888");
		// Define technical indicators
		ClosePriceIndicator closePrice = new ClosePriceIndicator(ta.series());
		BiasIndicator bias = new BiasIndicator(closePrice,610);
		TA4JBase.bb("close",closePrice,610);
		TA4JBase.bb("bias",bias,610);
	}


}
