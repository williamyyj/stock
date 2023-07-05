package org.cc.stock.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.cc.json.JSONObject;

public class MonthDataList extends ArrayList<JSONObject> {

    private StockModel sm;

    public MonthDataList(StockModel sm) {
        this.sm = sm;
        __init_list();
    }

    private void __init_list() {
        String old = monthString(sm.data().get(0).optDate("sdate"));
        String curr = "";
        JSONObject nrow = null;
        double so = 0.0, sh = 0.0, sl = 0.0, sc = 0.0, sa = 0.0, vol = 0.0;
        for (JSONObject row : sm.data()) {
            curr = monthString(row.optDate("sdate"));
            if (!old.equals(curr)) {
                if (nrow != null) {
                    sa = sa / vol;
                    if(old.length()==0){
                        old = curr;
                    }
                    nrow.put("sdate", old);
                    nrow.put("so", so);
                    nrow.put("sh", sh);
                    nrow.put("sl", sl);
                    nrow.put("sc", sc);
                    nrow.put("sa", sa);
                    nrow.put("vol", vol);
                    add(nrow);
                    old = curr;
                }
                nrow = new JSONObject();
                so = row.optDouble("so");
                sh = Double.MIN_VALUE;
                sl = Double.MAX_VALUE;
                sa = 0.0;
                vol = 0.0;
            }
            sh = (row.optDouble("sh") > sh) ? row.optDouble("sh") : sh;
            sl = (sl > row.optDouble("sl")) ? row.optDouble("sl") : sl;
            sc = row.optDouble("sc");
            sa += sc * row.optDouble("vol");
            vol += row.optDouble("vol");
            // System.out.println(curr+"-->"+sc);
        }
        if (old.equals(curr)) {
            sa = sa / vol;
            nrow = new JSONObject();
            nrow.put("sdate", old);
            nrow.put("so", so);
            nrow.put("sh", sh);
            nrow.put("sl", sl);
            nrow.put("sc", sc);
            nrow.put("sa", sa);
            nrow.put("vol", vol);
            add(nrow);
        }
    }

    public String monthString(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(d) + "-01";
    }
}
