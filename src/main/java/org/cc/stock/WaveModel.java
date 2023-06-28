package org.cc.stock;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.cc.App;
import org.cc.data.CCData;
import org.cc.json.JSONArray;
import org.cc.json.JSONException;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;
import org.cc.stock.model.StockModel;
import org.cc.stock.model.WaveSDataList;
import org.cc.text.TextUtils;

public class WaveModel extends StockModel {
	
	private String stockId;
	NumberFormat nf = new DecimalFormat("0.000");
	
	public WaveModel(CCProcObject proc,String stockId) {
		super (proc,stockId);
	}
		
	protected void gen_ratio() throws JSONException, Exception {
		JSONArray ja = new JSONArray();
		for(int i=0;i<data.size();i++) {
			JSONObject item = new JSONObject();
			JSONObject row = data.get(i);
			double sc = sc(row);
			double ss = avg(2000,i);
			double s5 = avg(5,i);
			double s10 = avg(10,i);
			double s20 = avg(20,i);
			double s60= avg(60,i);
			double s240 = avg(240,i);
			double sm = (s5+s10+s20)/3 ;
			double ry = Math.log(sc/avg(240,i));
			item.put("date", TextUtils.df("yyyy-MM-dd", row.opt("sdate")));
			item.put("sc", sc);
			item.put("s5", s5);
			item.put("s10",s10);
			item.put("s20", s20);
			item.put("s60",s60);
			item.put("s240",s240);
			item.put("__indent__", false);
			ja.put(item);
		}
		CCData.saveText(new File("D:\\HHome\\myprj\\jupyter\\stock\\"+stockId+".json"), ja.toString(4), "UTF-8");
		ja.clear();
		for(int i=0;i<data.size();i++) {		
			JSONObject row = data.get(i);
			if(row.has("isPeek")) {
				JSONObject item = new JSONObject();				
				item.put("date", TextUtils.df("yyyy-MM-dd", row.opt("sdate")));
				item.put("sc", sc(row));
				ja.put(item);
			}
			
		}
		CCData.saveText(new File("D:\\HHome\\myprj\\jupyter\\stock\\"+stockId+"_w.json"), ja.toString(4), "UTF-8");
	}
	
	
	
	public static void main(String[] args) {
		String base = System.getProperty("$base", App.base);
		try (CCProcObject proc = new CCProcObject(base, false)) {
			// 6770 006208 00752 2330 00893
			//StockDataModel sdm = new StockDataModel("6770");
			//StockDataModel sdm = new StockDataModel("006208");
			WaveModel wm = new WaveModel(proc,"00878");

			WaveSDataList wp = new WaveSDataList(wm,260);
			wp.procWaveAllItem();
			for(JSONObject row: wm.data()) {
				if(row.has("isPeek")) {
					int n = row.optInt("$i");
					System.out.println( TextUtils.df("yyyy/MM/dd", row.opt("sdate"))+":::"+wm.sc(row)+""
							+ ","+row.optString("isPeek")+","+wm.bias(200, n)+","+wm.avg(200,n));
				}
			}
	
			wp.show_params();
			//SPeriodDataList pm = new SPeriodDataList(wm);
			wm.gen_ratio();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
