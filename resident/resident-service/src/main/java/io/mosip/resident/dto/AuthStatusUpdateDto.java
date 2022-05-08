package io.mosip.resident.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AuthStatusUpdateDto {

    private String authType;
    private String authSubType;
    private Boolean locked;
    private Long unlockForSeconds;
    private String requestId;
    private Map<String, Object> metadata;
}
