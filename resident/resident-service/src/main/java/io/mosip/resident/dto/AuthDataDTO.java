package io.mosip.resident.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthDataDTO {

    @NotBlank
    private String transactionID;

    @NotBlank
    private String requestdatetime;

    @NotBlank
    private String authtypeCode;

    @NotBlank
    private String statusCode;

    @NotBlank
    private String statusComment;

    @NotBlank
    private String referenceIdType;

    @NotBlank
    private String entityName;

    private String requestSignature;

    @NotBlank
    private String responseSignature;

    @Pattern(regexp = "\\d{32}", message = "Token ID must be a 32-digit numeric string")
    private String tokenId;

    @NotBlank
    private String entityId;

    @Pattern(regexp = "^[A-Fa-f0-9]{64}$", message = "individualId must be a 64-character hex string")
    private String individualId;

}

