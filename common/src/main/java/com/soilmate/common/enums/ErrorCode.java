package com.soilmate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SYSTEM_ERROR("SYSTEM_001", "System error"),
    PARAM_ERROR("SYSTEM_002", "Invalid parameter"),

    // Authentication
    UNAUTHORIZED("AUTH_001", "Unauthorized or session expired"),
    ACCESS_DENIED("AUTH_002", "Access denied"),

    // Resource
    RESOURCE_NOT_FOUND("RESOURCE_001", "Resource not found"),

    // Barcode
    BARCODE_CONTENT_EMPTY("BARCODE_001", "QR code content cannot be empty"),
    BARCODE_GENERATE_FAILED("BARCODE_002", "Failed to generate QR code"),
    BARCODE_IMAGE_CONVERT_FAILED("BARCODE_003", "Failed to convert image"),
    BARCODE_IMAGE_EMPTY("BARCODE_004", "Image cannot be empty"),
    BARCODE_IMAGE_READ_FAILED("BARCODE_005", "Failed to read image"),
    BARCODE_IMAGE_FORMAT_INVALID("BARCODE_006", "Invalid image format"),
    BARCODE_PARSE_FAILED("BARCODE_007", "No QR code detected"),
    BARCODE_BASE64_EMPTY("BARCODE_008", "Base64 string cannot be empty"),
    BARCODE_BASE64_INVALID("BARCODE_009", "Invalid Base64 encoding");

    private final String code;
    private final String message;
}
