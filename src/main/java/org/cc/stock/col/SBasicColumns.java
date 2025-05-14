package org.cc.stock.col;



import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;
/**
 * StockModel 基本欄位 
 * 共用取值演算法
 * 
 * @author 94017
 *
 */
public class SBasicColumns {
	
	protected StockModel sm ;

	
	public SBasicColumns(StockModel sm) {
		this.sm = sm;
		__init_basic_data();
	}
	
	private void __init_basic_data() {
		double svol=0.0;
		double smem=0.0;
		double ssc = 0.0;
		double max = 0.0 ; 
		double min = 999999999999999999999.0;
		
		int idx=0;
		for(JSONObject row:sm.data()) {
			row.remove("mem");
			double vol =  row.optDouble("svol");
			row.put("vol",vol); // 成交股數
			svol += vol; // sum of volume
			smem += row.optDouble("mvol"); // sum of vloume * price
			double sc = row.optDouble("sc",0.0);
			ssc += sc; // sum of close
			// basic cplumns
			row.put("svol", svol);
			row.put("smem", smem);
			row.put("ssc", ssc);
			row.put("$i", idx);
			idx++;
		}
		
	}
}
