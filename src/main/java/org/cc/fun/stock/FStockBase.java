package org.cc.fun.stock;

import java.util.Date;

import org.cc.model.CCCMParams;
import org.cc.model.CCProcObject;
import org.cc.model.ICCModule;
import org.cc.util.CCDateUtils;

public abstract class FStockBase {

    protected boolean proc_range(CCProcObject proc, String cmdString ){
        CCCMParams cmp = CCCMParams.newInstance(cmdString);    
        ICCModule cm = proc.module(cmp.mid());
        Date dp1 = proc.params().optDate("dp1");
        Date dp2 = proc.params().optDate("dp2");
        dp2 = (dp2 == null) ? new Date() : dp2;
        Date d = (Date) dp1.clone();
        while (d.before(dp2)) {
            try {
                proc_date(proc, cm, d);
    
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            
            d = CCDateUtils.addDate(d, 1);
        }
        return true;
    }
   
    public abstract void proc_date(CCProcObject proc, ICCModule cm, Date d);
   
    
   

}
