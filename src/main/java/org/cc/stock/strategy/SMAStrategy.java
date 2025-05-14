package org.cc.stock.strategy;

import java.util.Calendar;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

public class SMAStrategy extends Strategy {
	
	private int period = 5;
	private double price = 0.0;
	private double ratio = 10; // 賣出比率 買 -5% 賣 10%
	//private double buyRatio = 0.8; // 賣出比率
	
	public SMAStrategy(StockModel sm) {
		this(sm, 5, 21, 1000000);
	}
	
	public SMAStrategy(StockModel sm, int period, double ratio , double funds) {
		super(sm, funds);
		this.period = period;
		this.ratio = ratio;
		__init_columns();
	}
	
	protected void __init_columns() {
	
	}
	
	
	

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
			this.netValue = this.holding * row.optDouble("sc")+this.funds;
			String nv = nf.format(this.netValue);
			String high = nf.format(sm.highRatio(period, idx));
			String pv = nf.format(price*(1+ratio/100));
			
			System.out.println("結算日期:" + TextUtils.df("yyyy-MM-dd", cal1.getTime())+",股價["+ 
			row.optDouble("sc")+ ","+pv+","+ high + "],持股:" + this.holding + ",淨值:" + nv);
		}
		
	}

	@Override
	protected void proc_trade(JSONObject row) {
		int idx = row.optInt("$i");
		double sc = row.optDouble("sc");
		double pv = sm.avg(period, idx);
		//double bias =sm.bias(period, idx);
		double high = sm.highRatio(period, idx);
		 if ( (sc > pv) && (sc> (1+ratio/100)*this.price) && holding > 0) {
			// 賣出用最低點
			 this.funds = this.holding * row.optDouble("sl");
			 this.holding = 0;
			 this.netValue = this.funds+this.holding*row.optDouble("sc");
			 this.price = row.optDouble("sl"); // 賣出價
			 
			 System.out.println("賣出日期:" + TextUtils.df("yyyy-MM-dd", row.optDate("sdate"))+",股價:" + sc + ",賣出價:" + row.optDouble("sl") + ",淨值:" + this.netValue);
		} else if ( high<80 && funds > 0) { 
		    // 週期內回檔2%
			this.holding = funds / row.optDouble("sh");
			this.price = row.optDouble("sh");
			this.funds = 0;
			this.netValue = this.funds+this.holding*row.optDouble("sc");
			System.out.println("買進條件:"+ high );
			System.out.println("買進日期:" + TextUtils.df("yyyy-MM-dd", row.optDate("sdate"))+ ",股價:" + sc + ",買進價:" + row.optDouble("sh") + ",淨值:" + this.netValue);

		} else if (idx==0) {
			this.holding = funds / row.optDouble("sh");
			this.price = row.optDouble("sh");
			this.funds = 0;
			this.netValue = this.funds+this.holding*row.optDouble("sc");
			System.out.println("買進日期:" + TextUtils.df("yyyy-MM-dd", row.optDate("sdate"))+ ",股價:" + sc + ",買進價:" + row.optDouble("sh") + ",淨值:" + this.netValue);

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
	
	
	

	@Override
	protected void proc_take_profit(JSONObject row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void proc_stop_loss(JSONObject row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}
}
