package org.cc.stock;

public class FLineMethod {

	private int level ;
	private double[] x ;
	private double[] y ;
	
	public FLineMethod(int level , double x[], double y[]) {
		this.level = level;
		this.x = x;
		this.y= y;
	}
	
	public double value(double xx) {
		if(xx<x[0]) {
			return y[0];
		} else if (x[0]>=xx && xx <x[1]) {
			return fn(xx,x[0],y[0],x[1],y[1]);
		} else if (x[1]>=xx && xx <x[2]) {
			return fn(xx,x[1],y[0],x[2],y[1]);
		} else if (x[2]>=xx && xx <x[3]) {
			return fn(xx,x[2],y[0],x[3],y[1]);
		} else if (x[3]>=xx && xx <x[4]) {
			return fn(xx,x[3],y[0],x[4],y[1]);
		} else if (x[4]>=xx && xx <x[5]) {
			return fn(xx,x[4],y[0],x[5],y[1]);
		} else if (x[5]>=xx && xx <x[6]) {
			return fn(xx,x[5],y[0],x[6],y[1]);
		} else {
			return y[6];
		}
	}
	
	private double fn(double xx , double x0 , double y0 , double x1 , double y1 ) {
		return  (y1-y0) / (x1-x0) * (x1-x0);
	}
	
}
