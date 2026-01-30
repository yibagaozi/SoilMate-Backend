package com.soilmate.barcodecore.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QRCodeParseResult {

    private String content;
    private long costTime;
}
