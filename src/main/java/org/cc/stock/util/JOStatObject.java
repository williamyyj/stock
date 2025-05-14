package org.cc.stock.util;

import org.cc.json.JSONObject;
import org.cc.stock.col.FStatBase;
import org.cc.stock.col.IFNValue;
import org.cc.stock.model.StockModel;

public class JOStatObject extends JSONObject{
	
	private StockModel sm ; 
	
	private int period ; 
	
	private IFNValue fn ;
	
	 double[] boundariesX = new double[5];
	 double[] boundariesY = new double[5];  
	 double[] intervals = new double[4];
	
	public JOStatObject(StockModel sm, int period , IFNValue fn) {
		super();
		this.sm = sm ; 
		this.period = period;
		this.fn = fn;
		__init_data();
	}
	
	private void __init_data() {
		double sa=0,sh=0,sl=Double.MAX_VALUE;
		for (int i = 0; i < sm.data().size(); i++) {
			JSONObject row = sm.data().get(i);
			double fv = fn.v(row);
			sa += fv;
			sh = (fv > sh) ? fv : sh;
			sl = (sl > fv) ? fv : sl;
		}
		sa = sa / sm.data().size();
		this.put("sa", sa);
		this.put("sh", sh);
		this.put("sl", sl);
		proc_phase();
		proc_stddev();
		System.out.println(this.toString());
	}
	
	private void proc_stddev() {
		double sa = this.optDouble("sa");
        double sh = this.optDouble("sh");
        double sl = this.optDouble("sl");
        double sum = 0;
        for (int i = 0; i < sm.data().size(); i++) {
            JSONObject row = sm.data().get(i);
            double fv = fn.v(row);
            sum += Math.pow(fv-sa, 2);
        }
        double stddev = Math.sqrt(sum/sm.data().size());
        this.put("sd", stddev);
	}

	private void proc_phase() {
		double sa = this.optDouble("sa");
		double sh = this.optDouble("sh");
		double sl = this.optDouble("sl");
		double diff = sh - sl;
		boundariesX[0] = sl;
		boundariesX[1] = sl+diff/4;
		boundariesX[2] = sl+diff/4*2;
		boundariesX[3] = sl+diff/4*3;
		boundariesX[4] = sh;
		
		
		for (int i = 0; i < sm.data().size(); i++) {
			JSONObject row = sm.data().get(i);
			double fv = fn.v(row);
	        int idx = (fv > boundariesX[3]) ? 3 : (fv > boundariesX[2]) ? 2 : (fv > boundariesX[1]) ? 1 : 0;
	        intervals[idx]++;
		}		
		boundariesY[0] = 0;			
		for (int i = 0; i < 4; i++) {
		   intervals[i] = intervals[i] / sm.data().size()*100;
		   boundariesY[i+1] = intervals[i] + boundariesY[i];
		}   
		
	}
	/**
	 * 取得相位 利用分段函數所在機率計算
	 * @param fv
	 * @return
	 */
	public double getPhaseByProbability(double fv) {
		int idx = (fv > boundariesX[3]) ? 3 : (fv > boundariesX[2]) ? 2 : (fv > boundariesX[1]) ? 1 : 0;
		double ret = intervals[idx]*(fv-boundariesX[idx])/(boundariesX[idx+1]-boundariesX[idx])+boundariesY[idx];
		return ret;
	}
	
	/**
	 * 取得相位 利用 sl sh 區間依比例計算
	 * 
	 * @param fv
	 * @return
	 */
	public double getPhase(double fv) {
		double sl = this.optDouble("sl");
		double sh = this.optDouble("sh");
		double ret = (fv - sl) / (sh - sl)*100;
		return ret;
	}
}
