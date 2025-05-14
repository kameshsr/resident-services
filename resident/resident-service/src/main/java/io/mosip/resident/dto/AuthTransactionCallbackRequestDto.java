package io.mosip.resident.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthTransactionCallbackRequestDto {

    @NotBlank
    private String publisher;

    @NotBlank
    private String topic;

    @NotBlank
    private String publishedOn; // Consider using Instant if you want automatic date binding

    @Valid
    @NotNull
    private AuthEventDTO event;

}
