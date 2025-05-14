package org.cc.fun.stock;

import java.util.function.BiFunction;

import org.cc.json.JSONObject;
import org.cc.stock.strategy.SPhaseStrategy;

/**
 * SPhaseStrategy的第二個規則
 * 1. phase > highGate && rvLowPhase >  sellGate 賣出
 * 2. phase < lowGate && rvHighPhase < buyGate  買進
 * @author 94017
 *
 */
public class BiPhaseRule2 implements BiFunction<SPhaseStrategy, JSONObject, Integer> {

	private double highGate;
	private double lowGate;
	private	double buyGate;
	private double sellGate;
	
	public BiPhaseRule2(double highGate, double lowGate, double buyGate, double sellGate) {
		this.highGate = highGate;
		this.lowGate = lowGate;
		this.buyGate = buyGate;
		this.sellGate = sellGate;
	}
	
	@Override
	public Integer apply(SPhaseStrategy strategy, JSONObject row) {
		double phase = strategy.getPhase(row);
		double rvHighPhase = strategy.getHighPhase(row);
		double rvLowPhase = strategy.getLowPhase(row);
		if (phase > highGate && rvLowPhase > sellGate) {
			return 1;
		} else if (phase < lowGate && rvHighPhase < buyGate) {
            return 0;
		}
		return -1;
	}

	

}
