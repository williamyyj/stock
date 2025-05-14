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
	protected int period = 0; // 週期用233交易日
	protected double total = 0.0;
	protected double initFunds = 0; //
	protected double netValue = 0; //
	protected double holding = 0; //
	protected double funds = 0; //
	//protected int nextTrade = 

	// protected double allholding = 0;
	// protected double allholding = 0;
	// protected double allholding = 0;

	public SBaseStrategy(StockModel sm, int period, int division, double funds) {
		this.tradeDeque = new ArrayDeque<>(32);
		this.rpt = new ArrayList<>();
		this.period = period;
		this.division = division;
		this.initFunds = funds;
	}

	public SBaseStrategy(StockModel sm) {
		this(sm, 233, 10, 1000000);
	}

	public void evaluation() {
		for (int i = 0; i < sm.data().size(); i++) {
			JSONObject row = sm.data().get(i);
			proc_trade(row); // 交易

		}
	}

	private void proc_trade(JSONObject row) {
		int idx = row.optInt("$i");
		if (idx == 0) {
			// 第一天
			proc_buy_trade(row);
		}

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
			proc_stop_loss_trade(item, curr);
			if ("loss".equals(item.optString("status"))) {
				i.remove();
			}
		}

	}

	private void proc_stop_loss_trade(JSONObject item, JSONObject curr) {
		double buy = item.optDouble("buy");
		double sell = curr.optDouble("sc");
		double bias = (sell - buy) / buy;
		if (bias < -0.05) {
			proc_sell_trade(item, curr, "loss");
		}

	}

	/**
	 * 停利點（Take Profit Point）處理
	 */
	public void proc_take_profit(JSONObject curr) {
		Iterator<JSONObject> i = tradeDeque.iterator();
		while (i.hasNext()) {
			JSONObject item = i.next();
			proc_take_profit_trade(item, curr);
			if ("profit".equals(item.optString("status"))) {
				i.remove();
			}
		}
	}

	private void proc_take_profit_trade(JSONObject item, JSONObject curr) {
		double buy = item.optDouble("buy");
		double sell = curr.optDouble("sc");
		double bias = (sell - buy) / buy;
		if (bias > 0.2) {
			proc_sell_trade(item, curr, "profit");
		}

	}

	public void proc_buy_trade(JSONObject row) {
		if (tradeDeque.size() < this.division) {
	

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
