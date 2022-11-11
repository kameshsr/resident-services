package io.mosip.resident.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessResponseDto {

	private String transactionID;

	private String trackingId;
}
