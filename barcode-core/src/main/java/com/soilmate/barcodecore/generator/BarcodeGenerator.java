package com.soilmate.barcodecore.generator;

import com.soilmate.barcodecore.model.QRCodeGenerateRequest;
import com.soilmate.barcodecore.model.QRCodeGenerateResult;

public interface BarcodeGenerator {

    QRCodeGenerateResult generate(QRCodeGenerateRequest request);

    default QRCodeGenerateResult generate(String content) {
        return generate(QRCodeGenerateRequest.builder().content(content).build());
    }

    default QRCodeGenerateResult generate(String content, int width, int height) {
        return generate(QRCodeGenerateRequest.builder()
                .content(content)
                .width(width)
                .height(height)
                .build());
    }

}
