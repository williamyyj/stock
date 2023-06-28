package org.cc.ctr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    
    @GetMapping(value="/hello" , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String hello(){
        return "<h1>中文OK！！！！</h1>";
    }
}
