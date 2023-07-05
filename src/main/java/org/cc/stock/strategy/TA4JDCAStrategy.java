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
    protected double lastPrice ;
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
            double bv = bias.getValue(i).doubleValue();
            double num = Math.floor(installment  / sh);
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

      public JSONObject eval_scale() {
        JSONObject jo = new JSONObject();
        __init_value();
        for (int i = 0; i <= last; i += period) {
            double sh = high.getValue(i).doubleValue();
            double bv = bias.getValue(i).doubleValue();
            double scale = bv>0 ? 0.5 : 1.5;
           // if (bv > 15) {
           //     scale = 0.25;
           // } else if (bv > 0 && bv <= 15) {
           //     scale = 0.75;
           // } else if (bv > -15 && bv <= 0) {
           //     scale = 1.25;
           // } else if (bv <= -15) {
           //     scale = 1.75;
           // }
            //scale=1.0;
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

    public void show() {
        System.out.println("股票代號:" + stockId + ",預期成本:" + (term * installment));
        System.out.println("總期數:" + term + ",持股:" + this.shareholding + ",均價:" + shareavg+",現價:"+lastPrice);
        System.out.println("成本:" + total + ",現值:" + pvshareholding + ",價差:" + sharecash);
        System.out.println("總獲利(%):" + pvshareholding / total * 100);
        System.out.println("平均單期(元）：" + (pvshareholding - total) / term + ",利率:" + calcInterestRate());
    }

    /**
     * 利率 = ((本利和 / 本金)^(1 / 期數)) - 1
     * 
     * @return
     */
    public double calcInterestRate() {
        double estimatedRate = Math.pow(pvshareholding / total, (1.0 / term)) - 1;
        double estimatedRatePercentage = estimatedRate * 100;
        return estimatedRatePercentage;
    }

}
