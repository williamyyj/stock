package org.cc.stock.ta4j;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

public class TA4JBase {

    public static void bb(String title,Indicator<Num> items, int period) {
        int last = items.getBarSeries().getBarCount() - 1;
        SMAIndicator sma = new SMAIndicator(items, period);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(sma);
        StandardDeviationIndicator sd = new StandardDeviationIndicator(items, period);
        BollingerBandsUpperIndicator bb20u = new BollingerBandsUpperIndicator(bbm, sd, DecimalNum.valueOf(2));
        BollingerBandsUpperIndicator bb15u = new BollingerBandsUpperIndicator(bbm, sd, DecimalNum.valueOf(1.5));
        BollingerBandsUpperIndicator bb10u = new BollingerBandsUpperIndicator(bbm, sd, DecimalNum.valueOf(1));
        BollingerBandsUpperIndicator bb05u = new BollingerBandsUpperIndicator(bbm, sd, DecimalNum.valueOf(0.5));
        BollingerBandsLowerIndicator bb05l = new BollingerBandsLowerIndicator(bbm, sd, DecimalNum.valueOf(0.5));
        BollingerBandsLowerIndicator bb10l = new BollingerBandsLowerIndicator(bbm, sd, DecimalNum.valueOf(1));
        BollingerBandsLowerIndicator bb15l = new BollingerBandsLowerIndicator(bbm, sd, DecimalNum.valueOf(1.5));
        BollingerBandsLowerIndicator bb20l = new BollingerBandsLowerIndicator(bbm, sd, DecimalNum.valueOf(2));
        System.out.println("==============="+title+"-"+(period)+"===============");
        System.out.println("period: " + period);
        System.out.println("item: " + items.getValue(last));
        System.out.println("sma: " + sma.getValue(last));
        System.out.println("sd: " + sd.getValue(last));
        System.out.println("BB 2.0 upper: " + bb20u.getValue(last));
        System.out.println("BB 1.5 upper: " + bb15u.getValue(last));
        System.out.println("BB 1.0 upper: " + bb10u.getValue(last));
        System.out.println("BB 0.5 upper: " + bb05u.getValue(last));
        System.out.println("BB middle: " + bbm.getValue(last));
        System.out.println("BB 0.5 lower: " + bb05l.getValue(last));
        System.out.println("BB 1.0 lower: " + bb10l.getValue(last));
        System.out.println("BB 1.5 lower: " + bb15l.getValue(last));
        System.out.println("BB 2.0 lower: " + bb20l.getValue(last));
    }
}
