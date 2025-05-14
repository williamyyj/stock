package org.cc.stock.util;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public class StockUtil {
	

	public static JSONObject getPhase4(StockModel sm, int periord , int idx) {
		JSONObject ret = new JSONObject();
		JSONObject curr = sm.data().get(idx);
		JSONObject prev = idx>0 ? sm.data().get(idx-1) : null;
		double sa = sm.avg(periord, idx);
		double sah = 0.0,sal=0.0;
		double sc=0,sh,sl,smax=0,smin=9999999999.9;
		int cah=0,cal=0;
		int bIdx = (idx - periord + 1) > 0 ? (idx - periord + 1) : 0;
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
			smax = (sh>smax) ? sh : smax ;
			smin = (smin>sl) ? sl : smin ;
		}
		ret.put("high",smax); // 最高價
		ret.put("sah",sah/cah); // 上部平均值
		ret.put("avg",sa); // 平均價
		ret.put("sal",sal/cal); // 下部平均值
		ret.put("low",smin); // 最低價
		ret.put("phase", (curr.optDouble("sc")-smin)/(smax-smin)); // 4 phase
		proc_all_phase2(sm,ret,periord,idx); // ph9,ph8,ph7,ph6,phh,phl
		return ret;
	}
	
	private static void proc_all_phase2(StockModel sm, JSONObject ret, int periord , int idx ) {
		JSONObject curr = sm.data().get(idx);
        JSONObject prev = idx>0 ? sm.data().get(idx-1) : null;
        int bIdx = (idx - periord + 1) > 0 ? (idx - periord + 1) : 0;
        double hm = ret.optDouble("sah");
        double lm = ret.optDouble("sal");
        double middle = ret.optDouble("avg");
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
        ret.put("ph9", ph9 / smem * 100);
        ret.put("ph8", ph8 / smem * 100);
        ret.put("ph7", ph7 / smem * 100);
        ret.put("ph6", ph6 / smem * 100);
        ret.put("phh", (ph9+ph8) / smem * 100);
        ret.put("phl", (ph7+ph6) / smem * 100);
	}
	
}
