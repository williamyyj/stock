package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public class SATRColumns extends SBaseColumns {
	public static IFNValue fnTR = (row) -> row.optDouble("tr");
	private int period = 0;

	public SATRColumns(StockModel sm) {
		this(sm, 21);
	}

	public SATRColumns(StockModel sm, int period) {
		super(sm);
		this.period = period;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		JSONObject perior = sm.data().get(0);
		perior.put("tr", Math.abs(perior.optDouble("sh") - perior.optDouble("sl")));
		JSONObject curr = null;
		for (int i = 1; i < sm.data().size(); i++) {
			curr = sm.data().get(i);
			double tr1 = Math.abs(curr.optDouble("sh") - curr.optDouble("sl"));
			double tr2 = Math.abs(curr.optDouble("sh") - perior.optDouble("sc"));
			double tr3 = Math.abs(curr.optDouble("sl") - perior.optDouble("sc"));
			double tr = Math.max(Math.max(tr1, tr2), tr3);
			curr.put("tr", tr);
			JSONObject tai = FStatBase.stat(sm.data(), fnTR, period, i);
			curr.put("atr" + period, tai.optDouble("sd"));
			perior = curr;
		}

	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub

	}

}
