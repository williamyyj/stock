package org.cc.stock.model;


import java.io.File;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.arrow.flatbuf.DateUnit;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.util.CCDateUtils;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;

import tech.tablesaw.api.Table;

public class TA4JModel extends SBaseModel {

	private BarSeries series = null;
	private Table table ;
	private String model;

	public TA4JModel(CCProcObject proc, String stockId) {
		super(stockId);
		__init_jo(proc);
	}
	
	public TA4JModel(String base, String stockId) {
		super(stockId);
		__init_tablesaw(base);
	}
	
	private void __init_tablesaw(String base) {
		File f = new File(base,stockId+".csv");
		table = Table.read().csv(f);
		this.series = new BaseBarSeriesBuilder().withName(stockId).build();
		for(int i=0;i<table.rowCount();i++){
			addBar(series,table,i);
		}
		setModel("tablesaw");
	}
	
	
	private void addBar(BarSeries series, Table tb, int i) {
		Date sdate =  CCDateUtils.toDate(tb.getString(i, "sdate"));
		ZonedDateTime d = sdate.toInstant().atZone(ZoneId.systemDefault());
		double so = tb.doubleColumn("so").get(i);
		double sh = tb.doubleColumn("sh").get(i);
		double sl = tb.doubleColumn("sl").get(i);
		double sc = tb.doubleColumn("sc").get(i);
		series.addBar(d,so,sh,sl,sc,0);
	}

	private void __init_jo(CCProcObject proc) {
		loadFormDB(proc);
		this.series = new BaseBarSeriesBuilder().withName(stockId).build();
		for(JSONObject row:data){
			//System.out.println(row);
			addBar(series,row);
		}
		setModel("db");
	}

	private void addBar(BarSeries series, JSONObject row) {
		ZonedDateTime d = row.optDate("sdate").toInstant().atZone(ZoneId.systemDefault());
		series.addBar(d,row.optDouble("so"),row.optDouble("sh"),row.optDouble("sl"),row.optDouble("sc"),row.optDouble("svol"));
	}
	
	public BarSeries series() {
		return this.series;
	}
	
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
}
