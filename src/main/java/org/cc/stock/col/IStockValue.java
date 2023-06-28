package org.cc.stock.col;

import org.cc.json.JSONObject;

public interface IStockValue {
	
	public static double v(JSONObject row, String id) {
		return row.optDouble(id);
	}
	
	public static double log(JSONObject row, String id) {
		return Math.log(row.optDouble(id));
	}

	
}
