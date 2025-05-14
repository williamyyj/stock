package org.cc.stock.strategy;

import org.cc.json.JSONObject;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DecimalNum;

/**
 * DCA (Dollar Cost Averaging) + BB
 */
public class TA4JDCABBStrategy extends TA4JStrategy {

    int period = 0;
    int bbPeriod = 0;
    SMAIndicator sma = null;
    BollingerBandsMiddleIndicator bbm = null;
    StandardDeviationIndicator sd = null;

    public TA4JDCABBStrategy(String stockId, BarSeries series) {
        this(stockId, series, 5, 55, 2500);
    }

    public TA4JDCABBStrategy(String stockId, BarSeries series, int period, int bbPeriod, double installment) {
        super(stockId, series);
        this.period = period;
        this.bbPeriod = bbPeriod;
        this.installment = installment;
        this.last = this.series.getBarCount() - 1;
        sma = new SMAIndicator(close, bbPeriod);
        bbm = new BollingerBandsMiddleIndicator(sma);
        sd = new StandardDeviationIndicator(close, bbPeriod);
    }

    protected void __init_value() {
        this.total = 0.0;
        this.pvshareholding = 0.0;
        this.sharecash = 0.0;
        this.shareholding = 0;
        this.term = 0;
    }

    /**
     * 定期定額直到最後
     * 
     * @return
     */
    public JSONObject evaluatetion() {
        JSONObject jo = new JSONObject();
        __init_value();
        for (int i = 0; i <= last; i += period) {
            double sh = high.getValue(i).doubleValue();
            double scale = scale(i);
            scale=1.0;
            double num = Math.floor(installment / sh) * scale;
            shareholding += num;
            total += Math.floor(num * sh);
            pvshareholding = Math.floor(shareholding * close.getValue(i).doubleValue());
            shareavg = total / shareholding;
            term++;
        }

        jo.put("term", term);
        jo.put("shareholding", shareholding);
        jo.put("total", total);
        jo.put("pvshareholding", pvshareholding);
        jo.put("shareavg", shareavg);
        jo.put("price", close.getValue(last));
        return jo;
    }

    private double scale(int i) {
        double vsd  = sd.getValue(i).doubleValue();
        double vma  = sma.getValue(i).doubleValue();
        double vsc = close.getValue(i).doubleValue();
        double up2 = vsd+2*vma ;
        double up1 = vsd+vma ;
        double low1 = vsd-vma;
        double low2 = vsd-2*vma;
        if(vsc>=up2) {
            return 0.5;
        } else if(up2>vsc && vsc>=up1){
            return 1;
        } else if(up1>vsc && vsc>=vma){
            return 1;
        } else if( vma>vsc && vsc > low1){
            return 1.0 ;
        } else if( low1>vsc && vsc> low2){
            return 1.5;
        } else if (low2>vsc){
            return 1.5;
        }
        return 0;
    }

}
