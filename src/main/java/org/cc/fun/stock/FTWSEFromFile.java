package org.cc.fun.stock;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiFunction;

import org.cc.json.CCCache;
import org.cc.json.JSONObject;
import org.cc.model.CCActObject;
import org.cc.model.CCCMParams;
import org.cc.model.CCModule;
import org.cc.model.CCProcObject;

/**
 *
 * @author william
 */
public class FTWSEFromFile implements BiFunction<CCProcObject, String, JSONObject> {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Override
    public JSONObject apply(CCProcObject proc, String line) {
        CCCMParams cmp = CCCMParams.newInstance(line);
        CCModule cm = proc.module(cmp.mid());
        CCActObject act = cm.act(cmp.aid());
        JSONObject cfg = act.cfg();
        Date dp1 = proc.params().optDate("dp1");
        if (dp1 != null) {
            File f = new File(cfg.optString("$targetPath"), sdf.format(dp1) + ".json");
            if (f.exists()) {
                return CCCache.load(f);
            }
        }
        return null;
    }

}
