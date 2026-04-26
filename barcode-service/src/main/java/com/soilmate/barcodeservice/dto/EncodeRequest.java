package com.soilmate.barcodeservice.dto;

import lombok.Data;

@Data
public class EncodeRequest {

    private String content;
    private Integer width;
    private Integer height;
}
