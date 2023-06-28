package org.cc.stock;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;

import com.sun.rowset.internal.Row;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public class LoadCSVTest {
	
	public static void main(String[] args) throws IOException {
		File base = new File("D:\\HHome\\GoogleDrive\\mydata\\stock\\twotc");
		File f = new File(base,"00888_11204.csv");
		
		Iterator<CsvRow> ite = CsvReader.builder().build(f.toPath()).iterator();
		
		while(ite.hasNext()) {
			CsvRow row = ite.next();
			System.out.println(row);
		}
		
		
	}
	
	 static class otcConsumer implements Consumer<CsvRow> {
		 
		@Override
		public void accept(CsvRow row) {
			System.out.println(row);
			
		}
		 
	 }
	
}
