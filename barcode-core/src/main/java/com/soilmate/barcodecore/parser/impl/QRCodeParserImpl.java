package com.soilmate.barcodecore.parser.impl;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.soilmate.barcodecore.config.QRCodeProperties;
import com.soilmate.barcodecore.model.QRCodeParseResult;
import com.soilmate.barcodecore.parser.BarcodeParser;
import com.soilmate.common.enums.ErrorCode;
import com.soilmate.common.exception.BarcodeException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class QRCodeParserImpl implements BarcodeParser {

    private final QRCodeProperties properties;

    public QRCodeParserImpl(QRCodeProperties properties) {
        this.properties = properties;
    }

    @Override
    public QRCodeParseResult parse(BufferedImage image) {
        if (image == null) {
            throw new BarcodeException(ErrorCode.BARCODE_IMAGE_EMPTY);
        }

        long startTime = System.currentTimeMillis();

        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType, Object> hints = buildHints();
            Result result = new MultiFormatReader().decode(bitmap, hints);

            return QRCodeParseResult.builder()
                    .content(result.getText())
                    .costTime(System.currentTimeMillis() - startTime)
                    .build();

        } catch (NotFoundException e) {
            throw new BarcodeException(ErrorCode.BARCODE_PARSE_FAILED, e);
        }
    }

    @Override
    public QRCodeParseResult parse(InputStream inputStream) {
        if (inputStream == null) {
            throw new BarcodeException(ErrorCode.BARCODE_IMAGE_EMPTY);
        }

        try {
            BufferedImage image = ImageIO.read(inputStream);
            return parse(image);
        } catch (IOException e) {
            throw new BarcodeException(ErrorCode.BARCODE_IMAGE_READ_FAILED, e);
        }
    }

    @Override
    public QRCodeParseResult parse(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new BarcodeException(ErrorCode.BARCODE_IMAGE_EMPTY);
        }

        return parse(new ByteArrayInputStream(bytes));
    }

    @Override
    public QRCodeParseResult parseBase64(String base64) {
        if (base64 == null || base64.isBlank()) {
            throw new BarcodeException(ErrorCode.BARCODE_BASE64_EMPTY);
        }
        
        try {
            String data = base64.contains(",") ? base64.split(",")[1] : base64;
            byte[] bytes = Base64.getDecoder().decode(data);
            return parse(bytes);
        } catch (IllegalArgumentException e) {
            throw new BarcodeException(ErrorCode.BARCODE_BASE64_INVALID, e);
        }
    }

    private Map<DecodeHintType, Object> buildHints() {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, properties.getCharset());
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        return hints;
    }

}
