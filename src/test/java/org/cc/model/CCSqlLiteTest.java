package org.cc.model;

import org.cc.App;
import org.cc.json.JSONObject;


public class CCSqlLiteTest {
 
    public void test_insert() {
        String base = App.base;
        try (CCProcObject proc = new CCProcObject(base, false)){

           JSONObject jo = new JSONObject("{stockid:9901,sdate:'2021-05-05 13:05:01',so:100,sh:102,sl:99,sc:101}");
           proc.put("$",jo);
           //CCProcUtils.exec(proc, "proc.dao.BiDaoRowAdd@twse,mstock");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void test_row(){
        String base = App.base;
        try (CCProcObject proc = new CCProcObject(base, false)){
            ICCModule cm = proc.module("twse");
            CCActObject ao = new CCActObject(cm, "chkDate");
            System.out.println(ao.cfg());
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
