package org.cc.tablesaw;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.cc.data.CCData;
import org.junit.jupiter.api.Test;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

public class TestTablesawExcel {
	
	
	public void test_load_xlsx() throws IOException {
		String base = "G:\\我的雲端硬碟\\myjob\\resources\\prj\\baphiq_data\\export\\";
		String fname = "致POS系統公司-111年申請友善資材補助農友名單.xlsx";
		XlsxReadOptions options = XlsxReadOptions.builder(base+fname).build();
		XlsxReader reader = new XlsxReader();
		List<Table> tables = reader.readMultiple(options);
		Table tab = tables.get(0);
		for(int i = 0; i < tab.rowCount(); i++) {
			int idx = tab.intColumn("編號").getInt(i);
			String name = tab.stringColumn("班員名稱").get(i);
			String id = tab.stringColumn("身分證號").get(i);
			System.out.println(idx+":"+name+"--->"+id);
		}

	}
	
	@Test
	public void test_load_csv() throws IOException {
		String base = "G:\\我的雲端硬碟\\myjob\\resources\\prj\\baphiq_data\\export\\";
		String fname = "20250425-生物農藥.csv";
	     try {
	            // 从 CSV 文件中读取数据
	    	 	String insSql="insert into rpt2ApplyFarmer (id, name) values ('%s',N'%s');\r\n";
	    	 	StringBuilder sb = new StringBuilder();
	            Table table = Table.read().csv(base+fname);
	            for(int i=0;i<table.rowCount();i++) {
	            	String id = table.getString(i, "id");
	            	String name = table.getString(i, "name");
	            	sb.append(String.format(insSql, id,name));
	            }
	            CCData.saveText(new File(base,"20250425-生物農藥_rpt2ApplyFarmer.sql"), sb.toString(), "UTF-8");
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	}
}
