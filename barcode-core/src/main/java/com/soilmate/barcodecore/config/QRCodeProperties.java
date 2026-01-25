package com.soilmate.barcodecore.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix="soilmate.qrcode")
@ComponentScan("com.soilmate.barcodecore")
public class QRCodeProperties {

    private int width = 300;
    private int height = 300;
    private String charset = "UTF-8";
    private int margin = 1;
    private String errorCorrectionLevel = "M";
    private String imageFormat = "PNG";

}
