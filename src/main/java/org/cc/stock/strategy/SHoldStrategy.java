package org.cc.stock.strategy;

import java.util.Calendar;
import java.util.Date;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

public class SHoldStrategy {

	protected StockModel sm = null;
	protected double funds = 0; // 資金
	protected double netValue = 0; // 淨值
	protected double holding = 0; // 持股
	protected int longPeriod = 0; // 長週期
	protected int shortPeriod = 0; // 短週期
	
	
	public SHoldStrategy(StockModel sm) {
		this(sm,1000000);
	}
	
	public SHoldStrategy(StockModel sm, double funds) {
		this.sm = sm;
		this.funds = funds;
	}

	public void evaluation() {
		for (int i = 0; i < sm.data().size(); i++) {
			JSONObject row = sm.data().get(i);
			proc_stop_loss(row);
			proc_take_profit(row);
			proc_trade(row);
			proc_settle_month(row);
		}
	}

	/**
	 * 交易
	 * 
	 * @param row
	 */
	protected void proc_trade(JSONObject row) {
		int idx = row.optInt("$i");
		if(idx==0) {
			// 第一天
			proc_buy_trade(row);
		}

	}
	
	/**
	 * 買進
	 * 
	 * @param row
	 */
	protected void proc_buy_trade(JSONObject row) {
		this.holding = funds / sm.sh(row);
		this.funds = 0;
		
	}
	
	/**
	 * 賣出	
	 * @param row
	 */
	protected void proc_sell_trade(JSONObject row) {

		
	}

	/**
	 * 停損
	 * 
	 * @param row
	 */
	protected void proc_stop_loss(JSONObject row) {
		// stop lo
	}

	/**
	 * 停利
	 * 
	 * @param row
	 */
	public void proc_take_profit(JSONObject row) {

	}

	/**
	 * 每月結算
	 * 
	 * @param row
	 */
	public void proc_settle_month(JSONObject row) {
		int idx = row.optInt("$i");
		int nextIdx = idx + 1;
		if(nextIdx>=sm.data().size()) {
            return;
        }
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(row.optDate("sdate"));
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(sm.data().get(nextIdx).optDate("sdate"));
		
		if (cal2.get(Calendar.MONTH) != cal1.get(Calendar.MONTH)) {
			// 結算
			this.netValue = this.holding * row.optDouble("sc")+this.funds;
			
			System.out.println("結算日期:" + TextUtils.df("yyyy-MM-dd", cal1.getTime())+",股價:"+ row.optDouble("sc") + ",持股:" + this.holding + ",淨值:" + this.netValue);
		}
		
	}

	protected void __init_columns() {
		// TODO Auto-generated method stub
		
	}

}
