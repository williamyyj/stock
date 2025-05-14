package org.cc.stock.model;

import java.util.ArrayList;

import org.cc.json.JSONObject;

public class DateDataList extends ArrayList<JSONObject> {

    private StockModel sm;

    public DateDataList(StockModel sm) {
        this.sm = sm;
        __init_list();
    }
    
    private void __init_list() {
    	
    }

}
