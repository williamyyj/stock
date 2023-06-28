package org.cc.stock;

import java.io.File;

import org.cc.App;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

public class TSModelTest {
	
	public static void main(String[] args) throws Exception  {
		String dp = App.google+"\\mydata\\stock\\twotc";
		
		
		File f = new File(dp,"00888.csv");
		Table tb = Table.read().csv(f);
        String xColumn = "sdate";
        String[] data = { "sc", "sh", "sl"};
       // CategoricalColumn categoryColumn = CategoricalColumn.create("Pets", data);

        // 繪製折線圖
        Plot.show(TSModelTest.create("Stock Prices", tb, xColumn, "sc", data ));
 
	}
	
	public static Figure create(
			String title, Table table, String xCol, String yCol , String[] groupCol ) {
		    Layout layout = Layout.builder(title, xCol, yCol).showLegend(true).build();
		    ScatterTrace[] traces = new ScatterTrace[groupCol.length];
		    for (int i = 0; i < groupCol.length; i++) {
		      traces[i] =
		          ScatterTrace.builder( table.column(xCol), table.column(groupCol[i]))
		              .showLegend(true)
		              .name(groupCol[i])
		              .mode(ScatterTrace.Mode.LINE)
		              .build();
		    }
		    return new Figure(layout, traces);
		  }
}
