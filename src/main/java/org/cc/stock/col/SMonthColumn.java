package org.cc.stock.col;

import java.util.ArrayList;
import java.util.List;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

public class SMonthColumn extends SBaseColumns {

	private List<JSONObject> monthDataList;
    
	public SMonthColumn(StockModel sm) {
		super(sm);
		__init_columns();
        monthDataList = new ArrayList<>();
	}

    @Override
    protected void __init_columns() {
        for (int i=1;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
            System.out.println();
		}
    }

    @Override
    public void evaluation() {

    }

}
