package org.cc.stock.model;

import java.util.List;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;

public class SBaseModel {
	protected List<JSONObject> data ;
	protected String stockId; 
	
	public SBaseModel(String stockId) {
		this.stockId = stockId;
	}
	
	public void loadFormDB(CCProcObject proc) {
		proc.put("$$", App.$app);
		proc.params().put("stockId", stockId);
		data = (List<JSONObject>) CCProcUtils.exec(proc, "rows@twse,qrStockId");
		System.out.println("====== loadFormDB size : "+data.size());
	}
}
