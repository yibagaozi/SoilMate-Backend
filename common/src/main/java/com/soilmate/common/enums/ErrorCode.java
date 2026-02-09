package com.soilmate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of all error codes used throughout the SoilMate application.
 *
 * <p>Each error code consists of a unique code string and a message.
 * The code is returned in API error responses for client-side error handling.</p>
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    SYSTEM_ERROR("SYSTEM_001", "System error"),
    PARAM_ERROR("SYSTEM_002", "Invalid parameter"),

    // Authentication
    UNAUTHORIZED("AUTH_001", "Unauthorized or session expired"),
    ACCESS_DENIED("AUTH_002", "Access denied"),
    TOKEN_MISSING("AUTH_003", "Token is missing"),
    TOKEN_INVALID("AUTH_004", "Token is invalid"),
    TOKEN_EXPIRED("AUTH_005", "Token has expired"),
    TOKEN_MALFORMED("AUTH_006", "Token is malformed"),
    TOKEN_SIGNATURE_INVALID("AUTH_007", "Token signature is invalid"),
    TOKEN_TYPE_INVALID("AUTH_008", "Invalid token type"),
    REFRESH_TOKEN_EXPIRED("AUTH_009", "Refresh token has expired"),
    REFRESH_TOKEN_INVALID("AUTH_010", "Refresh token is invalid"),
    WRONG_CREDENTIALS("AUTH_011", "Wrong email or password"),

    // Resource
    RESOURCE_NOT_FOUND("RESOURCE_001", "Resource not found"),
    PLANT_NOT_FOUND("RESOURCE_002", "Plant not found"),
    USER_PLANT_NOT_FOUND("RESOURCE_003", "User plant not found"),
    BARCODE_NOT_FOUND("RESOURCE_004", "Barcode not found"),

    // Barcode
    BARCODE_CONTENT_EMPTY("BARCODE_001", "QR code content cannot be empty"),
    BARCODE_GENERATE_FAILED("BARCODE_002", "Failed to generate QR code"),
    BARCODE_IMAGE_CONVERT_FAILED("BARCODE_003", "Failed to convert image"),
    BARCODE_IMAGE_EMPTY("BARCODE_004", "Image cannot be empty"),
    BARCODE_IMAGE_READ_FAILED("BARCODE_005", "Failed to read image"),
    BARCODE_IMAGE_FORMAT_INVALID("BARCODE_006", "Invalid image format"),
    BARCODE_PARSE_FAILED("BARCODE_007", "No QR code detected"),
    BARCODE_BASE64_EMPTY("BARCODE_008", "Base64 string cannot be empty"),
    BARCODE_BASE64_INVALID("BARCODE_009", "Invalid Base64 encoding"),
    BARCODE_FORMAT_INVALID("BARCODE_010", "Invalid barcode format"),

    // User
    USER_NOT_FOUND("USER_001", "User not found"),
    USER_EMAIL_EXISTS("USER_002", "Email already registered"),
    USER_EMAIL_INVALID("USER_003", "Invalid email format"),
    USER_PASSWORD_WEAK("USER_004", "Password must be at least 8 characters"),
    USER_DISPLAY_NAME_REQUIRED("USER_005", "Display name is required"),
    USER_AUTH_NOT_FOUND("USER_006", "Authentication method not found"),
    USER_ALREADY_LINKED("USER_007", "This authentication method is already linked"),

    // Task
    TASK_NOT_FOUND("TASK_001", "Task not found"),
    TASK_ALREADY_PROCESSED("TASK_002", "Task already completed or skipped");

    private final String code;
    private final String message;
}
