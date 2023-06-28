package org.cc.stock.col;

import java.util.List;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public class SVPColumns extends SBaseColumns {

	private int range = 0;
	
	public SVPColumns(StockModel sm) {
		this(sm,22);
	}
	

	
	public SVPColumns(StockModel sm, int range) {
		super(sm);
		this.range = range;
		__init_columns();
	}
	

	@Override
	protected void __init_columns() {
		for (int i=0;i<sm.data().size();i++) {
			calc_time(range,i);
			calc_price(range,i);
		}
	}
	
	private void calc_time(int range, int eIdx) {
		JSONObject curr = sm.data().get(eIdx);
		int b1 = (eIdx-range)>=0 ? (eIdx-range) : 0 ; 
		int nrange = eIdx-b1+1 ;
		int left = nrange / 2 ;
		double v1=0.0, v2=0.0;
		double v =0.0;
		
		for(int i=b1;i<=eIdx;i++) {
			JSONObject row = sm.data().get(i);
			if(i<(b1+left)) {
				v1 += row.optDouble("mvol");
			} else {
				v2 += row.optDouble("mvol");
			}
			v +=  row.optDouble("mvol");
		}
		System.out.println(eIdx+"-->"+range+","+ left+","+v+","+v1+","+v2);
		curr.put("tv", v1/v*100);
	}
	
	private void calc_price(int range, int eIdx) {
		JSONObject curr = sm.data().get(eIdx);
		int bIdx = (eIdx-range+1)>0 ? (eIdx-range+1) : 0;
		int cycle = eIdx-bIdx+1;
		int count = 0;
		double sa =0.0;
		double pv = 0.0;
		for(int i=bIdx;i<=eIdx;i++) {
			JSONObject row = sm.data().get(i);
			double sc =  row.optDouble("mvol");
			sa += sc;
		}
		//System.out.println(count+":::"+cycle);
		sa = sa / cycle;		
		double v=0.0, v1=0.0, v2=0.0;
		for(int i=bIdx;i<=eIdx;i++) {
			JSONObject row = sm.data().get(i);
			double sc =  row.optDouble("mvol");
			if(sc<sa) {
				v1 += sc;
			} else {
				v2 += sc;
			}
			v += sc;
		}
		curr.put("pv1", v1/v*100);
		curr.put("pv2", v2/v*100);
	
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub
		
	} 

}
