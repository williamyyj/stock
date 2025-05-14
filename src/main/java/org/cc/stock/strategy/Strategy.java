package org.cc.stock.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Deque;
import java.util.LinkedList;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public abstract class Strategy {
	
	Deque<JSONObject> tradeDeque  = new LinkedList<>();
	NumberFormat nf = new DecimalFormat("0.000");
	NumberFormat nf0 = new DecimalFormat("0");
	protected StockModel sm = null;
	protected double funds = 0; // 資金
	protected double netValue = 0; // 淨值
	protected double holding = 0; // 持股
	
	public Strategy(StockModel sm, double funds) {
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
		//proc_kill_trade(lastRow);
		//proc_calc_report();
	}
	
	/**
	 * 月結算
	 * @param row
	 */
	protected abstract void proc_settle_month(JSONObject row);

	/**
	 * 交易
	 * 
	 * @param row
	 */
	protected abstract void proc_trade(JSONObject row);

	/**
	 *  停利
	 * 
	 * @param row
	 */
	protected abstract void proc_take_profit(JSONObject row);

	/**
	 * 停損
	 * 
	 * @param row
	 */
	protected abstract void proc_stop_loss(JSONObject row);

	/**
	 * 初始化欄位
	 */
	protected abstract void __init_columns();
	
	/**
	 * 買進交易 設買進價格 買進數量 設定狀態為買進 設定交易日期
	 * 如果最後一筆是賣出交易，則買進(status=0)
	 * 
	 * @param row
	 */
	protected void proc_buy_trade(JSONObject row) {
		JSONObject last = tradeDeque.peekLast();
        if (last == null || last.optInt("status") == 1) {
            double sc = row.optDouble("sc");
            double buy = sc;
             //qty = 資金 / 買進價格 且取整數
            int qty = (int) (funds / buy); // 如果多筆可使用 funds / n 
            funds -= qty * buy; // 資金 = 資金 - 買進價格 * 買進數量
            holding += qty; // 持股 = 持股 + 買進數量
            netValue = funds + holding * sc; // 淨值
            JSONObject trade = new JSONObject();
            trade.put("status", 0); // 買進
            trade.put("buy", buy);
            trade.put("qty", qty);
            trade.put("buyDate", row.optString("sdate"));
            tradeDeque.add(trade);
        }
    }

	/**
	 * 賣出交易 設定賣出價格 設定狀態為賣出 設定交易日期
	 * 如果最後一筆是買進交易，則賣出(status=1)
	 * 
	 * @param row
	 */
	public void proc_sell_trade(JSONObject row) {
		JSONObject last = tradeDeque.peekLast();
        if (last != null && last.optInt("status") == 0) {
            double sc = row.optDouble("sc");
            double sell = sc;
            double qty = last.optInt("qty");
            funds += qty * sell; // 資金 = 資金 + 賣出價格 * 買進數量
            holding -= qty; // 持股 = 持股 - 買進數量
            netValue = funds + holding * sc; // 淨值
            last.put("status", 1); // 賣出
            last.put("sell", sell);
            last.put("sellDate", row.optString("sdate"));
        }
	}
	
	/**
	 * 取到交易紀錄
	 * @return
	 */
	public Deque<JSONObject> getTradeDeque() {
         return this.tradeDeque;
    }
	
	/**
     * 取得淨值
     *	
     */
	
	public double getNetValue() {
		int idx = sm.data().size()-1;
		double sc = sm.sc(idx);
		return funds + holding * sc; // 淨值
    }
	
	public abstract int getPeriod() ;
	

	public  double getValue(JSONObject row, String id ) {
		return row.optDouble(id+getPeriod());
	}
	
	public StockModel getStockModel() {
		return sm;
	}
}
