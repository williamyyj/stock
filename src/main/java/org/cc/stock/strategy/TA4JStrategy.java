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
        this.total = 0.0;
        this.pvshareholding = 0.0;
        this.sharecash = 0.0;
        this.shareholding = 0;
        this.term = 0 ;
    }

}
