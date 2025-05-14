package org.cc.stock.col;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

/**
 *  1.取平均值　sa
 *  2. 取上部平均值　sum(if(sh>=sc,sc,0))  --> sah
 *  3. 取下部平均值  sum(if(sl<sc,sc,0)) --> sal
 *  
 * @author 94017
 *
 */
public class SPhaseColumns extends SBaseColumns {
	private int shortPeriod = 5; // 取短週期用
	private int range = 0;
	
	public SPhaseColumns(StockModel sm) {
		this(sm,21);
	}

	public SPhaseColumns(StockModel sm, int range) {
		super(sm);
		this.range = range;
		__init_columns();
	}

	@Override
	protected void __init_columns() {
		for (int i = 0; i < sm.data().size(); i++) {
			calc_phase(sm,i);
		}
	}

	private void calc_phase(StockModel sm, int idx) {
		JSONObject curr = sm.data().get(idx);
		JSONObject prev = idx>0 ? sm.data().get(idx-1) : null;
		double sa = sm.avg(range, idx);
		double sah = 0.0,sal=0.0;
		double sc=0,sh,sl,smax=0,smin=9999999999.9;
		int cah=0,cal=0;
		int bIdx = (idx - range + 1) > 0 ? (idx - range + 1) : 0;
		for (int i = bIdx; i <= idx; i++) {
			JSONObject row = sm.data().get(i);
			sc = row.optDouble("sc");
			sh = row.optDouble("sh");
			sl = row.optDouble("sl");
			if(sc>=sa) {
				sah+=sc; cah++;
			} else {
				sal+=sc; cal++;
			}
			if (sh > smax) {
				smax = sh; 
				curr.put("$high"+range, row.optInt("$i"));
			}
			if (sl < smin) {
				smin = sl;
				curr.put("$low" + range, row.optInt("$i"));
			}
		}
	
		sah = cah>0 ? sah/cah : 0;
		sal = cal>0 ? sal/cal : 0;
		curr.put("sah"+range, sah); // 上部平均值
		curr.put("sal"+range, sal); // 下部平均值
		curr.put("rvHigh"+range, sc/smax*100); // 回測值
		curr.put("rvLow"+range, sc/smin*100); // 回測值
		curr.put("high"+range, smax); 	// 區閶最大值
		curr.put("avg"+range, sa);	// 區間平均值
		curr.put("low"+range, smin); // 區間最小值
		curr.put("middle"+range, (smax+smin)/2); // 區間中間值
		double sv =  curr.optDouble("sc");
		double phase =  (sv-smin)/(smax-smin)*100;
		double cphase = prev!=null ? prev.optDouble("phase"+range) : 50;
		phase = cphase*2/3+phase/3;
		curr.put("phase"+range,phase); // 	
		curr.put("phcount"+range,cah*100.0/range); // 
		//proc_all_phase1(sm,idx); // after high, low, 用區間高低點資料會有 跳躍(gap)的問題 
		proc_all_phase2(sm,idx); // after low, high , using sah , sal ,avg
	}
	
	/**
	 * after high, low, 用區間高低點成交量資料會有 跳躍(gap)的問題
	 * @param sm
	 * @param idx
	 */
	private void proc_all_phase1(StockModel sm, int idx ) {
		JSONObject curr = sm.data().get(idx);
        JSONObject prev = idx>0 ? sm.data().get(idx-1) : null;
        int bIdx = (idx - range + 1) > 0 ? (idx - range + 1) : 0;
        double high = curr.optDouble("high"+range);
        double low = curr.optDouble("low"+range);
        double middle = (high+low)/2;
        double hm = (high+middle)/2;
        double lm = (low+middle)/2;
        double smem=0,ph9=0,ph8=0,ph7=0,ph6=0;
        for (int i = bIdx; i <= idx; i++) {
            JSONObject row = sm.data().get(i);
            double sc = row.optDouble("sc");
            double mvol = row.optDouble("vol");
            if (sc >= hm) {
                ph9 += mvol;
            } else if (sc >= middle && sc< hm) {
                ph8 += mvol;
            } else if (sc >= lm && sc < middle) {
                ph7 += mvol;
            } else if (sc < lm) {
                ph6 += mvol;
            }
            smem += mvol;
        }
        curr.put("ph9"+range, ph9 / smem * 100);
        curr.put("ph8"+range, ph8 / smem * 100);
        curr.put("ph7"+range, ph7 / smem * 100);
        curr.put("ph6"+range, ph6 / smem * 100);
        curr.put("phh"+range, (ph9+ph8) / smem * 100);
        curr.put("phl"+range, (ph7+ph6) / smem * 100);
	}
	
