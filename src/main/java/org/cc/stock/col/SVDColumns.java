package org.cc.stock.col;

import org.cc.stock.model.StockModel;

public class SVDColumns extends SBaseColumns {

	private int range = 0 ;
	
	public SVDColumns(StockModel sm) {
		this(sm,21);
	}
	
	public SVDColumns(StockModel sm, int range) {
		super(sm);
		this.range = range ;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		for(int i=0;i<sm.data().size();i++) {
			proc_vd(i);
		}
	}

	private void proc_vd(int eIdx) {
		int bIdx = eIdx-range+1<0 ? 0 : eIdx-range+1 ;
		double sh = 0 , sl = Double.MAX_VALUE;
		for(int i=bIdx;i<eIdx;i++) {
			
		}
		
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub
		
	}

}
