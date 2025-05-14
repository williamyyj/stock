package org.cc.stock;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

public class SBlockDataModel extends StockModel {
		
	public SBlockDataModel(CCProcObject proc,String stockId) {
		super(proc,stockId);
		__init_kd();
	}
	
	

	private void __init_kd() {
		double k20=0, d20=0;
		for(int i=0; i<data.size();i++) {
			JSONObject row = data.get(i);
			if(i==0) {
				k20=50;
				d20=50;
			} else {
				double a20 = will(45,i);
				k20 = 2*k20/3 + a20/3 ;
				d20 = 2*d20/3 + k20/3;
			}
			row.put("k20",k20);
			row.put("d20",d20);
		}
	}




	
	private double will(int range, int eIdx) {
		int bi = eIdx-range+1;
		bi = bi<0 ? 0 : bi ;
		double max =-1 ,min = 99999999;
		for(int i=bi ;i<=eIdx;i++) {
			JSONObject row = data.get(i);
			//double sc = StockModel.sc(row);
			double sh = sh(row);
			double sl = sl(row);
			max = sh>max ? sh : max ;
			min = min>sl ? sl : min ;
		}
		System.out.println(max+","+min);
		//double ar = avg(range,eIdx);
		double sc =  sc(data.get(eIdx));
		double b = (max-min);
		if(b==0) {
			return 50.0;
		} else {
			return (sc-min)*100.0/b;
		}
	}
	
	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.000");
		String base = System.getProperty("$base", App.base);
		try (CCProcObject proc = new CCProcObject(base, false)) {
			SBlockDataModel bm = new SBlockDataModel(proc,"00685L");
			for(int i=0;i<bm.data.size();i++) {
				JSONObject row = bm.data.get(i);
				double sc = bm.sc(row);
				double a20 = bm.avg(20, i);
				double k20 = row.optDouble("k20");
				double d20 = row.optDouble("d20");
				//System.out.println(i+" ,"+row);
				System.out.println(i + " " + TextUtils.df("yyyy-MM-dd", row.optDate("sdate")) + ":::" + nf.format(sc)
				+ "||" + nf.format(k20)+","+ nf.format(d20)+","+ nf.format(sc/a20) 
				//+ "|" + nf.format(ph60)+","+ nf.format(pl60)+","+ nf.format(a60) 
				//+ "|" + nf.format(ph240)+","+ nf.format(pl240)+","+ nf.format(a240) 
				//+ "|" + nf.format(ph720)+","+ nf.format(pl720)+","+ nf.format(a720) 
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