	/**
	 * after high, low, 用區間高低點 跳躍(gap)的問題
	 * @param sm
	 * @param idx
	 */
	private void proc_all_phase1_1(StockModel sm, int idx ) {
		JSONObject curr = sm.data().get(idx);
        JSONObject prev = idx>0 ? sm.data().get(idx-1) : null;
        int bIdx = (idx - range + 1) > 0 ? (idx - range + 1) : 0;
        double high = curr.optDouble("high"+range);
        double low = curr.optDouble("low"+range);
        double middle = (high+low)/2;
        double hm = (high+middle)/2;
        double lm = (low+middle)/2;
        double count=0,ph9=0,ph8=0,ph7=0,ph6=0;
        for (int i = bIdx; i <= idx; i++) {
            JSONObject row = sm.data().get(i);
            double sc = row.optDouble("sc");
    
            if (sc >= hm) {
                ph9 ++;
            } else if (sc >= middle && sc< hm) {
                ph8 ++;
            } else if (sc >= lm && sc < middle) {
                ph7 ++;
            } else if (sc < lm) {
                ph6 ++;
            }
            count++;
        }
        curr.put("ph9"+range, ph9 / count*100) ;
        curr.put("ph8"+range, ph8 / count * 100);
        curr.put("ph7"+range, ph7 / count * 100);
        curr.put("ph6"+range, ph6 / count * 100);
        curr.put("phh"+range, (ph9+ph8) / count * 100);
        curr.put("phl"+range, (ph7+ph6) / count * 100);
	}
	
	/**
	 * after low, high , using sah , sal ,avg 比較平滑
	 * @param sm
	 * @param idx
	 */
	private void proc_all_phase2(StockModel sm, int idx ) {
		JSONObject curr = sm.data().get(idx);
        JSONObject prev = idx>0 ? sm.data().get(idx-1) : null;
        int bIdx = (idx - range + 1) > 0 ? (idx - range + 1) : 0;
        double hm = curr.optDouble("sah"+range);
        double lm = curr.optDouble("sal"+range);
        double middle = curr.optDouble("avg"+range);
        double count=0,ph9=0,ph8=0,ph7=0,ph6=0;
        for (int i = bIdx; i <= idx; i++) {
            JSONObject row = sm.data().get(i);
            double sc = row.optDouble("sc");
            double mvol = row.optDouble("vol");
            if (sc >= hm) {
                ph9 += mvol;
            } else if (sc >= middle && sc< hm) {
                ph8 += mvol;
            } else if (sc >= lm && sc < middle) {
            	ph7 += mvol;
            } else if (sc < lm) {
                ph6 += mvol;
            }
            count+= mvol;
        }
        curr.put("ph9"+range, ph9 / count * 100);
        curr.put("ph8"+range, ph8 / count * 100);
        curr.put("ph7"+range, ph7 / count * 100);
        curr.put("ph6"+range, ph6 / count * 100);
        curr.put("phh"+range, (ph9+ph8) / count * 100);
        curr.put("phl"+range, (ph7+ph6) / count * 100);
	}
	
	/**
	 * after low, high , using sah , sal ,avg 比較平滑
	 * @param sm
	 * @param idx
	 */
	private void proc_all_phase2_1(StockModel sm, int idx ) {
		JSONObject curr = sm.data().get(idx);
        JSONObject prev = idx>0 ? sm.data().get(idx-1) : null;
        int bIdx = (idx - range + 1) > 0 ? (idx - range + 1) : 0;
        double hm = curr.optDouble("sah"+range);
        double lm = curr.optDouble("sal"+range);
        double middle = curr.optDouble("avg"+range);
        double count=0,ph9=0,ph8=0,ph7=0,ph6=0;
        for (int i = bIdx; i <= idx; i++) {
            JSONObject row = sm.data().get(i);
            double sc = row.optDouble("sc");
            double mvol = row.optDouble("vol");
            if (sc >= hm) {
                ph9++; // ph9 += mvol;
            } else if (sc >= middle && sc< hm) {
                ph8++; // ph8 += mvol
            } else if (sc >= lm && sc < middle) {
                ph7++; // ph7 += mvol;
            } else if (sc < lm) {
                ph6++; // ph6 += mvol;
            }
            count++;
        }
        curr.put("ph9"+range, ph9 / count * 100);
        curr.put("ph8"+range, ph8 / count * 100);
        curr.put("ph7"+range, ph7 / count * 100);
        curr.put("ph6"+range, ph6 / count * 100);
        curr.put("phh"+range, (ph9+ph8) / count * 100);
        curr.put("phl"+range, (ph7+ph6) / count * 100);
	}
	
	
	@Override
	public void evaluation() {
		// TODO Auto-generated method stub

	}

}
