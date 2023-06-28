package org.cc.fun.csv;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.cc.IAProxyClass;
import org.cc.json.JSONObject;
import org.cc.model.CCActObject;
import org.cc.model.CCCMParams;
import org.cc.model.CCField;
import org.cc.model.CCFunc;
import org.cc.model.CCModule;
import org.cc.model.CCProcObject;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
/**
 * proc.params().put("path","...")
 * CCProcUtils.exec(proc, "csv.reader@otc,load");
 * @author 94017
 *
 */
@IAProxyClass(id="csv.reader")
public class BiCSVReader implements BiFunction<CCProcObject, String, List<JSONObject>> {

	@Override
	public List<JSONObject> apply(CCProcObject proc, String cmdString) {
		List<JSONObject> ret = new ArrayList<>();
	     CCCMParams cmp = CCCMParams.newInstance(cmdString);
	     CCModule m = proc.module(cmp.mid());
	     CCActObject act = m.act(cmp.aid());
	     List<CCField> flds = act.fields();
	     Path p = (Path) proc.params().opt("path");
	 	try (CsvReader csv = CsvReader.builder().build(p,Charset.forName("UTF-8"))) {
			int idx = 0; 
			CsvRow head = null;
	 		for (CsvRow row : csv) {
				 if(idx==0) {
					 head = row;
				 } else {
					 ret.add(proc_set_jo(flds,row));
				 }
				 idx++;
			 }
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	
		return ret;
	}

	private JSONObject proc_set_jo(CsvRow head, CsvRow row) {
		JSONObject jo = new JSONObject();
		for(int i=0;i<head.getFieldCount();i++) {
			jo.put(head.getField(i), row.getField(i));
		}
		return jo;
	}
	
	private JSONObject proc_set_jo(List<CCField> flds, CsvRow row) {
		JSONObject jo = new JSONObject();
		for(CCField fld:flds) {
			Object fv = null;
			if(fld.has("cast")) {
				// cast using cast funtion
				fv = CCFunc.apply(fld.optString("cast"), row.getField(fld.optInt("idx")));
			} else {
				// cast data by type
				fv = row.getField(fld.optInt("idx"));
				fv = fld.type().value(fv); 
			}
			jo.put(fld.id(), fv);
		}
		return jo;
	}

}
