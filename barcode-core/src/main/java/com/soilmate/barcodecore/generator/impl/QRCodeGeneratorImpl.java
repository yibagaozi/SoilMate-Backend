package com.soilmate.barcodecore.generator.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.soilmate.barcodecore.config.QRCodeProperties;
import com.soilmate.barcodecore.generator.BarcodeGenerator;
import com.soilmate.barcodecore.model.QRCodeGenerateRequest;
import com.soilmate.barcodecore.model.QRCodeGenerateResult;
import com.soilmate.common.enums.ErrorCode;
import com.soilmate.common.exception.BarcodeException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class QRCodeGeneratorImpl implements BarcodeGenerator {

    private final QRCodeProperties properties;

    public QRCodeGeneratorImpl(QRCodeProperties properties) {
        this.properties = properties;
    }

    @Override
    public QRCodeGenerateResult generate(QRCodeGenerateRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BarcodeException(ErrorCode.BARCODE_CONTENT_EMPTY);
        }

        try {
            int width = getOrDefault(request.getWidth(), properties.getWidth());
            int height = getOrDefault(request.getHeight(), properties.getHeight());
            
            if (width <= 0 || height <= 0) {
                throw new BarcodeException(ErrorCode.PARAM_ERROR);
            }

            Map<EncodeHintType, Object> hints = buildHints(request);

            BitMatrix matrix = new MultiFormatWriter().encode(
                    request.getContent(), BarcodeFormat.QR_CODE, width, height, hints);

            BufferedImage image = toImage(matrix, request);

            return buildResult(image, request.getContent());

        } catch (WriterException e) {
            throw new BarcodeException(ErrorCode.BARCODE_GENERATE_FAILED, e);
        }
    }

    private Map<EncodeHintType, Object> buildHints(QRCodeGenerateRequest request) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, properties.getCharset());
        hints.put(EncodeHintType.MARGIN, getOrDefault(request.getMargin(), properties.getMargin()));
        hints.put(EncodeHintType.ERROR_CORRECTION, parseErrorLevel(
                getOrDefault(request.getErrorCorrectionLevel(), properties.getErrorCorrectionLevel())));
        return hints;
    }

    private BufferedImage toImage(BitMatrix matrix, QRCodeGenerateRequest request) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ?
                        request.getForegroundColor() : request.getBackgroundColor());
            }
        }
        return image;
    }

    private QRCodeGenerateResult buildResult(BufferedImage image, String content) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, properties.getImageFormat(), baos);
            byte[] bytes = baos.toByteArray();

            return QRCodeGenerateResult.builder()
                    .image(image)
                    .bytes(bytes)
                    .base64(Base64.getEncoder().encodeToString(bytes))
                    .content(content)
                    .build();
        } catch (IOException e) {
            throw new BarcodeException(ErrorCode.BARCODE_IMAGE_CONVERT_FAILED, e);
        }
    }

    private ErrorCorrectionLevel parseErrorLevel(String level) {
        return switch (level.toUpperCase()) {
            case "L" -> ErrorCorrectionLevel.L;
            case "Q" -> ErrorCorrectionLevel.Q;
            case "H" -> ErrorCorrectionLevel.H;
            default -> ErrorCorrectionLevel.M;
        };
    }

    private <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
