package org.cc.stock;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cc.data.CCData;
import org.cc.data.CCHttpData;
import org.cc.json.CCJSON;
import org.cc.json.JSONObject;
import org.cc.text.TextUtils;

public class PullOTCByStockId {
	//public final static String  url = "https://www.tpex.org.tw/web/stock/aftertrading/daily_trading_info/st43_download.php?l=zh-tw&d=%s&stkno=%s&s=0,asc,0";
	public final static String  url ="https://www.tpex.org.tw/www/zh-tw/afterTrading/tradingStock?code=%s&date=%s&id=&response=json";
	public final static Pattern p = Pattern.compile("[0-1]?\\d{1,2}[/][0-1]\\d[/][0-3]\\d");
	private String stockId = "";
	private File bf ;
	private Date ds;
	private Date de;
	private String base;
	private JSONObject cfg ;
	public PullOTCByStockId(String base , String stockId) {
		this.base = base;
		this.stockId = stockId;
		bf = new File(base);
		if(!bf.exists()) {
			bf.mkdirs();
		}
		cfg = CCJSON.laod(base, stockId+"_cfg");
		ds = cfg.optDate("lastUpdate");
		if(ds==null){
			ds =  cfg.optDate("beginDate");
		}
		de = new Date();
		System.out.println("===== ds:"+ds);
		System.out.println("===== de:"+de);
	}

	
	private void mergeData() throws Exception {
		File csv = new File(bf,stockId+".csv");
		LinkedHashSet<String> data = loadFromCsv(csv);
		data.add("sdate,svol,mvol,so,sh,sl,sc,漲跌,筆數");
		Calendar curr = getBeginDate();
		String lastUpdate = "";
		
		while (curr.getTime().before(de)) {
			int year = curr.get(Calendar.YEAR);
			int month = curr.get(Calendar.MONTH)+1;
			String ms = month<10 ? "0"+String.valueOf(month) : String.valueOf(month);
			String fym = String.valueOf(year-1911)+ms;
			File f = new File(bf,stockId+"_"+fym+".csv");
			lastUpdate = TextUtils.df("yyyyMMdd", curr.getTime());
			if(f.exists()) {
				proc_one_file(data,f);
				f.deleteOnExit();
			}
			curr.add(Calendar.MONTH, 1);
		}
		cfg.put("lastUpdate", lastUpdate);
		File fcfg = new File(base, stockId+"_cfg.json");
		CCData.saveText(fcfg,cfg.toString(),"UTF-8");
		
		
		StringBuilder sb = new StringBuilder();
		for(String item:data) {
			sb.append(item).append("\r\n");
		}
		CCData.saveText(csv, sb.toString(), "UTF-8");
		
	}
	
	public LinkedHashSet<String> loadFromCsv(File f) throws Exception {
		LinkedHashSet<String> data = new LinkedHashSet<>();
		if(f.exists()) {
			List<String> items = CCData.loadList(f, "UTF-8");
			for(String item:items) {
				data.add(item);
			}
			System.out.println("===== curr size : "+data.size());
		}
		return data;
	}
	
	private void proc_one_file(LinkedHashSet<String> data, File f) throws Exception {
		List<String> items = CCData.loadList(f, "UTF-8");
		for(String item:items) {
			Matcher m = p.matcher(item);
			if(m.find()) {
				data.add(item);
			}
		}
	}

	private Calendar getBeginDate(){
		Calendar curr = Calendar.getInstance();
		curr.setTime(ds);
		curr.set(Calendar.DATE,1);
		curr.set(Calendar.HOUR,0);
		curr.set(Calendar.MINUTE,0);
		curr.set(Calendar.SECOND,0);
		curr.set(Calendar.MILLISECOND,0);
		return curr;
	}
	
	public void loadFromUrl() throws Exception {
		Calendar curr = getBeginDate();
		while (curr.getTime().before(de)) {
			int year = curr.get(Calendar.YEAR);
			int month = curr.get(Calendar.MONTH)+1;
			String ms = month<10 ? "0"+String.valueOf(month) : String.valueOf(month);
			String ym = String.valueOf(year-1911) + "%2F" + ms+"%2F01";
			String url = String.format(PullOTCByStockId.url, stockId, ym);
			String fym = String.valueOf(year-1911)+ms;
			File f = new File(bf,stockId+"_"+fym+".json");
			System.out.println(url);
			if(!f.exists()) {
				//String content = CCHttpData.text(url, "BIG5");
				//CCData.saveText(f, content, "UTF-8");
			}
			curr.add(Calendar.MONTH, 1);
		}
	}
	
	public static void main(String[] args) throws Exception {
		String base = "G:\\我的雲端硬碟\\mydata\\stock\\twotc";
		//String[] items = new String[]{"00888","00928","00772B","00687B","00751B","00773B","00719B"};
		String[] items = new String[]{"00719B"};
		for(String stockId:items){
			PullOTCByStockId pull = new PullOTCByStockId(base,stockId);
			pull.loadFromUrl();
			//pull.mergeData();
		}
	}
	
}
