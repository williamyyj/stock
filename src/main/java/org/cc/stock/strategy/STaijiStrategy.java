package org.cc.stock.strategy;

import java.util.Date;

import org.cc.json.JSONObject;
import org.cc.stock.col.SPhaseColumns;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

public class STaijiStrategy extends SHoldStrategy {
	
	protected String idsa = "sa";
	protected String idsh = "sh";
	protected String idsl = "sl";
	
	public STaijiStrategy(StockModel sm) {
		this(sm,5,21,1000000);
	}
	
	public STaijiStrategy(StockModel sm, int shortPeriod, int longPeriod, double funds) {
		super(sm,funds);
		this.shortPeriod = shortPeriod;
		this.longPeriod = longPeriod;
		JSONObject row = sm.data().get(0);
		if(!row.has("sa"+longPeriod)) {
			new SPhaseColumns(sm,longPeriod);
		}
		idsa = "sa"+longPeriod;
		idsh = "sh"+longPeriod;
		idsl = "sl"+longPeriod;
		
	}
	
	
	/**
	 * 交易
	 * 
	 * @param row
	 */
	@Override
	protected void proc_trade(JSONObject row) {
		int idx = row.optInt("$i");
		double sc = row.optDouble("sc");
		double shortValue = sm.avg(shortPeriod,idx);
		//double smax =  row.optDouble(idsa
		double sa = row.optDouble(idsa);
		double sh = row.optDouble(idsh); 
		double sl = row.optDouble(idsl); 
		
		String status= (sa>shortValue)?"多頭":"空頭";
	
		if (funds > 0 && "多頭".equals(status) && true ) {
			proc_buy_trade(row,sa,shortValue);
		} else if (shortValue < sa && holding > 0) {
			proc_sell_trade(row,sa,shortValue);
		}
	}
	
	/**
	 * 買進
	 * 
	 * @param row
	 */
	protected void proc_buy_trade(JSONObject row, double sa, double shortValue) {
		this.holding = funds / row.optDouble("sh");
		this.funds = 0;
		System.out.println(show_net_value("買進",row,sa,shortValue));
	}
	
	/**
	 * 賣出	
	 * @param row
	 */
	protected void proc_sell_trade(JSONObject row,double sa,double shortValue) {
		this.funds = holding * row.optDouble("sl");
		this.holding = 0.0;
		System.out.println(show_net_value("賣出",row,sa,shortValue));
	}
	
	public String show_net_value(String label,JSONObject row,double sa,double shortValue) {		
		return label+":" + 
				TextUtils.df("yyyy-MM-dd", row.optDate("sdate"))+
				",股價:"+ row.optDouble("sc") + 
				",short:"+ row.optDouble("sh") +
				",long:"+ row.optDouble("sl") +
				",持股:" + this.holding +
				",淨值:" + this.funds;
	}


}
