package com.soilmate.webservice.dto.request;

import lombok.Data;

/**
 * Request for completing a care task.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
public class CompleteTaskRequest {

    private Integer waterAmountMl;

    private String notes;
}
