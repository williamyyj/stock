package org.cc.stock;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.cc.data.CCData;
import org.cc.data.CCHttpData;
import org.cc.text.DateUtil;

public class PullTESCByStockId extends PullStockBase {
	
	public final static String  url = "https://www.twse.com.tw/zh/exchangeReport/STOCK_DAY?response=csv&date=%s&stockNo=%s";
			
	public static void main(String[] args) throws Exception {
		String stockId = "00907";
		Date ds = DateUtil.to_date("20100101");
		Date de = new Date();
		Calendar curr = Calendar.getInstance();
		curr.setTime(ds);
		File base = new File("G:\\我的雲端硬碟\\mydata\\stock\\csv");
		if(!base.exists()) {
			base.mkdirs();
		}
		while (curr.getTime().before(de)) {
			int year = curr.get(Calendar.YEAR);
			int month = curr.get(Calendar.MONTH)+1;
			int day = 1; // 一定是1 ; 
			String dStr = String.valueOf( year*10000+(month)*100+day);
			String url = String.format(PullTESCByStockId.url, dStr,stockId);
			System.out.println(url);
			String content = CCHttpData.text(url, "UTF-8");
			File f = new File(base,stockId+"_"+dStr+".json");
			CCData.saveText(f, content, "UTF-8");
			curr.add(Calendar.MONTH, 1);
		}
		
		
	}
	
}
