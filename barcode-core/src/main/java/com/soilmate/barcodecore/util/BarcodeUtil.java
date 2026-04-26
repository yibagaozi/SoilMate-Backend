package com.soilmate.barcodecore.util;

/**
 * Utility for generating and validating SoilMate plant barcodes.
 *
 * <p>Format: {@code PLT-XXXXX-VV}
 * <ul>
 *   <li>{@code PLT} – fixed prefix</li>
 *   <li>{@code XXXXX} – 5-digit zero-padded plant ID (0–99999)</li>
 *   <li>{@code VV} – 2-character alphanumeric check code (base-36)</li>
 * </ul>
 *
 * <p>Check algorithm:
 * <pre>
 *   S  = d₁×1 + d₂×2 + d₃×3 + d₄×4 + d₅×5
 *   C₁ = S mod 36        → base-36 char
 *   C₂ = (S×7 + 13) mod 36 → base-36 char
 * </pre>
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
public final class BarcodeUtil {

    private static final String PREFIX = "PLT";
    private static final char[] BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int MAX_ID = 99999;
    private static final int ID_DIGITS = 5;

    private BarcodeUtil() {}

    /**
     * Generate a full barcode string for the given plant ID.
     *
     * @param plantId plant ID (0 – 99999)
     * @return barcode in format {@code PLT-XXXXX-VV}
     * @throws IllegalArgumentException if plantId is out of range
     */
    public static String generate(int plantId) {
        if (plantId < 0 || plantId > MAX_ID) {
            throw new IllegalArgumentException(
                    "Plant ID must be between 0 and " + MAX_ID + ", got: " + plantId);
        }

        String digits = String.format("%0" + ID_DIGITS + "d", plantId);
        int s = weightedSum(digits);

        char c1 = BASE36[s % 36];
        char c2 = BASE36[(s * 7 + 13) % 36];

        return PREFIX + "-" + digits + "-" + c1 + c2;
    }

    /**
     * Validate a barcode string.
     *
     * @param barcode the full barcode string to check
     * @return {@code true} if the format is correct and check digits match
     */
    public static boolean validate(String barcode) {
        if (barcode == null) {
            return false;
        }

        // Normalize to uppercase for case-insensitive comparison
        String upper = barcode.trim().toUpperCase();

        // Must match PLT-XXXXX-VV (length 12)
        if (upper.length() != 12) {
            return false;
        }
        if (!upper.startsWith(PREFIX + "-") || upper.charAt(9) != '-') {
            return false;
        }

        String digits = upper.substring(4, 9);
        for (int i = 0; i < ID_DIGITS; i++) {
            if (!Character.isDigit(digits.charAt(i))) {
                return false;
            }
        }

        char v1 = upper.charAt(10);
        char v2 = upper.charAt(11);
        if (base36Value(v1) < 0 || base36Value(v2) < 0) {
            return false;
        }

        // Recompute and compare
        int s = weightedSum(digits);
        char expectedC1 = BASE36[s % 36];
        char expectedC2 = BASE36[(s * 7 + 13) % 36];

        return v1 == expectedC1 && v2 == expectedC2;
    }

    // ── internal helpers ──

    /**
     * Position-weighted sum: S = d₁×1 + d₂×2 + d₃×3 + d₄×4 + d₅×5
     */
    private static int weightedSum(String digits) {
        int sum = 0;
        for (int i = 0; i < ID_DIGITS; i++) {
            sum += (digits.charAt(i) - '0') * (i + 1);
        }
        return sum;
    }

    /**
     * Return 0–35 for a valid base-36 char, or -1 if invalid.
     */
    private static int base36Value(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'A' && c <= 'Z') return c - 'A' + 10;
        return -1;
    }
}
