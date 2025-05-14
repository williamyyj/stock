package org.cc.stock.model;

import java.util.ArrayList;
import java.util.List;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;
import org.cc.stock.col.SBasicColumns;

public class StockModel extends SBaseModel{
	
	//protected List<JSONObject> highList = new ArrayList<JSONObject>();
	//protected List<JSONObject> lowList = new ArrayList<JSONObject>();
	
	
	public StockModel(CCProcObject proc,String stockId) {
		super(stockId);
		loadFormDB(proc);
		new SBasicColumns(this);
	}

	public StockModel(String base,String stockId) {
		super(stockId);
		loadFormCSV(base);
		new SBasicColumns(this);
	}
	
	
	public  double so(JSONObject row) {
		return row.optDouble("so");
	}

	public  double sh(JSONObject row) {
		return row.optDouble("sh");
	}

	public  double sl(JSONObject row) {
		return row.optDouble("sl");
	}

	public  double sc(JSONObject row) {
		return row.optDouble("sc");
	}
	
	public  double mvol(JSONObject row) {
		return row.optDouble("mvol");
	}

	public  double svol(JSONObject row) {
		return row.optDouble("svol");
	}
	
	public  double mem(JSONObject row) {
		return row.optDouble("mem");
	}
	
	public  double pmem(JSONObject row) {
		return row.optDouble("pmem");
	}
	
	public  double pvol(JSONObject row) {
		return row.optDouble("pvol");
	}
	
	public double ssc(JSONObject row) {
		return row.optDouble("ssc");
	}
	
	public List<JSONObject> data(){
		return data;
	}
		
	/**
	 * 取得幾日前取盤
	 * @param range
	 * @param row
	 * @return
	 */
	public double bsc(int range, JSONObject row) {
		int idx = row.optInt("$i");
		int bIdx = idx - range;
		if (bIdx < 0) {
			bIdx = 0;
		}
		JSONObject bData = data.get(bIdx);
	    return bData.optDouble("sc");
	}
	
	public double sc(int i) {
		return data.get(i).optDouble("sc");
	}
	
	

	public double avg(int range, int eIdx) {
		
		if (eIdx > data.size() || eIdx < 0) {
			throw new RuntimeException("range error : [ " + data.size() + "]:::" + eIdx);
		}
		int bIdx = eIdx - range;
		if (bIdx < 0) {
			bIdx = 0;
		}
		JSONObject bData = data.get(bIdx);
		JSONObject eData = data.get(eIdx);
		if (eIdx >= range) {
			double sc = ssc(eData) - ssc(bData);
			return sc / range;
		} else {
			return ssc(eData) / (eIdx + 1);
		}

	}
	
	
	
	/**
	 * (sc - avg(n))/ avg(n)*100 
	 * @param range
	 * @param eIdx
	 * @return
	 */
	public double bias(int range, int eIdx) {
		double sc = sc (eIdx);
		double avg = avg(range,eIdx);
		return (sc - avg) / avg *100; 
	}
	
	
	public double highRatio(int range, int eIdx) {
		int bIdx = (eIdx-range+1)>0 ? (eIdx-range+1) : 0;
		int cycle = eIdx-bIdx+1;
		double sc=0,sh=0,sl=999999.9;
		for(int i=bIdx;i<=eIdx;i++) {
			double sv = sc(i);
			sc += sv;
			sh = (sv>sh) ? sv : sh ;
			sl = (sl>sv) ? sv : sl ;
		}
		return  sc(eIdx)/sh*100;
	}
	
	/**
	 * 
	 * @param range
	 * @param varId ['svol', 'smem' , 'ssc'] 
	 * @param eIdx
	 * @return
	 */
	public double diff(int range, String varId, int eIdx) {
		if (eIdx > data.size() || (eIdx -range) < 0) {
			return 0.1;
		}
		int bIdx = eIdx - range ;
		JSONObject bData = data.get(bIdx);
		JSONObject eData = data.get(eIdx);	
		return eData.optDouble(varId)-bData.optDouble(varId);
	}
	
