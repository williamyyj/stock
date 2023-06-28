package org.cc.stock.col;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

/**
 * SAR[n] = SAR[n-1] + AF (EP - SAR[n-1]) AF為加速因子(acceleration
 * factor)，EP為極值(extreme price) 決定 SAR[0]
 * 
 * trend : 1 （上行) , 0(下行)
 * 
 * 2022-11-02-->s:1,sc:25.17,sar:25.4278,af:0.02,smax:25.32,smin:24.36
 * 2022-11-03-->s:1,sc:24.88,sar:24.36,af:0.02,smax:25.18,smin:24.36
 * 2022-11-04-->s:2,sc:24.93,sar:24.36,af:0.02,smax:25.18,smin:24.36
 * 
 * 2022-11-02 25.01 2022-11-03 24.36 2022-11-04 24.38
 * 
 * status:1 --> 下跌 EP[n-1] =
 * 
 * 
 * 
 *
 */
public class SSARColumns extends SBaseColumns {

	private double afInc = 0.02;
	private double afMax = 0.2;
	private double afInit = 0.2;
	private double af = 0.2;
	private double ep = 0.0;
	private int trend = 0;
	private double sar = 0.0;
	private Deque<Double> highRows;
	private Deque<Double> lowRows;
	private List<Double> highTrends;
	private List<Double> lowTrends;

	

	public SSARColumns(StockModel sm) {
		this(sm, 10, 0.2);
	}

	public SSARColumns(StockModel sm, int range, double afInc) {
		super(sm);
		this.afInc = afInc;
		this.afMax = 0.2;
		this.afInit = afInc;
		this.af = afInc;
		this.ep = 0;
		this.sar = 0;
		this.highRows = new ArrayDeque<>(2);
		this.lowRows = new ArrayDeque<>(2);
		this.highTrends = new ArrayList<>();
		this.lowTrends = new ArrayList<>();
		__init_columns();
	}

	@Override
	protected void __init_columns() {

		for (int i = 0; i < sm.data().size(); i++) {
			JSONObject row = sm.data().get(i);
			double high = row.optDouble("sh");
			double low = row.optDouble("sl");
			if (i < 3) {
				sar = initSARValue(row, high, low);
			} else {
				sar = calcSARValue(i);
			}
			sar = updateCurrentVals(row,sar, high, low);
			System.out.println(TextUtils.df("yyyy-MM-dd",
			row.optString("sdate"))+"["+row.optDouble("sc")+","+high+","+low+"]-->"
			+sar+","+ep+","+trend+","+af);

		}
	}

	private double updateCurrentVals(JSONObject row,double psar, double high, double low) {
		if(this.trend==1) {
			highTrends.add(high);
		} else if (this.trend==0) {
			lowTrends.add(low);
		}
		sar = trendReversal(psar, high, low);
		row.put("sar", sar);
		row.put("ep", ep);
		row.put("af", af);
		row.put("trend", trend);
		highRows.addLast(high);
		lowRows.addLast(low);
		if(highRows.size()>=3) {
			highRows.removeFirst();
			lowRows.removeFirst();
		}
		return sar;
	}

	private double trendReversal(double psar, double high, double low) {
		boolean reversal = false;
		if( this.trend==1 && psar>low) {
			this.trend=0;
			sar = Collections.max(highTrends);
			this.ep = low;
			reversal = true;
		} else if ( this.trend==0 && psar<high ) {
			this.trend=1;
			sar = Collections.min(lowTrends);
			this.ep = high;
			reversal = true;
		}
		if(reversal) {
			this.af = afInit ;
			highTrends.clear();
			lowTrends.clear();
		} else {
			if(high>ep && trend==1) {
				af = Math.min(af+afInc, afMax);
				ep = high ;
			} else if (low<ep && trend==0) {
				af = Math.min(af+afInc, afMax);
				ep = low ;
			}
		}
		return sar;
	}

	private double calcSARValue(int eIdx) {
		double psar = sm.data().get(eIdx - 1).optDouble("sar");
		
		if (trend == 1) { // 上行
			sar = psar + af * (ep - psar);
			sar = Math.min(sar, Collections.min(lowRows));
		} else {
			sar = psar - af * (psar-ep);
			sar = Math.max(sar, Collections.max(highRows));
		}
		System.out.println((eIdx)+","+highRows);
		System.out.println((eIdx)+","+lowRows);
		System.out.println((eIdx)+","+psar+","+sar);
		return sar ;
	}

	private double initSARValue(JSONObject row, double high, double low) {
		if (highRows.size() <= 1) {
			this.trend = 0;
			this.ep = high;
			this.af = 0;
			return 0.0;
		}
		System.out.println(highRows);
		if (highRows.getFirst() < highRows.getLast()) {
			this.trend = 1;
			sar = Collections.min(lowRows);
			ep = Collections.max(highRows);
			af = afInit;
		} else {
			this.trend = 0;
			sar = Collections.max(highRows);
			ep = Collections.min(lowRows);
			af = afInit;
		}
		return sar;
	}


	@Override
	public void evaluation() {
		// TODO Auto-generated method stub

	}



}
