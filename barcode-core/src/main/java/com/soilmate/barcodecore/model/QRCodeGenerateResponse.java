package com.soilmate.barcodecore.model;

import lombok.Builder;
import lombok.Data;

import java.awt.image.BufferedImage;

@Data
@Builder
public class QRCodeGenerateResponse {

    private BufferedImage image;
    private byte[] bytes;
    private String base64;
    private String content;
}
