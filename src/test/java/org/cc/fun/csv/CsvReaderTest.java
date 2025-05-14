package org.cc.fun.csv;

import java.nio.file.Path;
import java.util.List;

import org.cc.App;
import org.cc.json.CCConfig;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.model.CCProcUtils;
import org.junit.Test;

public class CsvReaderTest {
	
	@Test
	public void test_reader() {
		String base = App.base;
		try (CCProcObject proc = new CCProcObject(base, false)) {
			JSONObject cfg = new CCConfig(proc.base(),"otc").params();
			//Path p = Path.of(cfg.optString("$targetPath"), "00888.csv");
			proc.params().put("stockId", "00888");
			//proc.params().put("path", p);
			//List<JSONObject> rows = (List<JSONObject>) CCProcUtils.exec(proc, "csv.reader@otc,load");
			//rows.forEach(System.out::println);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
