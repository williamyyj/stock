package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

/**
 * 除息 前15天10次交易 除息 後05天10次交易 除息 後05天配息再投入 每月 第一個交易日評價
 * 
 * @author 94017
 *
 */
public class SFatFaceSimulationColumns extends SBaseColumns {

	private double funds1 = 0;
	private double funds2 = 0;
	private double allholding = 0;

	public SFatFaceSimulationColumns(StockModel sm) {
		this(sm, 500000, 500000);
	}

	/**
	 * 
	 * @param sm
	 * @param funds1 --> 一次投入
	 * @param funds2 --> 加碼金
	 */
	public SFatFaceSimulationColumns(StockModel sm, double funds1, double funds2) {
		super(sm);
		this.funds1 = funds1;
		this.funds2 = funds2;
		this.allholding = 0;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		proc_init_funds1();
		for (int i = 0; i < sm.data().size(); i++) {
			JSONObject row = sm.data().get(i);
			double paym = row.optDouble("paym");
			if (paym > 0) {
				proc_buy_funds2(i);
				proc_buy_paym(i);
				proc_sell_funds2(i);
			}
		}
	}

	private void proc_buy_funds2(int eIdx) {
		int bIdx = eIdx - 15 + 1;
		double part = funds2 / 10;
		for (int i = 0; i < 10; i++) {
			proc_buy_stock(bIdx + i, part, "加碼");
		}
	}

	private void proc_buy_paym(int eIdx) {
		JSONObject pay = sm.data().get(eIdx);
		pay.put("allholding", allholding); // inject 現持股票
		int bIdx = eIdx + 4 ;
		if (bIdx < sm.data().size()) {
			double paym = pay.optDouble("paym") * pay.optDouble("allholding");
			proc_buy_stock(bIdx, paym, "配息");
		}
	}

	private void proc_sell_funds2(int bIdx) {
		int eIdx = bIdx + 5;
		double part = funds2 / 10;
		for (int i = 0; i < 10; i++) {
			proc_sell_stock(eIdx + i, part, "加碼");
		}
	}

	private void proc_buy_stock(int i, double part, String holdType) {
		JSONObject row = sm.data().get(i);
		double sc = row.optDouble("sc");
		double value = Math.floor(part / sc);
		allholding = allholding+value;
		row.put("holdType", holdType);
		row.put("allholding", allholding);
		row.put("shareholding", value);
	}

	private void proc_sell_stock(int i, double part, String holdType) {
		if (i < sm.data().size()) {
			JSONObject row = sm.data().get(i);
			double sc = row.optDouble("sc");
			double value = Math.floor(part / sc);
			allholding = allholding -value;
			row.put("holdType", holdType);
			row.put("allholding", allholding);
			row.put("shareholding", -value);
		}
	}

	/**
	 * 一次購入
	 */
	private void proc_init_funds1() {
		JSONObject row = sm.data().get(0);
		double sc = row.optDouble("sc");
		allholding = Math.floor(funds1 / sc);
		row.put("allholding", allholding); // for paym
		row.put("shareholding", allholding);
		row.put("holdType", "持股");
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub

	}

}
