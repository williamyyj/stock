package org.cc.stock;

import java.util.List;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

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
    
	public static double polynomialCurveValue(double[] coeff, double xi) {
		double ret = 0;
		for (int i = 0; i < coeff.length; i++) {
			ret += coeff[i] * Math.pow(xi, i);
		}
		return ret;
	}
    
	public static double[]  PolynomialCurveFitter(double[] x, double[] y) {
		
		WeightedObservedPoints obs = new WeightedObservedPoints();
		for (int i = 0; i < x.length; i++) {
			obs.add(x[i], y[i]);
		}
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2); // y = a[0]+a[1]x+a[2]x^2 , degree=2
		List<WeightedObservedPoint> obs2List = obs.toList();
		double[] coeff = fitter.fit(obs2List);
		return coeff;
	}
    
    public static void main(String[] args) {
    	//===== rvHighStat :{"sa":92.44479194661658,"sd":6.803371964477431,"sh":100,"sl":64.76079346557759}
    	//===== rvLowStat :{"sa":124.46115358251555,"sd":18.494507420861716,"sh":209.3264248704663,"sl":100}
    	System.out.println((100+64)/2);
        //double[] x = {100, 92.44479194661658 + 6.803371964477431 ,92.44479194661658 , 92.44479194661658-2*6.803371964477431, 64.76079346557759};
    	// revert double[] x
    	double[] x = {64.76079346557759, 92.44479194661658-2*6.803371964477431, 92.44479194661658 ,92.44479194661658 + 6.803371964477431, 100};
    	        
        double[] y = {0,25, 50, 75, 100};
        double xi = 70;
        
        double[] coffe = PolynomialCurveFitter(x, y);
        for(int i=65;i<=100;i++) {
         	double v = polynomialCurveValue(coffe, i);
        	System.out.println("f(" + i + ") = " + v);
        }
        
        SplineInterpolator splineInterpolator = new SplineInterpolator();
        PolynomialSplineFunction splineFunction = splineInterpolator.interpolate(x, y);

        // 预测值
        double yi = splineFunction.value(xi);
        System.out.println("当 xi = " + xi + " 时，预测的 yi = " + yi);
       yi =  lagrangeInterpolation(x, y, xi);
        System.out.println("当 xi = " + xi + " 时，预测的 yi = " + yi);
    }
}
