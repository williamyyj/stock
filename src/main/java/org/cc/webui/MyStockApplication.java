package org.cc.webui;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@Configurable
@ComponentScan("org.cc.ctr")
@SpringBootApplication
public class MyStockApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyStockApplication.class, args);
    }
}
