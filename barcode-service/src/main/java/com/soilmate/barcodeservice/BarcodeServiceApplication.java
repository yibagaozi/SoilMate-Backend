package com.soilmate.barcodeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.soilmate.barcodeservice", "com.soilmate.barcodecore", "com.soilmate.common"})
public class BarcodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarcodeServiceApplication.class, args);
    }

}
