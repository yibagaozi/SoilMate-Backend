package com.soilmate.webservice.dto.request;

import lombok.Data;

/**
 * Request for skipping a care task.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
public class SkipTaskRequest {

    private String reason;
}
