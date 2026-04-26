package com.soilmate.barcodeservice.controller;

import com.soilmate.barcodecore.generator.BarcodeGenerator;
import com.soilmate.barcodecore.model.QRCodeGenerateResult;
import com.soilmate.barcodecore.model.QRCodeParseResult;
import com.soilmate.barcodecore.parser.BarcodeParser;
import com.soilmate.barcodeservice.dto.DecodeRequest;
import com.soilmate.barcodeservice.dto.DecodeResponse;
import com.soilmate.barcodeservice.dto.EncodeRequest;
import com.soilmate.barcodeservice.dto.EncodeResponse;
import com.soilmate.common.response.ApiResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qrcode")
public class QRCodeController {

    private final BarcodeGenerator barcodeGenerator;
    private final BarcodeParser barcodeParser;

    public QRCodeController(BarcodeGenerator barcodeGenerator, BarcodeParser barcodeParser) {
        this.barcodeGenerator = barcodeGenerator;
        this.barcodeParser = barcodeParser;
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage(@NonNull @RequestParam String content,
                                           @RequestParam(required = false, defaultValue = "300") Integer width,
                                           @RequestParam(required = false, defaultValue = "300") Integer height) {

        QRCodeGenerateResult result = barcodeGenerator.generate(content, width, height);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("inline", "qrcode.png");

        return ResponseEntity.ok()
                .headers(headers)
                .body(result.getBytes());
    }

    @PostMapping("/encode")
    public ApiResponse<EncodeResponse> encode(@RequestBody EncodeRequest request) {

        QRCodeGenerateResult result;

        if (request.getWidth() != null && request.getHeight() != null) {
            result = barcodeGenerator.generate(request.getContent(), request.getWidth(), request.getHeight());
        } else {
            result = barcodeGenerator.generate(request.getContent());
        }

        EncodeResponse response = EncodeResponse.builder()
                .content(result.getContent())
                .base64Image(result.getBase64())
                .width(result.getImage().getWidth())
                .height(result.getImage().getHeight())
                .build();

        return ApiResponse.success(response);
    }

    @PostMapping("/decode")
    public ApiResponse<DecodeResponse> decode(@RequestBody DecodeRequest request) {
        QRCodeParseResult result = barcodeParser.parseBase64(request.getBase64Image());

        DecodeResponse response = DecodeResponse.builder()
                .content(result.getContent())
                .build();

        return ApiResponse.success(response);
    }

}
