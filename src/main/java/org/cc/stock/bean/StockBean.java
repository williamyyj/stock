package org.cc.stock.bean;

import org.cc.json.JSONObject;

public class StockBean  {
	
	private JSONObject data ;
	
	public StockBean(JSONObject data) {
		this.data = data ;
	}
	
	public StockBean() {
		this.data = new JSONObject();
	}
	
	public JSONObject data() {
		return data;
	}
	
	
}
