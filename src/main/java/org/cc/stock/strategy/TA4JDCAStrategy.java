package org.cc.stock.strategy;

import org.cc.json.JSONObject;
import org.cc.stock.ta4j.indicators.BiasIndicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;

/**
 * Dollar Cost Averaging
 * 定期定額只買不出
 */
public class TA4JDCAStrategy extends TA4JStrategy {

    protected int period; // 周期 5週 20月 55 季
    private double sellRatio;
    private double buyRatio;
    protected BiasIndicator bias;


    public TA4JDCAStrategy(String stockId, BarSeries series) {
        this(stockId, series, 5, 2500);
    }

    public TA4JDCAStrategy(String stockId, BarSeries series, int period, double installment) {
        super(stockId, series);
        this.period = period;
        this.installment = installment;
        last = this.series.getBarCount() - 1;
        this.sellRatio = 1.2;
        this.buyRatio = 0.8;
        this.lastPrice = close.getValue(last).doubleValue();
        bias = new BiasIndicator(close, 200);
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
            double num = Math.floor(installment  / sh);
           
            shareholding += num;
            total += Math.floor(num * sh);
            pvshareholding = Math.floor(shareholding * close.getValue(i).doubleValue());
            shareavg = total / shareholding;
            sharecash = close.getValue(i).doubleValue()-shareavg;
            // proc_sell_strategy(i);
            // proc_buy_strategy(i);
            term++;
        }
        jo.put("term", term);
        jo.put("shareholding", shareholding);
        jo.put("total", total);
        jo.put("pvshareholding", pvshareholding);
        jo.put("shareavg", shareavg);
        jo.put("price", lastPrice);
        jo.put("sharecash", sharecash);
        return jo;
    }

      public JSONObject eval_scale() {
        JSONObject jo = new JSONObject();
        __init_value();
        for (int i = 0; i <= last; i += period) {
            double sh = high.getValue(i).doubleValue();
            double bv = bias.getValue(i).doubleValue();
            double scale = 1;
            if (bv >= 15) {
                scale = 0.25;  // 0.75
            } else if (bv < 15 && bv >= 5) {
                scale = 2; // 1
            } else if (bv < 5 && bv >= -5 ) {
            	scale = 4;
           } else if (bv < -5 && bv <= -15) {
                scale = 8;  // 1 
           } else {
                scale = 16; // 1.5 
            }
            scale = bv>0 ? 0.5: 2;
            //scale=1.0;  標準定期定額
            double num = Math.floor(installment * scale / sh);
            shareholding += num;
            total += Math.floor(num * sh);
            pvshareholding = Math.floor(shareholding * close.getValue(i).doubleValue());
            shareavg = total / shareholding;
            // proc_sell_strategy(i);
            // proc_buy_strategy(i);
            term++;
        }
        jo.put("term", term);
        jo.put("shareholding", shareholding);
        jo.put("total", total);
        jo.put("pvshareholding", pvshareholding);
        jo.put("shareavg", shareavg);
        jo.put("price", lastPrice);
        return jo;
    }

    public JSONObject evaluatetion(int begin, int periodFunds) {
        JSONObject jo = new JSONObject();
        __init_value();
        for (int i = begin*period; i <= last; i += period) {
            if (periodFunds > 0) {
                double sh = high.getValue(i).doubleValue();
                double bv = bias.getValue(i).doubleValue();
                double num = Math.floor(installment / sh);
                shareholding += num;
                total += Math.floor(num * sh);
                pvshareholding = Math.floor(shareholding * close.getValue(i).doubleValue());
                shareavg = total / shareholding;
                periodFunds--;
            }
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

    private void proc_buy_strategy(int i) {
        double sv = high.getValue(i).doubleValue();
        double currcash = shareholding * shareavg + sharecash;
        if ((pvshareholding / currcash) < buyRatio && sharecash > 0) {
            System.out.println("-------------buy strategy ----------------------------");
            System.out.println("currcash :" + currcash + "," + pvshareholding + "-->" + pvshareholding / currcash);
            System.out.println("before :" + total + "-->" + pvshareholding + "," + sharecash + "," + shareholding + ","
                    + shareavg);
            int adjnum = (int) ((1 - pvshareholding / currcash) * shareholding); // 調整股
            int adjcash = (int) (adjnum * sv); // 使用單日最高買入
            if (adjcash > sharecash) {
                adjcash = (int) sharecash;
                adjnum = (int) (adjcash / sv);
            }
            sharecash = sharecash - adjcash;
            shareholding = shareholding + adjnum;
            total = shareholding * shareavg;
            pvshareholding = Math.floor(shareholding * close.getValue(i).doubleValue());
            System.out.println("after  :" + total + "-->" + pvshareholding + "," + sharecash + "," + shareholding + ","
                    + shareavg);
        }

    }

    private void proc_sell_strategy(int i) {
        double sv = low.getValue(i).doubleValue();
        double currcash = shareholding * shareavg + sharecash;

        if ((pvshareholding / currcash) > sellRatio) {
            System.out.println("-------------sell strategy ----------------------------");
            System.out.println("currcash :" + currcash);
            System.out.println("before :" + total + "-->" + pvshareholding + "," + sharecash + "," + shareholding + ","
                    + shareavg);
            int adjnum = (int) ((1 - currcash / pvshareholding) * shareholding) / 2; // 調整股
            int adjcash = (int) (adjnum * sv); // 使用單日最低賣出
            // total = total - adjcash;
            sharecash = sharecash + adjcash;
            shareholding = shareholding - adjnum;
            total = shareholding * shareavg;
            pvshareholding = Math.floor(shareholding * close.getValue(i).doubleValue());
            System.out.println("after  :" + total + "-->" + pvshareholding + "," + sharecash + "," + shareholding + ","
                    + shareavg);
        }
    }

  

}