	/**
	 * 
	 * @param range
	 * @param varId ['svol', 'smem' , 'ssc']
	 * @param eIdx
	 * @return
	 */
	public double sum(String varId, int range, int eIdx) {
		int bIdx = eIdx-range <0 ? 0 : eIdx-range ;
		if (eIdx > data.size() || eIdx<0) {
			return 0.1;
		}
		//int cycle = eIdx - bIdx+1 ;
		JSONObject bData = data.get(bIdx);
		JSONObject eData = data.get(eIdx);	
		return (eIdx==0) ? bData.optDouble(varId) : eData.optDouble(varId)-bData.optDouble(varId);
	}
	/**
	 * 
	 * @param varId ['svol', 'smem' , 'ssc']
	 * @param range
	 * @param eIdx
	 * @return
	 */
	public double avg(String varId, int range, int eIdx) {
		int bIdx = eIdx-range <0 ? 0 : eIdx-range ;
		if (eIdx > data.size() || eIdx<0) {
			return data.get(0).optDouble(varId);
		}
		int cycle = eIdx - bIdx ;
		JSONObject bData = data.get(bIdx);
		JSONObject eData = data.get(eIdx);	
		return (eIdx==0) ? bData.optDouble(varId) : 
			(eData.optDouble(varId)-bData.optDouble(varId))/cycle;
	}

	public double dsvol(int range, int eIdx) {
		if (eIdx > data.size() || (eIdx -range) < 0) {
			return 0.1;
		}
		int bIdx = eIdx - range ;
		JSONObject bData = data.get(bIdx);
		JSONObject eData = data.get(eIdx);	
		return eData.optDouble("svol")-bData.optDouble("svol");
	}

	public double dsmem(int range, int eIdx) {
		if (eIdx > data.size() || (eIdx -range) < 0) {
			return 0.1;
		}
		int bIdx = eIdx - range ;
		JSONObject bData = data.get(bIdx);
		JSONObject eData = data.get(eIdx);	
		return eData.optDouble("smem")-bData.optDouble("smem");
	}
	
	public double dssc(int range, int eIdx) {
		if (eIdx > data.size() || (eIdx -range) < 0) {
			return 0.1;
		}
		int bIdx = eIdx - range ;
		JSONObject bData = data.get(bIdx);
		JSONObject eData = data.get(eIdx);	
		return eData.optDouble("ssc")-bData.optDouble("ssc");
	}
	
	


	
	public JSONObject stat(JSONObject tai, int range,int eIdx) {
		int bIdx = (eIdx-range+1)>0 ? (eIdx-range+1) : 0;
		int cycle = eIdx-bIdx+1;
		double sc=0,sh=0,sl=999999.9;
		for(int i=bIdx;i<=eIdx;i++) {
			double sv = sc(i);
			sc += sv;
			sh = (sv>sh) ? sv : sh ;
			sl = (sl>sv) ? sv : sl ;
		}
		double sa = sc / cycle;
		double sd = 0.0 ;
		for(int i=bIdx;i<=eIdx;i++) {
			double sv = sc(i);
			sd += (sa-sv)*(sa-sv);
		}
		sd = Math.sqrt(sd/cycle);
		tai.put("sa", sa);
		tai.put("sl", sl);
		tai.put("sh", sh);
		tai.put("sd", sd);
		return tai;
	}
	
	public JSONObject statTaiji(JSONObject tai, int range,int eIdx) {
		int bIdx = (eIdx-range+1)>0 ? (eIdx-range+1) : 0;
		int cycle = eIdx-bIdx+1;
		double sc=0,sh=0,sl=999999.9;
		for(int i=bIdx;i<=eIdx;i++) {
			double sv = Math.log(sc(i)/avg(360,i));
			sc += sv;
			sh = (sv>sh) ? sv : sh ;
			sl = (sl>sv) ? sv : sl ;
		}
		double sa = sc / cycle;
		double sd = 0.0 ;
		for(int i=bIdx;i<=eIdx;i++) {
			double sv = Math.log(sc(i)/avg(360,i));
			sd += (sa-sv)*(sa-sv);
		}
		sd = Math.sqrt(sd/cycle);
		tai.put("sa", sa);
		tai.put("sl", sl);
		tai.put("sh", sh);
		tai.put("sd", sd);
		return tai;
	}
	
	public double avg(String id) {
		JSONObject bData = data.get(0);
		JSONObject eData = data.get(data.size()-1);	
		double bv = bData.optDouble(id);
		double ev = eData.optDouble(id);
		return   (data.size()==1)? bv : (ev-bv)/ (data.size());
	}
	
	public double acc(String varId, int range, int eIdx) {
		if(eIdx-2*range <0 && eIdx<data.size()) {
			return 0.0;
		}
		JSONObject ao = data.get(eIdx-2*range);
		JSONObject a1 = data.get(eIdx-range);
		JSONObject a2 = data.get(eIdx);
		return  (ao.optDouble(varId)-2*a1.optDouble(varId)+a2.optDouble(varId)) ;
	}


}
