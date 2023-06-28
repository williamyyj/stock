package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

/**
 * 
 * @author 94017
 *
 */
public class SHHKDColumns extends SBaseColumns{
	
	private int pl = 0;
	private int pm = 0; 
	private int ps = 0 ;

	public SHHKDColumns(StockModel sm) {
		this(sm,21,55,233);
	}
	public SHHKDColumns(StockModel sm, int ps , int pm , int pl) {
		super(sm);
		this.pl = pl ; 
		this.pm = pm ; 
		this.ps = ps ; 
		__init_columns();
	}
	
	
	@Override
	protected void __init_columns() {
		int len = sm.data().size();
		new SKDColumns(sm,pl,55,55).evaluation();
		new SKDColumns(sm,pm,13,13).evaluation();
		new SKDColumns(sm,ps,5,5).evaluation();

		for (int i=0;i<len;i++) {
			JSONObject row = sm.data().get(i);
			row.put("hh"+pl,proc_range_data(pl,55,i));
			row.put("hh"+pm,proc_range_data(pm,13,i));
			row.put("hh"+ps,proc_range_data(ps,5,i));
		}
	}
	
	
	

	private JSONObject proc_range_data(int p, int ps, int eIdx) {
		JSONObject row = sm.data().get(eIdx);
		JSONObject ret = FStatBase.stat(sm.data(), FStatBase.fnSC, p, eIdx);
		double sh = ret.optDouble("sh");
		double sl = ret.optDouble("sl");
		double sa = ret.optDouble("sa");
		ret.put("sold",  0.6 * (sh-sa)+sa);
		ret.put("buy", 0.4 * (sa-sl)+sl);
		ret.put("gap",  (sh-sl)*100.0/sa);
		ret.put("k",row.optDouble("k"+p));
		ret.put("d",row.optDouble("d"+p));
		
		//if(eIdx>0) {
		//	JSONObject prior = sm.data().get(eIdx-1).optJSONObject("hh"+p);
		//	double sg =( prior.optDouble("sg",0)*(ps-2)+ 2*ret.optDouble("grade"))/ps;
		//	ret.put("sg",sg);
		//}
		
		return ret;
	}
	
	@Override
	public void evaluation() {
		// TODO Auto-generated method stub
		
	}
}
