package org.cc.stock.ta4j.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

public class BiasIndicator extends CachedIndicator<Num> {
    
    private final Indicator<Num> closePrice;
    private final SMAIndicator sma;
    
    public BiasIndicator(Indicator<Num> closePrice, int smaPeriod) {
        super(closePrice.getBarSeries());
        this.closePrice = closePrice;
        this.sma = new SMAIndicator(closePrice, smaPeriod);
    }
    
    @Override
    protected Num calculate(int index) {
        // 計算乖離率
        Num closePriceValue = closePrice.getValue(index);
        Num smaValue = sma.getValue(index);
        return closePriceValue.minus(smaValue).dividedBy(smaValue).multipliedBy(DecimalNum.valueOf(100));
    }
}