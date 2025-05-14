package org.cc.stock.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;

public class TA4JStrategy {
    
    protected String stockId;
    protected BarSeries series;
	protected double total = 0.0; // 總資金
	protected double installment  = 0; // 分期付款
	protected double netValue = 0; // 淨值
	protected int shareholding = 0; //持股
    protected double shareavg = 0.0; // 平均股價
    protected double sharecash = 0; //持現
    protected double pvshareholding; // Present value of shareholding 持股現值
    protected double lastPrice ;
    
    protected int term =0; // 每期
    protected int last; // 最後一筆
    protected HighPriceIndicator high; // 買價(當日一定買的到)
    protected LowPriceIndicator low; // 賣價(當日一定賣的出)
    protected ClosePriceIndicator close; // 評價
      

    public TA4JStrategy(String stockId, BarSeries series){
        this.stockId = stockId;
        this.series = series;
        this.high = new HighPriceIndicator(this.series);
        this.low = new LowPriceIndicator(this.series);
        this.close = new ClosePriceIndicator(this.series);
        this.lastPrice = close.getValue(series.getBarCount()-1).doubleValue();
    }

    public void show() {
        System.out.println("股票代號:" + stockId + ",預期成本:" + (term * installment));
        System.out.println("總期數:" + term + ",持股:" + this.shareholding + ",均價:" + shareavg+",現價:"+lastPrice);
        System.out.println("成本:" + total + ",現值:" + pvshareholding + ",價差:" + sharecash);
        System.out.println("總獲利(%):" + pvshareholding / total * 100);
        System.out.println("單期獲利(元）：" + (pvshareholding - total) / term + ",利率:" + calcInterestRate());
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
