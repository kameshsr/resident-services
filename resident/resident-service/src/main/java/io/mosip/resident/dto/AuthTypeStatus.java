/**
 * 
 */
package io.mosip.resident.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author M1022006
 *
 */
@Data
public class AuthTypeStatus {

	private String authSubType;

	private String authType;

	private boolean locked;

	private Long unlockForSeconds;

	private String requestId;

	private Map<String, Object> metadata;

	public boolean getLocked() {
		return locked;
	}
}
