package org.cc.stock;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;
import org.junit.Test;

public class LoadDataTest {
	@Test
	public void test_load_twse_all() throws IOException{
		 String base = System.getProperty("$base", App.base);
	        CCProcObject proc = new CCProcObject(base,false);
	        try {
	            proc.put("$$", App.$app);
	            proc.params().put("sd","20230223");
			    List<JSONObject> rows =  (List<JSONObject>) CCProcUtils.exec(proc, "stock.FTWSEData@twse");
			    if(rows!=null) {
			    	Set<String> hset = new HashSet<String>();
			    	for(JSONObject row:rows) {
			    		String stockid = row.optString("stockid");
			    		if(hset.contains(stockid)) {
			    			System.out.println("==== dup "+stockid);
			    		} else {
			    			hset.add(stockid);
			    		}
			    	}
			    	System.out.println(hset.size());
			    	System.out.println("===== 00888 : "+hset.contains("00888"));
			    }
			    
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally{
	            proc.close();
	        }
		
		
    }
}
