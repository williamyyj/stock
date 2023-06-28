package org.cc.stock.col;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.cc.json.JSONObject;
import org.cc.model.CCField;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

public class SOutputColumns {
	
	protected List<CCField> fields = new ArrayList<>() ;
	NumberFormat nf = new DecimalFormat("0.00");
	
	public SOutputColumns() {
		this(new String[] {"sdate:date","so","sh","sl","sc","vol"});
	}
	
	public SOutputColumns(String[] cols) {
		for(String col: cols) {
			CCField fld = new CCField();
			String[] items = col.split(":");
			String dt = items.length>1 ? items[1] : "double";
			fld.put("id", items[0]);
			fld.put("label", items[0]);
			fld.put("dt", dt);
			fields.add(fld);
		}
	}
	
	

	public StringBuilder toCSVString(StockModel sm ) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			proc_fields(sb,row);
		}
		return sb;
	}
	
	private void proc_fields(StringBuilder sb, JSONObject row) {
		for(CCField fld:fields) {
			switch(fld.optString("dt")) {
				case "date": sb.append(TextUtils.df("yyyy-MM-dd", fld.getFieldValue(row))); break;
				case "double" : sb.append(nf.format(row.optDouble(fld.id()))); break;
				case "string" : sb.append(row.optString(fld.id()));break;
			}
			sb.append(",");
		}
		if(fields.size()>0) {
			sb.setLength(sb.length()-1);
		}
		sb.append("\n");
	}

	
}
