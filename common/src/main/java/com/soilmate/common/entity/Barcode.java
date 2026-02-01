package com.soilmate.common.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing the barcode information table.
 *
 * <p>Barcode format: {@code PLT-XXXXX-XX} where:
 * <ul>
 *   <li>{@code PLT} - Fixed prefix identifying plant barcodes</li>
 *   <li>{@code XXXXX} - 5-digit zero-padded plant identifier</li>
 *   <li>{@code XX} - 2-character alphanumeric check digits</li>
 * </ul>
 * </p>
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Barcode {

    /**
     * The unique identifier for this barcode record.
     */
    private Long id;

    /**
     * The encoded barcode string value.
     */
    private String barcodeValue;

    /**
     * The ID of the associated plant species.
     */
    private Long plantId;

    /**
     * The production batch number for this barcode.
     */
    private String batchNumber;

    /**
     * The timestamp when this barcode record was created.
     */
    private LocalDateTime createdAt;
}
