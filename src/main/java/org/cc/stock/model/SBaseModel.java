package org.cc.stock.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;
import org.cc.util.CCDateUtils;

import tech.tablesaw.api.Table;

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

	public void loadFormCSV(String base){
		data = new ArrayList<>();
		File f = new File(base,stockId+".csv");
		Table tb = Table.read().csv(f);
		System.out.println(tb.columnNames());
		for(int i=0;i<tb.rowCount();i++){
			JSONObject row = new JSONObject();
			Date sdate =  CCDateUtils.toDate(tb.getString(i, "sdate"));
			row.put("sdate",sdate);
			row.put("so",tb.doubleColumn("so").get(i));
			row.put("sh",tb.doubleColumn("sh").get(i));
			row.put("sl",tb.doubleColumn("sl").get(i));
			row.put("sc",tb.doubleColumn("sc").get(i));
			row.put("svol",tb.intColumn("svol").get(i));
			row.put("mvol",tb.intColumn("mvol").get(i));
			data.add(row);
		}
		System.out.println("====== load csv size : "+data.size());
	}
}
