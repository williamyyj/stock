package org.cc.fun.stock;

import java.util.List;
import java.util.function.BiFunction;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;
import org.cc.stock.model.StockModel;

public class FTWSEFromDB implements BiFunction<CCProcObject, String, List<JSONObject>> {

    @Override
    public List<JSONObject> apply(CCProcObject proc, String cmdString) {
    	proc.put("$$", App.$app);
		proc.params().put("stockId", cmdString);
		List<JSONObject> data = (List<JSONObject>) CCProcUtils.exec(proc, "rows@twse,qrStockId");
		double pmem = 0, pvol = 0, ssc = 0;
		for (int i = 0; i < data.size(); i++) {
			JSONObject row = data.get(i);
			pmem += row.optDouble("pmem");
			pvol += row.optDouble("pvol");
			ssc += row.optDouble("ssc");
			row.put("pmem", pmem);
			row.put("pvol", pvol);
			row.put("ssc", ssc);
		}
        return data;
    }

    
}
