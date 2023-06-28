package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

/**
 * 
 * @author 94017
 *
 */
public class STaijiBIASColumns extends SBaseColumns {
	
	private static IFNValue fn = (row) -> row.optDouble("b");
	
	private int range = 0 ; // 統計範圍
	private int bias = 0 ; // 週期
	
	public STaijiBIASColumns(StockModel sm) {
		this(sm,720,20);
	}
	
	public STaijiBIASColumns(StockModel sm, int range,int bias) {
		super(sm);
		this.range = range;
		this.bias = bias;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		double grade = 50.0;
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			double b = sm.avg(bias,i);
			//b =  Math.log(sm.sc(i)/sm.avg(bias,i));
			//b = sm.sc(i)/sm.avg(bias,i) ; 
			b = (sm.sc(i)-b)/b*100;
			//b = row.optDouble("sc");
			//b = row.optDouble("svol");
			row.put("b",b);
			JSONObject tai = FStatBase.stat(sm.data(), fn, range, i);
			grade = (grade*2 + tai.optDouble("grade",0.0))/3;		
			tai.put("grade", grade);
			row.put("b"+String.valueOf(bias), tai);
			
		
		}
		
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

}
