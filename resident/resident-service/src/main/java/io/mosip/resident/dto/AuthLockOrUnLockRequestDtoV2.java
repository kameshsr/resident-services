package io.mosip.resident.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@JsonPropertyOrder({ "authTypes" })
public class AuthLockOrUnLockRequestDtoV2 implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<AuthTypeStatusDtoV2> authTypes;

}