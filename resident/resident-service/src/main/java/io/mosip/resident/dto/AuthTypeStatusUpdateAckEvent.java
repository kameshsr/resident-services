package io.mosip.resident.dto;

import java.time.LocalDateTime;
import java.util.Map;

import io.mosip.resident.service.websub.dto.EventInterface;
import lombok.Data;

/**
 * Instantiates a new auth type status update acknowledge event.
 */
@Data
public class AuthTypeStatusUpdateAckEvent implements EventInterface {

    /** The id. */
    private String id;

    /** The request id. */
    private String requestId;

    /** The status. */
    private String status;

    /** The timestamp. */
    private LocalDateTime timestamp;

    /** The data. */
    private Map<String, Object> data;

}

