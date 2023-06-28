package org.cc.stock;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.cc.App;
import org.cc.data.CCData;
import org.cc.json.JSONObject;
import org.cc.model.CCProcObject;
import org.cc.stock.col.FStatBase;
import org.cc.stock.col.IFNValue;
import org.cc.stock.col.STaiJiColumns;
import org.cc.stock.model.StockModel;
import org.cc.text.TextUtils;

public class SMonthModel extends StockModel {

	public SMonthModel(CCProcObject proc, String stockId) {
		super(proc, stockId);
	}

	public static void main(String[] args) {
		NumberFormat nf = new DecimalFormat("0.00");
		String base = System.getProperty("$base", App.base);
		String stockId = "0050";
		try (CCProcObject proc = new CCProcObject(base, false)) {
			SMonthModel sm = new SMonthModel(proc, stockId);
			// new SKDColumns(sm);
			// new SSARColumns(sm);
			 //new STaiJiColumns(sm,240);
			 new STaiJiColumns(sm,20);
			 //new STaiJiColumns(sm,240);
			StringBuilder sb = new StringBuilder();
			int idx = 0;
			for (JSONObject row : sm.data()) {
				JSONObject tai = row.optJSONObject("tai20");
				if(tai!=null) {
					sb.append(TextUtils.df("yyyy-MM-dd", row.optString("sdate")) + "," + row.optDouble("sc"));
					//sb.append("," + sm.avg(5, idx) );
					//sb.append("," + sm.avg(10, idx)  );
					//sb.append("," + sm.avg(20, idx) );
					//sb.append("," + sm.avg(60, idx)  );
					//sb.append("," + sm.avg(240, idx)  );
					//sb.append("," + tai.optDouble("pg3") );
					//sb.append("," + tai.optDouble("mg1") );
					//sb.append("," + tai.optDouble("mg2") );
					//sb.append("," + tai.optDouble("pg") );
					sb.append("," + tai.optDouble("pg1") );
					sb.append("," + tai.optDouble("pg2") );
					sb.append("," + tai.optDouble("pg3") );
					//sb.append("," + (tai.optDouble("mg3")+tai.optDouble("sg3"))/2 );
					sb.append("\r\n");
				}
				// System.out.println(row.optJSONObbject("tai60"));
				// System.out.println(row.optJSONObject("tai240"));
				// System.out.println(TextUtils.df("yyyy-MM-dd",
				// row.optString("sdate"))+":::"+nf.format(row.optDouble("k"))+","+nf.format(row.optDouble("d"))
				// +","+row.optDouble("sar"));
				idx++;
			}
			CCData.saveText(new File(App.google+"\\stock\\data\\" + stockId + "-mem.csv"), sb.toString(),
					"UTF-8");
			
			//JSONObject tai3 = FStatBase.stat(sm.data(), ((row)->row.optDouble("vol")) , 20, 720);
			double avg = sm.avg("ssc",2400,sm.data().size()-1);
			System.out.println(avg);
			//IFNValue fn = (row)->Math.log(row.optDouble("mvol")/amem);
			IFNValue fn = (row)->Math.log(row.optDouble("sc")/avg);
			
			System.out.println(FStatBase.stat(sm.data(), fn , 20, sm.data().size()-1));
			System.out.println(FStatBase.stat(sm.data(), fn , 60, sm.data().size()-1));
			System.out.println(FStatBase.stat(sm.data(), fn , 240, sm.data().size()-1));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
