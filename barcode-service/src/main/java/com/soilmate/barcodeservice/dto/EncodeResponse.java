package com.soilmate.barcodeservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncodeResponse {

    private String content;
    private String base64Image;
    private Integer width;
    private Integer height;
}
