package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

/**
 * Bollinger Band 
 * 1*Sigma 常態分布的機率 68.26% 
 * 2*Sigma 常態分布的機率 95.44% 
 * 3*Sigma 常態分布的機率 99.74%
 * 
 * @author 94017
 *
 */
public class SBBandColumns extends SBaseColumns {
	private int range = 0;
	private double sigma = 0;

	public SBBandColumns(StockModel sm) {
		this(sm, 21, 2);
	}

	public SBBandColumns(StockModel sm, int range, double sigma) {
		super(sm);
		this.range = range;
		this.sigma = sigma;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		double bk = 50.0;
		for (int i = 0; i < sm.data().size(); i++) {
			JSONObject row = sm.data().get(i);
			proc_bband(i);
			double bsg = row.optDouble("bsg");
			bk = (bk * (range - 1) + bsg * 2) / (range + 1);
			row.put("bbg", bk);

		}

	}

	private void proc_bband(int eIdx) {
		JSONObject curr = sm.data().get(eIdx);
		int bIdx = (eIdx - range + 1) > 0 ? (eIdx - range + 1) : 0;
		int cycle = eIdx - bIdx + 1;
		double sum = 0.0;
		double sh = 0, sl = Double.MAX_VALUE;
		// 計算平均數取出最大最小
		for (int i = bIdx; i <= eIdx; i++) {
			JSONObject row = sm.data().get(i);
			double v = row.optDouble("sc");
			sum += v;
			sh = (v > sh) ? v : sh;
			sl = (sl > v) ? v : sl;
		}
		double sa = sum / cycle;
		double sd = 0;
		// 計算sd
		for (int i = bIdx; i <= eIdx; i++) {
			JSONObject row = sm.data().get(i);
			double v = row.optDouble("sc");
			sd += (sa - v) * (sa - v);
		}
		sd = Math.sqrt(sd / cycle);

		// 計算實際機率
		double chigh = 0.0; double vhigh = sa+ sigma*sd; 
		double clow = 0.0; double vlow = sa - sigma*sd;
		for (int i = bIdx; i <= eIdx; i++) {
			JSONObject row = sm.data().get(i);
			double v = row.optDouble("sc");
			//System.out.println("==== "+v+"-->"+(v>vhigh));
			if(v>vhigh) {
				chigh +=1.0;
			}else if (v<=vlow) {
				clow +=1.0;
			}
		}
		

		double grade = grade(sa, sd, curr.optDouble("sc"));
		curr.put("bsa", sa);
		curr.put("bsd", sd);
		curr.put("bsg", grade);
		curr.put("chigh", chigh/cycle);
		curr.put("clow", clow/cycle);
		curr.put("vhigh", vhigh);
		curr.put("vlow", vlow);

	}

	/**
	 * 近常態數字差距很大
	 * 
	 * @param mean
	 * @param stdDev
	 * @param x
	 * @return
	 */
	private double guess(double mean, double stdDev, double x) {
		double z = (x - mean) / stdDev;
		double p = (1.0 / (Math.sqrt(2 * Math.PI) * stdDev)) * Math.exp(-0.5 * Math.pow(z, 2));
		return p;
	}

	private double grade(double sa, double sd, double x) {
		if (x >= (sa + 2 * sd)) {
			// System.out.println("===== grade: "+x+","+(sa+2*sd)+","+100);
			return 100; // 加權強化
		} else if ((sa + 2 * sd) > x && x >= sa) {
			return (x - sa) / (2 * sd) * (80 - 50) + 50;
		} else if (sa > x && x > (sa - 2 * sd)) {
			return (x - sa + 2 * sd) / (2 * sd) * (50 - 20) + 20;
		} else {
			return 0;
		}
	}

	@Override
	public void evaluation() {
		// TODO Auto-generated method stub

	}

}
