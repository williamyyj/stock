package org.cc.stock.col;

import java.util.List;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

/**
 * 迴歸(regression) 方法是一個分析變數和變數之間關係的工具
 * @author 94017
 *
 */
public class SRegressionColumns extends SBaseColumns {

	private int range = 0;
	private int degree = 0;
	public SRegressionColumns(StockModel sm) {
		this(sm, 2,21);
	}
	/**
	 * 
	 * @param sm  stock model
	 * @param degree  多項式次數  y = a[0]+a[1]x+a[2]x^2 , degree=2 , 目前最大3
	 * @param range 周期
	 */
	public SRegressionColumns(StockModel sm, int degree,int range) {
		super(sm);
		this.range = range;
		this.degree = degree;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		double rk = 0.0;
		for (int i = 0; i < sm.data().size(); i++) {
			JSONObject row = sm.data().get(i);
			proc_range(range, i);
			rk = (rk * 4 + row.optDouble("rb" + range, 0.0)) / 5;
			row.put("rk" + range, rk);
		}
	}

	private void proc_range(int range, int eIdx) {
		int bIdx = eIdx - range + 1;
		WeightedObservedPoints obs = new WeightedObservedPoints();
		JSONObject curr = sm.data().get(eIdx);
		double scale = 100;
		for (int i = bIdx; i <= eIdx; i++) {
			int idx = i - eIdx; // --> eIdx = 0
			//int idx = i ; // --> bIdx = 0
			if (i < 0) {
				JSONObject row = sm.data().get(0);
				obs.add(idx/scale, row.optDouble("sc"));
			} else {
				JSONObject row = sm.data().get(i);
				obs.add(idx/scale, row.optDouble("sc"));
			}
		}
		
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree); // y = a[0]+a[1]x+a[2]x^2 , degree=2
		List<WeightedObservedPoint> obs2List = obs.toList();
		double[] coeff = fitter.fit(obs2List);
		
		
		curr.put("ra" + range, coeff[0]);
		curr.put("rb" + range, coeff[1]);
		if(degree>=2){
			curr.put("rc"+range, coeff[2]);
		}
		if(degree>=3){
			curr.put("rd"+range, coeff[3]);
		}
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub

	}
	
	

}
