package com.soilmate.barcodecore.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QRCodeGenerateRequest {

    private String content;

    private Integer width;
    private Integer height;
    private Integer margin;
    private String errorCorrectionLevel;

    @Builder.Default
    private int foregroundColor = 0xFF000000;

    @Builder.Default
    private int backgroundColor = 0xFFFFFFFF;
}
