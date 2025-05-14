package io.mosip.resident.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AuthEventDTO {

    @NotBlank
    private String id;

    @NotBlank
    private String transactionId;

    @NotNull
    private List<Long> timestamp; // You may also use a custom converter if needed

    @Valid
    @NotNull
    private AuthDataDTO data;

}

