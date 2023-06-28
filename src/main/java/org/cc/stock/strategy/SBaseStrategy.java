package org.cc.stock.strategy;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SBaseStrategy {

	protected ArrayDeque<JSONObject> tradeDeque;
	protected List<JSONObject> rpt;
	protected StockModel sm;
	protected int division = 0;
	protected double total = 0.0;
	protected double funds = 0; //
	protected double netValue = 0; //
	protected double holding = 0; //
	// protected double allholding = 0;
	// protected double allholding = 0;
	// protected double allholding = 0;

	public SBaseStrategy() {
		this.tradeDeque = new ArrayDeque<>(10);
		this.rpt = new ArrayList<>();
	}

	public void evaluation(List<JSONObject> rows, String tradeId, int division, double funds) {
		this.funds = funds;
		this.division = division;
		total = 0.0;
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.get(i);
			String trade = row.optString(tradeId);
			//proc_stop_loss(row);
			//proc_take_profit(row);
			proc_dividend_trade(row);
			switch (trade) {
			case "B":
				proc_buy_trade(row);
				break; // 買一單
			case "S":
				proc_sell_trade(row);
				break; // 賣一單
			}
		}
		JSONObject lastRow = rows.get(rows.size() - 1);
		proc_kill_trade(lastRow);
		proc_calc_report();
	}

	public void proc_kill_trade(JSONObject last) {
		while (tradeDeque.size() > 0) {
			JSONObject item = tradeDeque.removeFirst();
			proc_sell_trade(item, last, "kill"); // 結算or清單
		}
	}

	public void proc_dividend_trade(JSONObject row) {

	}

	protected void proc_calc_report() {
		for (JSONObject item : rpt) {
			total += item.optDouble("amount");
		}
		System.out.println("===== total:" + total);
	}

	/**
	 * 停損點（Stop-loss point）處理
	 */
	public void proc_stop_loss(JSONObject curr) {
		Iterator<JSONObject> i = tradeDeque.iterator();
		while (i.hasNext()) {
			JSONObject item = i.next();
			proc_stop_loss_trade(item,curr);
			if("loss".equals(item.optString("status"))) {
				i.remove();
			}
		}

	}

	private void proc_stop_loss_trade(JSONObject item, JSONObject curr) {
		double buy = item.optDouble("buy");
		double sell = curr.optDouble("sc");
		double bias =  (sell-buy)/buy;
		if(bias<-0.05) {
			proc_sell_trade(item,curr,"loss");
		}
		
	}

	/**
	 * 停利點（Take Profit Point）處理
	 */
	public void proc_take_profit(JSONObject curr) {
		Iterator<JSONObject> i = tradeDeque.iterator();
		while (i.hasNext()) {
			JSONObject item = i.next();
			proc_take_profit_trade(item,curr);
			if("profit".equals(item.optString("status"))) {
				i.remove();
			}
		}
	}
	
	private void proc_take_profit_trade(JSONObject item, JSONObject curr) {
		double buy = item.optDouble("buy");
		double sell = curr.optDouble("sc");
		double bias =  (sell-buy)/buy;
		if(bias>0.2) {
			proc_sell_trade(item,curr,"profit");
		}
		
	}


	public void proc_buy_trade(JSONObject row) {
		if (tradeDeque.size() <= division) {
			JSONObject item = new JSONObject();
			double sc = row.optDouble("sc");
			item.put("bdate", row.opt("sdate"));
			item.put("buy", sc);
			double num = Math.floor(funds / sc);
			item.put("num", num);
			item.put("status", "buy");
			tradeDeque.add(item);
		}
	}

	public void proc_sell_trade(JSONObject row) {
		// 先不考量停損及停利和配息
		if (tradeDeque.size() > 0) {
			JSONObject item = tradeDeque.removeFirst();
			proc_sell_trade(item, row, "sell");
		}
	}

	public void proc_sell_trade(JSONObject item, JSONObject row, String status) {
		double sell = row.optDouble("sc");
		item.put("edate", row.opt("sdate"));
		item.put("sell", sell);
		double buy = item.optDouble("buy");
		double num = item.optDouble("num");
		double amount = num * (sell - buy);
		item.put("amount", amount);
		item.put("status", status);
		rpt.add(item);
	}

	public List<JSONObject> report() {
		return this.rpt;
	}

}
