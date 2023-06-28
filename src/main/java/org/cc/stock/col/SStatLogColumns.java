package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

/**
 * 
 * @author 94017
 *
 */
public class SStatLogColumns extends SBaseColumns {
	
	private static IFNValue fn = (row) -> row.optDouble("$fv");
	
	private int range = 0 ; // 統計範圍
	private int bias = 0 ; // 
	
	public SStatLogColumns(StockModel sm) {
		this(sm,720,20);
	}
	
	public SStatLogColumns(StockModel sm, int range,int bias) {
		super(sm);
		this.range = range;
		this.bias = bias;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			double b = sm.avg(bias,i);
			//double fv =  Math.log(sm.sc(i)/b);
			double fv = sm.sc(i);
			row.put("$fv",fv);
			JSONObject tai = FStatBase.stat(sm.data(), fn, range, i);
			double sa = tai.optDouble("sa");
			double sh = sa+2*tai.optDouble("sd");
			double sl = sa-2*tai.optDouble("sd");
			row.put("bsv"+String.valueOf(bias), fv);
			row.put("bsa"+String.valueOf(bias), sa);
			row.put("bsh"+String.valueOf(bias), sh);
			row.put("bsl"+String.valueOf(bias), sl);
		}
		
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

}
