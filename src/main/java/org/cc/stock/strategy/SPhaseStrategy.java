package org.cc.stock.strategy;

import java.util.function.BiFunction;
import org.cc.json.JSONObject;
import org.cc.stock.col.SPhaseColumns;
import org.cc.stock.model.StockModel;
import org.cc.stock.util.JOStatObject;
import org.cc.text.TextUtils;

/**
 * 1. 取區間最大值的回檔數據
 * 2. sc > avg233 --> highStat
 * 3. sc < avg233 --> lowStat
 * 4. 如果股價高於均線進多頭判別規則
 * 5. 如果股價低於均線進空頭判別規則
 * @author 94017
 *
 */
public class SPhaseStrategy extends Strategy {

	private int period;
	private JOStatObject rvHighStat; 	// rvHigh 全部統計
	private JOStatObject rvLowStat; 	// rvLow 全部統計
	private BiFunction<SPhaseStrategy, JSONObject, Integer> rule;
	
	public SPhaseStrategy(StockModel sm, int period, double funds, BiFunction<SPhaseStrategy, JSONObject, Integer> rule) {
		super(sm, funds);
		this.period = period;
		System.out.println("===== period ::::: "+this.period);
		this.rule = rule;
		__init_columns();
	}
	
	@Override
	protected void __init_columns() {
		JSONObject row = sm.data().get(0);
		if (!row.has("avg"+period)) {
			new SPhaseColumns(sm, period);
		}
		rvHighStat = new JOStatObject( sm , period, (o) -> o.optDouble("rvHigh"+period));
		rvLowStat = new JOStatObject( sm , period, (o) -> o.optDouble("rvLow"+period));
	}


	// 回檔統計
	public JOStatObject getRvHighStat() {
		return rvHighStat;
	}
	
	// 回昇統計
	public JOStatObject getRvLowStat() {
		return rvLowStat;
	}
	
	@Override
	protected void proc_settle_month(JSONObject row) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 交易
	 * 1. 使用phase in [100,80] 出場 [20,0]進場
	 * 2. 使用rvLowStat 來判斷回檔
	 * 3. 使用rvHighStat 來判斷回昇
	 * 
	 */
	@Override
	protected void proc_trade(JSONObject row) {
		int status = rule.apply(this,row);
		if (status==1) {
			proc_sell_trade(row);
		} else if ( status == 0 ) {
			proc_buy_trade(row);			
		} 
	}

	@Override
	protected void proc_take_profit(JSONObject row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void proc_stop_loss(JSONObject row) {
		// TODO Auto-generated method stub
		
	}
	

	
	/**
	 * 買賣規則2
	 * phase > 80, rvLowPhase > 80 出場
	 * phase < 20, rvHighPhase < 20 進場
	 * 	
	 */
	
	
	public double getPhase(JSONObject row) {
		return  row.optDouble("phase" + period);
	}
	
	public double getHighPhase(JSONObject row) {
		double rvHigh = row.optDouble("rvHigh" + period);
		return rvHighStat.getPhase(rvHigh);
	}
	
	public double getLowPhase(JSONObject row) {
		double rvLow = row.optDouble("rvLow" + period);
		return rvLowStat.getPhase(rvLow);
	}

	@Override
	public int getPeriod() {
		return this.period;
	}

}
