package org.cc;

import java.io.File;

import org.cc.json.JSONObject;

/**
 * Hello world!
 *
 */
public class App 
{
    public static JSONObject $app = new JSONObject();

    public static String  google = "G:\\我的雲端硬碟";

    static {
        File f = new File(google);
     
        if(!f.exists()){
            google="/Users/william/Google 雲端硬碟";
            f = new File(google);

        }
        
        if(!f.exists()){
        	google="D:\\HHome\\GoogleDrive";
        	f = new File(google);
        }
        System.out.println("===== google path:"+google);
    }

    public static String  gstock = google+"/mydata/stock";

    public static String  project = google+"/myjob/resources/project";

    public static String base = project+"/stock";
    
    static {
        $app.put("$gstock",gstock);
        $app.put("$base",base);
    }
    

}
