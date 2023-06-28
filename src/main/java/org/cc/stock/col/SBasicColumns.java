package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public class SBasicColumns {
	
	protected StockModel sm ;
	
	public SBasicColumns(StockModel sm) {
		this.sm = sm;
		__init_data();
	}
	
	private void __init_data() {
		double svol=0.0;
		double smem=0.0;
		double ssc = 0.0;
		int idx=0;
		for(JSONObject row:sm.data()) {
			row.remove("mem");
			double vol =  row.optDouble("svol");
			row.put("vol",vol);
			svol += vol;
			smem += row.optDouble("mvol");
			ssc += row.optDouble("sc",0.0);
			row.put("svol", svol);
			row.put("smem", smem);
			row.put("ssc", ssc);
			row.put("$i", idx++);
		}
		
	}
}
