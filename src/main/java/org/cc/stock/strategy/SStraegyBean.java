package org.cc.stock.strategy;

import java.util.Date;

import org.cc.json.JSONObject;

/**
 * 共用交易策略統計資料bean
 * @author 94017
 *
 */
public class SStraegyBean {
	private Date sdate ; // 結算日期
	private double fonds ; // 啟動資金
	private double netValue ; // 淨值
	private double holding ; // 持股
	private double cash_dividend ; // 現金股利
	private double stock_dividend ; // 股票股利

	public SStraegyBean(JSONObject jo) {
		super();
		this.sdate = jo.optDate("sdate");
		this.fonds = jo.getDouble("fonds");
		this.netValue = jo.getDouble("netValue");
		this.holding = jo.getDouble("holding");
		this.cash_dividend = jo.getDouble("cash_dividend");
		this.stock_dividend = jo.getDouble("stock_dividend");
	}
	
	public SStraegyBean(Date sdate, double fonds, double netValue, double holding, double cash_dividend, double stock_dividend) {
        super();
        this.sdate = sdate;
        this.fonds = fonds;
        this.netValue = netValue;
        this.holding = holding;
        this.cash_dividend = cash_dividend;
        this.stock_dividend = stock_dividend;
    }
	
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();
		jo.put("sdate", sdate);
		jo.put("fonds", fonds);
		jo.put("netValue", netValue);
		jo.put("holding", holding);
		jo.put("cash_dividend", cash_dividend);
		jo.put("stock_dividend", stock_dividend);
		return jo;
	}
	
	public String toCSV() {
        return sdate+","+fonds+","+netValue+","+holding+","+cash_dividend+","+stock_dividend;
    }
	
}
