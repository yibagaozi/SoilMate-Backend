package com.soilmate.barcodecore.generator;

import com.soilmate.barcodecore.config.QRCodeProperties;
import com.soilmate.barcodecore.generator.impl.QRCodeGeneratorImpl;
import com.soilmate.barcodecore.model.QRCodeGenerateRequest;
import com.soilmate.barcodecore.model.QRCodeGenerateResult;
import com.soilmate.common.exception.BarcodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QRCodeGeneratorImpl Test")
public class QRCodeGeneratorImplTest {

    private BarcodeGenerator generator;
    private QRCodeProperties properties;

    @BeforeEach
    void setUp() {
        properties = new QRCodeProperties();
        generator = new QRCodeGeneratorImpl(properties);
    }

    @Nested
    @DisplayName("Success Cases")
    class SuccessCases {

        @Test
        @DisplayName("Should generate QR code with default config")
        void shouldGenerateWithDefaultConfig() {
            // Given
            String content = "PLT-00001-A1";

            // When
            QRCodeGenerateResult result = generator.generate(content);

            // Then
            assertNotNull(result);
            assertNotNull(result.getImage());
            assertNotNull(result.getBytes());
            assertNotNull(result.getBase64());
            assertEquals(content, result.getContent());
            assertEquals(properties.getWidth(), result.getImage().getWidth());
            assertEquals(properties.getHeight(), result.getImage().getHeight());
        }

        @Test
        @DisplayName("Should generate QR code with custom size")
        void shouldGenerateWithCustomSize() {
            // Given
            String content = "PLT-00002-B2";
            int width = 500;
            int height = 500;

            // When
            QRCodeGenerateResult result = generator.generate(content, width, height);

            // Then
            assertEquals(width, result.getImage().getWidth());
            assertEquals(height, result.getImage().getHeight());
        }

        @Test
        @DisplayName("Should generate QR code with full request")
        void shouldGenerateWithFullRequest() {
            // Given
            QRCodeGenerateRequest request = QRCodeGenerateRequest.builder()
                    .content("PLT-00003-C3")
                    .width(400)
                    .height(400)
                    .margin(2)
                    .errorCorrectionLevel("H")
                    .build();

            // When
            QRCodeGenerateResult result = generator.generate(request);

            // Then
            assertNotNull(result);
            assertEquals(400, result.getImage().getWidth());
            assertEquals("PLT-00003-C3", result.getContent());
        }

        @Test
        @DisplayName("Should generate decodable Base64")
        void shouldGenerateDecodableBase64() throws IOException {
            // Given
            String content = "PLT-00004-D4";

            // When
            QRCodeGenerateResult result = generator.generate(content);
            byte[] decoded = Base64.getDecoder().decode(result.getBase64());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(decoded));

            // Then
            assertNotNull(image);
            assertEquals(result.getImage().getWidth(), image.getWidth());
        }
    }

    @Nested
    @DisplayName("Failure Cases")
    class FailureCases {

        @Test
        @DisplayName("Should throw exception when content is null")
        void shouldThrowWhenContentIsNull() {
            // Given
            QRCodeGenerateRequest request = QRCodeGenerateRequest.builder()
                    .content(null)
                    .build();

            // When & Then
            BarcodeException exception = assertThrows(
                    BarcodeException.class,
                    () -> generator.generate(request)
            );
            assertEquals("BARCODE_001", exception.getErrorCode().getCode());
        }

        @Test
        @DisplayName("Should throw exception when content is empty")
        void shouldThrowWhenContentIsEmpty() {
            // Given
            QRCodeGenerateRequest request = QRCodeGenerateRequest.builder()
                    .content("")
                    .build();

            // When & Then
            assertThrows(BarcodeException.class, () -> generator.generate(request));
        }

        @Test
        @DisplayName("Should throw exception when content is blank")
        void shouldThrowWhenContentIsBlank() {
            // Given
            QRCodeGenerateRequest request = QRCodeGenerateRequest.builder()
                    .content("   ")
                    .build();

            // When & Then
            assertThrows(BarcodeException.class, () -> generator.generate(request));
        }
    }

}
