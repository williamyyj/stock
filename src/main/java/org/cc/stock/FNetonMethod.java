package org.cc.stock;

/**
 * 牛頓插值多項式
 * @author 94017
 *   		0		1		2		3		4		5		6		
 *	 price 	ma-3a	ma-2a	ma-a	ma 		ma+a	ma+2a   ma+3a
 *	 ratio 
 */	  
public class FNetonMethod {
	private double[] a = null ;
	private int level ;
	private double[] x ;
	private double[] y ;
	
	public FNetonMethod(int level , double x[], double y[]) {
		this.level = level;
		this.a = new double [level];
		this.x = x;
		this.y= y;
		proc_am();
	}
	
	
	public void proc_am() {
		for(int i=0;i<level;i++) {
			System.out.println(x[i]+"--->"+y[i]);
			proc_ami(i);
		}
	}
	
	public void proc_ami(int idx) {
		if(idx==0) {
			for(int i=0;i<level;i++) {
				a[i] = y[i];
			}
		} else {
			for(int i=level-1;i>=idx;i--) {
			  a[i] =(a[i] - a[i-1])/(x[i]-x[i-idx]);
			}
		}
	}
	
	
	public double value(double xx) {
		double fn = 0 ;
		for(int i=0; i<level;i++) {
			fn = fn + fni(i,xx);
		}
		return fn ;
	}
	
	/**
	 *   a[n] * (xx-x[0])...(xx-x[n-1]);
	 * @param idx
	 * @param xx
	 * @return
	 */
	private double fni(int idx ,double xx) {
		double ret = a[idx] ; 
		if(idx>0) {
			for(int i=0;i<idx;i++) {
				ret = ret*(xx-x[i]);
			}
		}
		return ret ;
	}
	
    public static double interpolate(double[] x, double[] y, double point) {
        int n = x.length;
        double result = y[0];
        double[] dividedDifference = new double[n];
        dividedDifference[0] = y[0];
        for (int i = 1; i < n; i++) {
            double denominator = 1;
            for (int j = 0; j < i; j++) {
                denominator *= (x[i] - x[j]);
            }
            double numerator = y[i];
            for (int j = 0; j < i; j++) {
                numerator -= dividedDifference[j] * (x[i] - x[j]);
            }
            dividedDifference[i] = numerator / denominator;
            double term = dividedDifference[i];
            for (int j = 0; j < i; j++) {
                term *= (point - x[j]);
            }
            result += term;
        }
        return result;
    }
    
    public static double lagrangeInterpolation(double[] x, double[] y, double xi) {
        double result = 0;
        for (int i = 0; i < x.length; i++) {
            double term = y[i];
            for (int j = 0; j < x.length; j++) {
                if (j != i) {
                    term *= (xi - x[j]) / (x[i] - x[j]);
                }
            }
            result += term;
        }
        return result;
    }
    
    public static void main(String[] args) {
        double[] x = {66.54,68.05287915727878, 68.66268957863939, 69.2725, 69.8823104213606, 70.4921208427212};
        double[] y = {0,0.05, 0.32, 0.5, 0.68, 0.95};
        double point = 66.55;
        double result = lagrangeInterpolation(x, y, point);
        System.out.println("f(" + point + ") = " + result);
    }
}
