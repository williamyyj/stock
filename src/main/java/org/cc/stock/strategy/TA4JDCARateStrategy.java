package org.cc.stock.strategy;

import org.cc.json.JSONObject;
import org.ta4j.core.BarSeries;

/**
 * 1. 由DCA 算出當期殖利率
 * 2.
 */
public class TA4JDCARateStrategy extends TA4JDCAStrategy {

    public TA4JDCARateStrategy(String stockId, BarSeries series, int period, double installment) {
        super(stockId, series, period, installment);
    }

  public JSONObject eval_rate(int begin, int periodFunds, double rate) {
        JSONObject jo = new JSONObject();
        __init_value();
        double valuation = 0.0 ;
         double termValue = 0.0;
        for (int i = begin*period; i <= last; i += period) {
            if (periodFunds > 0) {
                double sh = high.getValue(i).doubleValue();
                double bv = bias.getValue(i).doubleValue();
                double num = Math.floor(installment / sh);
                shareholding += num;
                termValue = Math.floor(num * sh);
                total += termValue;
                pvshareholding = Math.floor(shareholding * close.getValue(i).doubleValue());
                shareavg = total / shareholding;
                periodFunds--;
            } else {
                pvshareholding = Math.floor(shareholding * close.getValue(i).doubleValue());
                termValue = 0.0;
            }
            valuation = (valuation + termValue)*rate;
            System.out.println( (term+1)+"-->"+total +","+pvshareholding+ "," + valuation);
            term++;
        }
        double lastPrice = close.getValue(last).doubleValue();
        pvshareholding = shareholding*lastPrice;
        jo.put("term", term);
        jo.put("shareholding", shareholding);
        jo.put("total", total);
        jo.put("pvshareholding", shareholding*lastPrice);
        jo.put("shareavg", shareavg);
        jo.put("lastPrice", lastPrice);
        return jo;
    }

}
