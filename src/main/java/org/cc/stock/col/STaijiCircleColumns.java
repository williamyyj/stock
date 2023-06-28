package org.cc.stock.col;

import java.util.List;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public class STaijiCircleColumns extends SBaseColumns {

	private IFNValue fn ;
	
	public STaijiCircleColumns(StockModel sm,IFNValue fn) {
		super(sm);
		this.fn = fn;
		__init_columns();
	}
	
	@Override
	protected void __init_columns() {
	
		for(int i=0; i< sm.data().size();i++ ) {
			proc_row(sm.data(),20,i);
			proc_row(sm.data(),60,i);
			proc_row(sm.data(),240,i);
		}
		
	}

	private void proc_row(List<JSONObject> rows , int range, int eIdx) {
		JSONObject curr = rows.get(eIdx);
		int bIdx = (eIdx-range+1)>0 ? (eIdx-range+1) : 0;
		int cycle = eIdx-bIdx+1;
		int count = 0;
		double fcurr = fn.v(curr);
		for(int i=bIdx;i<=eIdx;i++) {
			JSONObject row = rows.get(i);
			double fv = fn.v(row);
			//System.out.println(fcurr+"-->"+fv);
			if(fcurr>=fv) {
				count++;
			}
		}
		curr.put("tai_"+range, count*100.0/cycle );
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
