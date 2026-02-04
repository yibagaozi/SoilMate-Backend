package com.soilmate.barcodeservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecodeResponse {

    private String content;
}
