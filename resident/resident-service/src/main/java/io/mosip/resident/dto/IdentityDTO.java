package io.mosip.resident.dto;

import lombok.Data;

@Data
public class IdentityDTO {

	private String UIN;
	private String email;
	private String phone;
	private String yearOfBirth;
	private String fullName;
//	private Map<String, Object> individualBiometrics;

}
