package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public class SOBVColumns extends SBaseColumns {

	private int range = 0;
	
	public SOBVColumns(StockModel sm) {
		this(sm,21);
	}
	
	public SOBVColumns(StockModel sm, int range) {
		super(sm);
		this.range = range ;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		JSONObject piror = sm.data().get(0);
		double obv = sm.data().get(0).optDouble("vol");
		double sobv = obv;
		double eobv = obv;
		piror.put("obv", obv);
		piror.put("sobv", obv);
		piror.put("eobv", obv);
		for(int i=1;i<sm.data().size();i++) {
			JSONObject curr =  sm.data().get(i);
			double psc = piror.optDouble("sc");
			double csc = curr.optDouble("sc");
			if(csc>psc) {
				obv += curr.optDouble("vol");
			} else if(csc<psc) {
				obv -= curr.optDouble("vol");
			} // else if (csc=psc) { obv = obv ;}
			sobv += obv;	
			eobv = (eobv*(range-1) + obv*2 )/(range+1);
			curr.put("obv",obv);
			curr.put("sobv",sobv);
			curr.put("eobv",eobv);
			piror = curr;
		}
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub
		
	}

}
