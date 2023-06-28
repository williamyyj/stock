package org.cc.stock.col;

public class TaiPhaseBean implements Comparable<TaiPhaseBean>{
	
	private int phase ;
	
	private double value ;
	
	public TaiPhaseBean(int phase, double value) {
		this.phase = phase ;
		this.value = value;
	}
	
	@Override
	public int compareTo(TaiPhaseBean o) {
		return Double.compare(this.value,o.value);
	}
	
	public int getPhase() {
		return this.phase;
	}
	
	public double getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return phase+"_"+String.valueOf(value);
	}

}
