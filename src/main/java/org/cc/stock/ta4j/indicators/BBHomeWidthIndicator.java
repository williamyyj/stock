package org.cc.stock.ta4j.indicators;

import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

public class BBHomeWidthIndicator extends CachedIndicator<Num> {

	private final BollingerBandsMiddleIndicator bbm;
	private final StandardDeviationIndicator deviation;
	private final ClosePriceIndicator close;
	private final Num k1;
	private final Num k2;

	/**
	 * 
	 * @param bbm
	 * @param deviation
	 * @param k
	 */
	public BBHomeWidthIndicator(BollingerBandsMiddleIndicator bbm, StandardDeviationIndicator deviation, Num k) {
		super(bbm.getBarSeries());
		this.bbm = bbm;
		this.deviation = deviation;
		this.close = new ClosePriceIndicator(getBarSeries());
		this.k1 = k.multipliedBy(bbm.getBarSeries().numOf(100));
		this.k2 = k.multipliedBy(bbm.getBarSeries().numOf(-100));
	}

	@Override
    protected Num calculate(int index) {
    	if(close.getValue(index).isGreaterThan(bbm.getValue(index))) {
    		return deviation.getValue(index).dividedBy(bbm.getValue(index)).multipliedBy(k1);
    	} else {
    		return deviation.getValue(index).dividedBy(bbm.getValue(index)).multipliedBy(k2);
    	}
    }
}