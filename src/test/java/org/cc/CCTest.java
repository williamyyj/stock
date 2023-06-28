package org.cc;

import java.io.File;

/**
 * Unit test for simple App.
 */
public class CCTest 
{

    public static String project = "D:\\HHome\\GoogleDrive\\myjob\\resources\\project";
    
    static {
        File f = new  File(project);
        if(!f.exists()){
            project = "G:\\我的雲端硬碟\\myjob\\resources\\project";
        }
    }
    
    public final static String ok="";

}
