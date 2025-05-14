package org.cc.stock.col;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

/**
 * 共用統計
 * @author 94017
 *
 */


public class FStatBase {
	
	public static IFNValue fnSC = (row)->row.optDouble("sc");
	
	
	public static void stat(Object[] ja, JSONObject ret, IFNValue fn) {		
		double sa=0,sh=0,sl=Double.MAX_VALUE;
		for (int i = 0; i < ja.length; i++) {
			JSONObject row = (JSONObject) ja[i];
			double sv = fn.v(row);
			sa += sv;
			sh = (sv > sh) ? sv : sh;
			sl = (sl > sv) ? sv : sl;
		}
		sa = sa / ja.length;
		double sd = 0.0;
		for (int i = 0; i < ja.length; i++) {
			JSONObject row = (JSONObject) ja[i];
			double sv = fn.v(row);
			sd += (sa - sv) * (sa - sv);
		}
		sd = Math.sqrt(sd/ja.length);
		ret.put("sa", sa);
		ret.put("sh", sh);
		ret.put("sl", sl);
		ret.put("sd", sd);
	}
	
	
	public static JSONObject stat(Object[] ja, IFNValue fn) {
		JSONObject ret = new JSONObject();
		stat(ja, ret, fn);
		return ret;
	}
	
	public static  void stat(StockModel sm, JSONObject ret, IFNValue fn){
		stat(sm.data().toArray(),ret,fn);
	}
	
	public static JSONObject stat(List<JSONObject> rows, IFNValue fn, int range, int eIdx) {	
		JSONObject tai = new JSONObject();
		JSONObject curr = rows.get(eIdx);
		int bIdx = (eIdx-range+1)>0 ? (eIdx-range+1) : 0;
		int cycle = eIdx-bIdx+1;
		int count = 0;
		double sc=0,sh=0,sl=Double.MAX_VALUE;
		for(int i=bIdx;i<=eIdx;i++) {
			JSONObject row = rows.get(i);
			double v =  fn.v(row);
			if(fn.v(curr)>=v) {
				count++;
			}
			sc += v;
			sh = (v>sh) ? v : sh ;
			sl = (sl>v) ? v : sl ;
		}
		
		//System.out.println(count+":::"+cycle);
		double se = (sh+sl)/2;
		double sa = sc / cycle;
		double sd = 0.0 ;
		double meh = 0;
		double mah = 0;
		for(int i=bIdx;i<=eIdx;i++) {
			JSONObject row = rows.get(i);
			double v =  fn.v(row);
			if(v>se) meh++;
			if(v>sa) mah++;
			sd += (sa-v)*(sa-v);
		}
		sd = Math.sqrt(sd/cycle);
		tai.put("sa", sa);
		tai.put("sl", sl);
		tai.put("sh", sh);
		tai.put("sd", sd);
		tai.put("diff",Math.log(se/sa)); 
		tai.put("se",se); 
		tai.put("meh",meh/cycle*100);
		tai.put("mah", mah/cycle*100); 
		tai.put("sv", fn.v(curr));
		tai.put("grade", count*100.00/cycle);
		tai.put("cycle", cycle);
		curr.put("stat_"+range, tai);
		return tai;
	}
	
	
	/**
	 * 皮爾森相關係數
	 * @param rows
	 * @param xId
	 * @param yId
	 * @return
	 */
	public static JSONObject pearson(List<JSONObject> rows, String xId, String yId) {
		JSONObject ret = new JSONObject();
		double ux=0.0 , uy=0.0 ; 
		int n = rows.size()-1;
		for(int i=0;i<n;i++) {
			JSONObject row = rows.get(i);
			ux += row.optDouble(xId);
			uy += row.optDouble(yId);		
		}
		ux = ux / n ; 
		uy = uy / n ;
		//System.out.println("===== ux :"+ux);
		//System.out.println("===== uy :"+uy);
		double cov=0.0 , varx=0.0 , vary=0.0;
		for(int i=0;i<=n;i++) {
			JSONObject row = rows.get(i);
			double x = row.optDouble(xId);
			double y = row.optDouble(yId);	
			cov += (x-ux)*(y-uy);
			varx += (x-ux)*(x-ux);
			vary += (y-uy)*(y-uy);
		}
		cov = cov/ (n-1);
		varx = varx/(n-1);
		vary = vary/(n-1);
		
		//System.out.println("===== cov :"+cov);
		//System.out.println("===== varx :"+varx);
		//System.out.println("===== vary :"+vary);
		
		double p = cov / (Math.sqrt(varx)*Math.sqrt(vary));
		ret.put("p", p);
		ret.put("cov", cov);
		ret.put("stdx", Math.sqrt(varx));
		ret.put("stdy", Math.sqrt(vary));
		ret.put("varx", varx);
		ret.put("vary", vary);	
		ret.put("ux", ux);
		ret.put("uy", uy);
		return ret;
	}
	
	public static double getPhaseMaxMin(JSONObject data, double fv) {
		double sh = data.optDouble("sh");
		double sl = data.optDouble("sl");
		return  (fv-sl)/(sh-sl)*100;
	}
	
	
		
}
