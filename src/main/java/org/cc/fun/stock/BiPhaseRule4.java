package org.cc.fun.stock;

import java.util.function.BiFunction;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;
import org.cc.stock.strategy.SPhaseStrategy;
import org.cc.text.TextUtils;

/**
 * SPhaseStrategy的第四個規則
 * 1. 平均線上為陽 buyPrice > avg
 * 2. 成交量大於平均成交量 
 * 3. 後半週期區間平均值 > 前半週期區間平均值 
 * @author 94017
 *
 */
public class BiPhaseRule4 implements BiFunction<SPhaseStrategy, JSONObject, Integer> {


	private int period;
	
	public BiPhaseRule4(int period) {
		this.period = period;
	}
	
	@Override
	public Integer apply(SPhaseStrategy strategy, JSONObject row) {
		int idx = row.optInt("$i");
		if(idx==0) {
			return 0;
		}
		StockModel sm = strategy.getStockModel();
		String sdate = TextUtils.df("yyyy-MM-dd", row.optDate("sdate"));
		double sc = row.optDouble("sc");
		double avg = row.optDouble("avg"+period);
	    double price = avg2(sm,"ssc", period, idx)>avg1(sm,"ssc", period, idx) ? 3 : 2;
	    double vol = avg2(sm,"svol", period, idx)>avg1(sm,"svol", period, idx) ? 3 : 2;
	    double mem = (sc > avg) ? 3 : 2;
	    
	    double k = row.optDouble("k55");
	    double d = row.optDouble("d55");
	    double phase = row.optDouble("phase"+period);
		double score = price + vol + mem;	
		
		System.out.println(sdate+"   "+ TextUtils.df("000.00",row.optDouble("sc"))+","+phase +"-->"
		+ score +":::"+k+":::"+d);

		return -1;

	}
	
	/**
	 * 平均值公式  avg(varId,range,idx)
	 * half = range/2
	 * 計算   eIdx = idx-half >= 0 ? idx-half : 0
	 * 回傳  avg(varid, half, eIdx)		
	 * 
	 * @param varId ['svol', 'smem' , 'ssc']
	 * @param range
	 * @param eIdx
	 * @return
	 */
	public double avg1(StockModel sm,String varId, int range, int idx) {
      int half = range/2;
      int eIdx = idx-half >= 0 ? idx-half : 0;
      return sm.avg(varId, half, eIdx);
    }
	
	/**
	 * 回傳後半部  
	 * @param varId
	 * @param range
	 * @param idx
	 */
	public double avg2(StockModel sm,String varId, int range, int idx) {
		int half = range/2;
		return sm.avg(varId, half, idx);
	}
    



	

}
