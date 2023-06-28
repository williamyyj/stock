package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public abstract class SBaseColumns {
	
	protected StockModel sm;

	
	public SBaseColumns(StockModel sm) {
		this.sm = sm;
	}

	abstract protected  void __init_columns() ;
	
	public double max(int bIdx , int eIdx) {
		double smax = 0.0;
		bIdx = bIdx < 0 ? 0 : bIdx;
		for(int i=bIdx ; i<=eIdx ;i++) {
			JSONObject row = sm.data().get(i);
			double sh = row.optDouble("sh");
			smax = sh>smax ? sh : smax;
		}
		return smax;
		
	}
	
	public double min(int bIdx , int eIdx) {
		double smin = Double.MAX_VALUE;
		bIdx = bIdx < 0 ?  0 : bIdx;
		for(int i=bIdx ; i<=eIdx ;i++) {
			JSONObject row = sm.data().get(i);
			double sl = row.optDouble("sl");
			smin = smin>sl ? sl : smin;
		}
		return smin;
		
	}
	
	public abstract void evaluation() ;
	

	
}
