package org.cc.fun.stock;

import java.util.function.BiFunction;

import org.cc.json.JSONObject;
import org.cc.stock.strategy.SPhaseStrategy;

/**
 * SPhaseStrategy的第一個規則
 * 1. phase > highGate && rvHighPhase > highGate && rvLowPhase > highGate 賣出
 * 2. phase < lowGate && rvHighPhase < lowGate && rvLowPhase < lowGate 買進
 * @author 94017
 *
 */
public class BiPhaseRule1 implements BiFunction<SPhaseStrategy, JSONObject, Integer> {

	private double highGate;
	private double lowGate;
	
	public BiPhaseRule1(double highGate, double lowGate) {
		this.highGate = highGate;
		this.lowGate = lowGate;
	}
	
	@Override
	public Integer apply(SPhaseStrategy strategy, JSONObject row) {
		double phase = strategy.getPhase(row);
		double rvHighPhase = strategy.getHighPhase(row);
		double rvLowPhase = strategy.getLowPhase(row);

		if (phase > highGate && rvHighPhase > highGate && rvLowPhase > highGate) {
			return 1;
		} else if (phase < lowGate && rvHighPhase < lowGate && rvLowPhase < lowGate) {
			return 0;
		}
		return -1;
	}

	

}
