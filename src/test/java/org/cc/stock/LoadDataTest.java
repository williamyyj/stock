package org.cc.stock;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;
import org.junit.Assert;
import org.junit.Test;


public class LoadDataTest {
	
	

	public void test_f1() {
		Assert.assertTrue(false);
		System.out.println("===== test f1");
	}

	
	public  static void main(String[] args) throws IOException {
		String base = System.getProperty("$base", App.base);
		CCProcObject proc = new CCProcObject(base, false);
		System.out.println("===== start test !!!!" );
		try {
	
			
			
			proc.put("$$", App.$app);
			proc.params().put("sd", "20240703");
			System.out.println("===== start " + proc.params());
			List<JSONObject> rows = (List<JSONObject>) CCProcUtils.exec(proc, "stock.FTWSEData@twse");
			
			if (rows != null) {
				System.out.println(rows.size());
				Set<String> hset = new HashSet<String>();
				for (JSONObject row : rows) {
					//System.out.println(row);
					String stockid = row.optString("stockid");
					if (hset.contains(stockid)) {
						System.out.println("==== dup " + stockid);
					} else {
						hset.add(stockid);
					}
				}
				System.out.println(hset.size());
				System.out.println("===== 00685L : " + hset.contains("00685L"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			proc.close();
		}

	}
}
