package com.soilmate.barcodecore.parser;

import com.soilmate.barcodecore.config.QRCodeProperties;
import com.soilmate.barcodecore.generator.BarcodeGenerator;
import com.soilmate.barcodecore.generator.impl.QRCodeGeneratorImpl;
import com.soilmate.barcodecore.model.QRCodeGenerateResult;
import com.soilmate.barcodecore.model.QRCodeParseResult;
import com.soilmate.barcodecore.parser.impl.QRCodeParserImpl;
import com.soilmate.common.exception.BarcodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QRCodeParserImpl Test")
public class QRCodeParserImplTest {
    
    private BarcodeParser parser;
    private BarcodeGenerator generator;
    private QRCodeProperties properties;
    
    @BeforeEach
    void setUp() {
        properties = new QRCodeProperties();
        parser = new QRCodeParserImpl(properties);
        generator = new QRCodeGeneratorImpl(properties);
    }
    
    @Nested
    @DisplayName("Success Cases")
    class SuccessCases {
        
        @Test
        @DisplayName("Should parse BufferedImage")
        void shouldParseBufferedImage() {
            // Given
            String content = "PLT-00001-A1";
            QRCodeGenerateResult generated = generator.generate(content);
            
            // When
            QRCodeParseResult result = parser.parse(generated.getImage());
            
            // Then
            assertEquals(content, result.getContent());
            assertTrue(result.getCostTime() >= 0);
        }
        
        @Test
        @DisplayName("Should parse byte array")
        void shouldParseByteArray() {
            // Given
            String content = "PLT-00002-B2";
            QRCodeGenerateResult generated = generator.generate(content);
            
            // When
            QRCodeParseResult result = parser.parse(generated.getBytes());
            
            // Then
            assertEquals(content, result.getContent());
        }
        
        @Test
        @DisplayName("Should parse InputStream")
        void shouldParseInputStream() {
            // Given
            String content = "PLT-00003-C3";
            QRCodeGenerateResult generated = generator.generate(content);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(generated.getBytes());
            
            // When
            QRCodeParseResult result = parser.parse(inputStream);
            
            // Then
            assertEquals(content, result.getContent());
        }
        
        @Test
        @DisplayName("Should parse Base64 string")
        void shouldParseBase64() {
            // Given
            String content = "PLT-00004-D4";
            QRCodeGenerateResult generated = generator.generate(content);
            
            // When
            QRCodeParseResult result = parser.parseBase64(generated.getBase64());
            
            // Then
            assertEquals(content, result.getContent());
        }
        
        @Test
        @DisplayName("Should parse Base64 with data URI prefix")
        void shouldParseBase64WithPrefix() {
            // Given
            String content = "PLT-00005-E5";
            QRCodeGenerateResult generated = generator.generate(content);
            String base64WithPrefix = "data:image/png;base64," + generated.getBase64();
            
            // When
            QRCodeParseResult result = parser.parseBase64(base64WithPrefix);
            
            // Then
            assertEquals(content, result.getContent());
        }
    }
    
    @Nested
    @DisplayName("Failure Cases")
    class FailureCases {
        
        @Test
        @DisplayName("Should throw exception when image is null")
        void shouldThrowWhenImageIsNull() {
            // When & Then
            BarcodeException exception = assertThrows(
                    BarcodeException.class,
                    () -> parser.parse((BufferedImage) null)
            );
            assertEquals("BARCODE_004", exception.getErrorCode().getCode());
        }
        
        @Test
        @DisplayName("Should throw exception when byte array is null")
        void shouldThrowWhenBytesIsNull() {
            // When & Then
            assertThrows(BarcodeException.class, () -> parser.parse((byte[]) null));
        }
        
        @Test
        @DisplayName("Should throw exception when Base64 is null")
        void shouldThrowWhenBase64IsNull() {
            // When & Then
            BarcodeException exception = assertThrows(
                    BarcodeException.class,
                    () -> parser.parseBase64(null)
            );
            assertEquals("BARCODE_008", exception.getErrorCode().getCode());
        }

        @Test
        @DisplayName("Should throw exception when Base64 is invalid")
        void shouldThrowWhenBase64IsInvalid() {
            // When & Then
            BarcodeException exception = assertThrows(
                    BarcodeException.class,
                    () -> parser.parseBase64("invalid-base64!!!")
            );
            assertEquals("BARCODE_009", exception.getErrorCode().getCode());
        }
        
        @Test
        @DisplayName("Should throw exception when no QR code in image")
        void shouldThrowWhenNoQRCodeInImage() {
            // Given
            BufferedImage blankImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            
            // When & Then
            BarcodeException exception = assertThrows(
                    BarcodeException.class,
                    () -> parser.parse(blankImage)
            );
            assertEquals("BARCODE_007", exception.getErrorCode().getCode());
        }
    }
}
