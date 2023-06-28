package org.cc.stock;

import java.io.File;

import org.cc.App;
import org.cc.data.CCData;
import org.cc.model.CCProcObject;
import org.junit.jupiter.api.Test;


public class ListDataListTest {

	@Test
	public void testMonthSDataList() {
		System.out.println("----------------------------");
		String base = System.getProperty("$base", App.base);
		String stockId = "0056";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			//StockModel sm = new StockModel(proc,stockId);
			//MonthSDataList ml = new MonthSDataList(sm);
			//System.out.println(ml.toString());
			//CCData.saveText(new File("D:\\HHome\\myprj\\jupyter\\stock",stockId+"_m.json"),ml.toString(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
