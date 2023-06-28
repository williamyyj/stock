package org.cc.stock.col;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

/**
 * 
 * @author 94017
 *
 */
public class SWaveColumns extends SBaseColumns{

	private int minRange = 0 ;
	private Deque<Double> highRows  =null;
	private Deque<Double> lowRows  =null;
	
	public SWaveColumns(StockModel sm) {
		this(sm,3);
	}
	
	public SWaveColumns(StockModel sm, int minRange) {
		super(sm);
		this.minRange = minRange;
		this.highRows = new ArrayDeque<>(minRange*2+1);  //[-3,-2,-1,0,1,2,3] , minRange=3
		this.lowRows = new ArrayDeque<>(minRange*2+1);  
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		JSONObject prior = null;
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			procWaveItem(row,i);
			if(prior==null && row.has("isPeek")) {
				prior = row;
			} else if (prior!=null && row.has("isPeek")) {
				if(prior.optString("isPeek").equals(row.opt("isPeek"))){			
					if("+".equals(prior.opt("isPeek"))){
						double ov = prior.optDouble("sh");
						double nv = row.optDouble("sh");
						if(nv>ov) {
							prior.remove("isPeek");
							prior = row;
						} else {
							row.remove("isPeek");
						}
					} else {
						double ov = prior.optDouble("sl");
						double nv = row.optDouble("sl");
						if(nv<ov) {
							prior.remove("isPeek");
							prior = row;
						} else {
							row.remove("isPeek");
						}
					}
				} else {
					prior = row;
				}
			}
		}
		
	}
	
	private void procWaveItem(JSONObject curr,int currIdx) {
		int bIdx = currIdx-minRange;
		int eIdx = currIdx+minRange;
		double sh = curr.optDouble("sh");
		double sl = curr.optDouble("sl");
		highRows.clear();
		lowRows.clear();
		for(int i=bIdx;i<=eIdx;i++) {		
			if(i<0 || i>= sm.data().size()) {
				highRows.add(sh);
				lowRows.add(sl);
			} else {
				JSONObject row = sm.data().get(i);
				highRows.add(row.optDouble("sh"));
				lowRows.add(row.optDouble("sl"));
			}
		}
		double max = Collections.max(highRows);
		double min = Collections.min(lowRows);
		if(sh==max) {
			curr.put("isPeek", "+");
		}
		if(sl==min) {
			//System.out.println(TextUtils.df("yyyy-MM-dd",curr.opt("sdate"))+"["+curr.optString("isPeek"," ")+"]"
			//		+curr.optDouble("sc")+","+curr.optDouble("sh")+","+curr.optDouble("sl"));
			//System.out.println(lowRows);
			//System.out.println("----------------------------------------------------------");
			curr.put("isPeek", "-");
		}
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub
		
	}

}
