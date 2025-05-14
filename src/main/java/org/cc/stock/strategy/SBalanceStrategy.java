package org.cc.stock.strategy;

import java.util.Calendar;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

public class SBalanceStrategy extends Strategy {

	private double scale = 0.6; // 持股比例 70%
	
	public SBalanceStrategy(StockModel sm) {
		super(sm, 1000000);
	}



	protected void proc_trade(JSONObject row) {
		// 只有一次買入
		int idx = row.optInt("$i");
		if (idx == 0) {
			proc_buy_trade(row, this.funds*scale);
		}
	}

	public void proc_buy_trade(JSONObject row, double tradeValue) {
		this.holding = tradeValue / sm.sh(row); //
		this.funds = funds - tradeValue;
	}

	/**
	 * 結算 (月底)	
	 * 結算使用再平衡 (Rebalancing） 	
	 * netValue = funds + holding * sc
	 * funds = netValue * (1-scale)  
	 * holding = netValue * scale / sc 
	 * @param row
	 */
	@Override
	protected void proc_settle_month(JSONObject row) {
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
			this.netValue = this.funds + this.holding * row.optDouble("sc");
			this.funds = this.netValue * (1-scale);
			this.holding = this.netValue * scale / row.optDouble("sc");
			System.out.println("結算日期:" + TextUtils.df("yyyy-MM-dd", cal1.getTime())+",股價:"+ 
					row.optDouble("sc") + ",持股:" + this.holding 
					+",資金:" + this.funds
					+ ",淨值:" + this.netValue);
		}
		
	}

	@Override
	protected void proc_take_profit(JSONObject row) {

		
	}

	@Override
	protected void proc_stop_loss(JSONObject row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void __init_columns() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}



}
