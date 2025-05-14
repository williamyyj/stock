package org.cc.fun.stock;

import java.util.function.BiFunction;

import org.cc.json.JSONObject;
import org.cc.stock.strategy.SPhaseStrategy;
import org.cc.stock.util.JOStatObject;
import org.cc.text.TextUtils;

/**
 * SPhaseStrategy的第三個規則
 * 1. 買進 rvHighPhase < rvHighStat.avg + rvHighStat.stddev*2 
 * 2. 賣出 buyPrice > buyPrice * 1.05 
 * @author 94017
 *
 */
public class BiPhaseRule3 implements BiFunction<SPhaseStrategy, JSONObject, Integer> {

	private double sellRatio;

	
	public BiPhaseRule3( double sellRatio) {
	    this.sellRatio = sellRatio;
	}
	
	@Override
	public Integer apply(SPhaseStrategy strategy, JSONObject row) {
		int idx = row.optInt("$i");
		if(idx==0) {
			return 0;
		}
		
		double rvHigh = strategy.getValue(row,"rvHigh");
		double rvLow = strategy.getValue(row,"rvLow");
		JOStatObject rvHighStat = strategy.getRvHighStat();
		JOStatObject rvLowStat = strategy.getRvLowStat();
		String sdate = TextUtils.df("yyyy-MM-dd", row.optDate("sdate"));
		if ( rvHighStat.optDouble("sa") - rvHighStat.optDouble("sd") * 2 > rvHigh) { 
			//買進
			System.out.println( sdate +  "  buy -->rv["+rvHigh+","+rvLow+"] sa:"+rvHighStat.optDouble("sa")+" sd:"+rvHighStat.optDouble("sd"));
			return 0;
		}
	    if (rvLowStat.optDouble("sa") + rvLowStat.optDouble("sd") * 2 < rvLow) {
			//賣出
	    	System.out.println( sdate + "  sell-->rv["+rvHigh+","+rvLow+"] sa:"+rvLowStat.optDouble("sa")+" sd:"+rvLowStat.optDouble("sd"));
			return 1;
		}
		return -1;
	}

	

}
