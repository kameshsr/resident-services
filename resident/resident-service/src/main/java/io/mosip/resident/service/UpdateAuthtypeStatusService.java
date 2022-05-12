
package io.mosip.resident.service;

import io.mosip.resident.dto.AuthtypeStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The Interface UpdateAuthtypeStatusService.
 */
@Service
public interface UpdateAuthtypeStatusService {

	/**
	 * Update auth type status.
	 *
	 * @param tokenId the token id
	 * @param authTypeStatusList the auth type status list
	 */
	public void updateAuthTypeStatus(String tokenId, List<AuthtypeStatus> authTypeStatusList, String partnerId, String authTypeStatusUpdateAck);

}
