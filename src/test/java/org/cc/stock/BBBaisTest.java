package org.cc.stock;

import java.io.IOException;

import org.cc.App;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.stock.col.FStatBase;
import org.cc.stock.model.StockModel;

public class BBBaisTest {
	public static void main(String[] args) {
		String base = System.getProperty("$base", App.base);
		String stockId = "00878";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			StockModel sm = new StockModel(proc,stockId);
			FStatBase.stat(sm.data(), null, 0, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
