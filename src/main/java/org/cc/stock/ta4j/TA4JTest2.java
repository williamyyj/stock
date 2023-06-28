package org.cc.stock.ta4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

import org.cc.App;
import org.cc.model.CCProcObject;
import org.cc.stock.model.TA4JModel;
import org.cc.stock.ta4j.indicators.BBHomeWidthIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorDIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.AnalysisCriterion;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Position;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.criteria.pnl.AverageProfitCriterion;
import org.ta4j.core.criteria.pnl.ReturnCriterion;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

public class TA4JTest2 {

	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		String base = System.getProperty("$base", App.base);
		String stockId = "00878";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			TA4JModel tm = new TA4JModel(proc, stockId);
			Strategy strategy = buildStrategy2(tm.series());
			BarSeriesManager seriesManager = new BarSeriesManager(tm.series());
			TradingRecord tradingRecord = seriesManager.run(strategy);
			System.out.println("Number of positions for the strategy: " + tradingRecord.getPositionCount());
			Num profit = DecimalNum.valueOf(0);
			for (int i = 0; i < tradingRecord.getPositionCount(); i++) {
				Position pos = tradingRecord.getPositions().get(i);
				System.out.println("Trade " + (i + 1) + ":");
				int bIdx = pos.getEntry().getIndex();
				System.out.println("  Entry time: " + tm.series().getBar(bIdx).getSimpleDateName());
				System.out.println("  Entry price: " + pos.getEntry().getNetPrice());
				System.out.println("  Entry amount: " + pos.getEntry().getAmount());
				int eIdx = pos.getExit().getIndex();
				System.out.println("  Exit time: " + tm.series().getBar(eIdx).getSimpleDateName());
				System.out.println("  Exit price: " + pos.getExit().getNetPrice());
				System.out.println("  Exit amount: " + pos.getExit().getAmount());
				System.out.println("  Profit: " + pos.getProfit());
				profit = profit.plus(pos.getProfit());
			}
			System.out.println("Total Profit: " + profit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * macd +k +sma
	 * @param series
	 * @return
	 */
	public static Strategy buildStrategy(BarSeries series) {
		if (series == null) {
			throw new IllegalArgumentException("Series cannot be null");
		}

		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

		// The bias is bullish when the shorter-moving average moves above the longer
		// moving average.
		// The bias is bearish when the shorter-moving average moves below the longer
		// moving average.
		EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
		EMAIndicator longEma = new EMAIndicator(closePrice, 26);

		StochasticOscillatorKIndicator stochasticOscillK = new StochasticOscillatorKIndicator(series, 14);

		MACDIndicator macd = new MACDIndicator(closePrice, 9, 26);
		EMAIndicator emaMacd = new EMAIndicator(macd, 18);

		// Entry rule
		Rule entryRule = new OverIndicatorRule(shortEma, longEma) // Trend
				.and(new CrossedDownIndicatorRule(stochasticOscillK, 20)) // Signal 1
				.and(new OverIndicatorRule(macd, emaMacd)); // Signal 2

		// Exit rule
		Rule exitRule = new UnderIndicatorRule(shortEma, longEma) // Trend
				.and(new CrossedUpIndicatorRule(stochasticOscillK, 80)) // Signal 1
				.and(new UnderIndicatorRule(macd, emaMacd)); // Signal 2

		return new BaseStrategy(entryRule, exitRule);
	}
	
	/**
	 * BB 610 day
	 * @param series
	 * @return
	 */
	public static Strategy buildStrategy2(BarSeries series) {
		if (series == null) {
			throw new IllegalArgumentException("Series cannot be null");
		}

		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		SMAIndicator sma = new SMAIndicator(closePrice, 233);
		BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(sma);
		StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 233);
		BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd, DecimalNum.valueOf(1));
		BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd, DecimalNum.valueOf(1));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for(int i=0; i<series.getBarCount();i++) {
			Bar bar = series.getBar(i);
			String d = bar.getEndTime().format(formatter);
			System.out.println(d+","+bar.getClosePrice()+","+bbu.getValue(i)+","+bbl.getValue(i));
		}


		// Entry rule
		Rule entryRule = new UnderIndicatorRule(closePrice, bbl) ;


		// Exit rule
		Rule exitRule = new OverIndicatorRule(closePrice, bbu) ; 
		return new BaseStrategy(entryRule, exitRule);
	}

}
