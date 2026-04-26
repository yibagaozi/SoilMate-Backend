package com.soilmate.webservice.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("barcode")
public class Barcode {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String barcodeValue;

    private Long plantId;

    private String batchNumber;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
