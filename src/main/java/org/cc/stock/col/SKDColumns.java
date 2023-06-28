package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

/**
 * KD(9,3,3) 百年曆史誠不欺我
 * @author 94017
 *
 */
public class SKDColumns extends SBaseColumns {

	private int rv =0;
	private int kv =0;
	private int dv =0;
	private String kId = "";
	private String dId = "";

	public SKDColumns(StockModel sm) {
		this(sm,9,3,3);
	}
	
	public SKDColumns(StockModel sm, int rv , int kv, int dv) {
		super(sm);
		this.rv = rv;
		this.kv = kv;
		this.dv = dv;
		this.kId= "k"+rv;
		this.dId= "d"+rv;
		__init_columns();
	}
	
	@Override
	protected void __init_columns() {
		double rsv = 0.0;
		double k =50.0;
		double d =50.0;
		JSONObject curr = sm.data().get(0);
		curr.put(kId,k);
		curr.put(dId,d);
		for (int i=1;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			rsv = rsv(i,rv);
			k = ((kv-1)*k+ rsv)/kv;
			d = ((dv-1)*d+ k)/dv;
			row.put(kId, k);
			row.put(dId, d);
			//grade(row);
		}

	}
	
	private double rsv(int cIdx , int offset) {
		JSONObject curr = sm.data().get(cIdx);
		int bIdx = cIdx - offset +1 ; 
		bIdx = bIdx>=0 ? bIdx : 0 ; 
		double max = 0.0 ;
		double min = 99999999.0 ;
		for(int i=bIdx ; i<=cIdx;i++) {
			JSONObject row = sm.data().get(i);
			double sh = row.optDouble("sh");
			double sl = row.optDouble("sl");
			max = sh>max ? sh : max ;
			min = min>sl ? sl : min ;
		}
		double sc = curr.optDouble("sc");
		if(max>min) {
			return (sc-min)/(max-min)*100.0;
		}
		return 0;
	}
	
	
	@Override
	public void evaluation() {
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			double k = row.optDouble(kId);
			double d = row.optDouble(dId);
			if( k<20 ) {
				row.put("trade"+kId,"B");
			} else if(k>80 ) {
				row.put("trade"+kId,"S");
			}
			
		}
		
	}

	/**
	 * 分五批
	 * @param eIdx
	 */
	private void proc_buy_trade(int eIdx) {
		
		
	}
	
	private void proc_sold_trade(int eIdx) {
		// TODO Auto-generated method stub
		
	}



	
}
