package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public class SMACDColumns extends SBaseColumns {

	private int fv=0;
	private int sv=0;
	private int mv=0;

	public SMACDColumns(StockModel sm) {
		this(sm,12,26,9);
		
	}
	
	public SMACDColumns(StockModel sm, int fv , int sv,int mv) {
		super(sm);
		this.fv = fv ;
		this.sv = sv ;	
		this.mv = mv;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		double fema = sm.sc(0);
		double sema = sm.sc(0);
		double dif = fema-sema;
		double macd = dif;
		sm.data().get(0).put("dif",dif);
		sm.data().get(0).put("macd",macd);
		for(int i=1;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			fema = ((fema)*(fv-1) + sm.sc(i)*2)/(fv+1);
			sema = ((sema)*(sv-1) + sm.sc(i)*2)/(sv+1);
			//System.out.println(fema+","+sema);
			dif = fema-sema;
			macd = (macd*(mv-1)+dif*2)/(mv+1);
			row.put("dif", dif);
			row.put("macd", macd);
		}
		
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub
		
	}
	
	
}
