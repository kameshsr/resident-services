package io.mosip.resident.service.impl;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.dto.AuthtypeStatus;
import io.mosip.resident.service.UpdateAuthtypeStatusService;
import io.mosip.resident.service.websub.impl.AuthTypeStatusEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The Class UpdateAuthtypeStatusServiceImpl.
 *
 * @author Kamesh Shekhar Prasad
 */
@Component
public class UpdateAuthtypeStatusServiceImpl implements UpdateAuthtypeStatusService {

	private static Logger mosipLogger = LoggerConfiguration.logConfig(UpdateAuthtypeStatusServiceImpl.class);

	private static final String STAUTS_LOCKED = "LOCKED";

	private static final String STATUS_UNLOCKED = "UNLOCKED";

	/** The Constant UNLOCK_EXP_TIMESTAMP. */
	private static final String UNLOCK_EXP_TIMESTAMP = "unlockExpiryTimestamp";

	@Autowired
	private AuthTypeStatusEventPublisher authTypeStatusEventPublisherManager;

	/**
	 * Update auth type status.
	 *
	 * @param tokenId            the token id
	 * @param authTypeStatusList the auth type status list
	*/


	@Override
	public void updateAuthTypeStatus(String tokenId, List<AuthtypeStatus> authTypeStatusList , String partnerId, String authTypeStatusUpdateAck)
			 {
//		List<Entry<String, AuthtypeLock>> entitiesForRequestId = authTypeStatusList.stream()
//				.map(authTypeStatus -> new SimpleEntry<>(authTypeStatus.getRequestId(),
//						this.putAuthTypeStatus(authTypeStatus, tokenId)))
//				.collect(Collectors.toList());
//		List<AuthtypeLock> entities = entitiesForRequestId.stream().map(Entry::getValue).collect(Collectors.toList());
//		entities.forEach(entity -> authLockRepository.findByTokenAndAuthtypecode(tokenId, entity.getAuthtypecode())
//				.forEach(authLockRepository::delete));
//		authLockRepository.saveAll(entities);

//		entitiesForRequestId.stream().forEach(entry -> {
//			String requestId = entry.getKey();
//			if (requestId != null) {
//				AuthtypeLock authtypeLock = entry.getValue();
//				String status = Boolean.valueOf(authtypeLock.getStatuscode()) ? STAUTS_LOCKED : STATUS_UNLOCKED;
				authTypeStatusEventPublisherManager.publishEvent(authTypeStatusUpdateAck, "123", LocalDateTime.now());
//			} else {
//				mosipLogger.error("requestId is null; Websub Notification for {} topic is not sent.", AUTH_TYPE_STATUS_ACK_TOPIC);
//			}
//		});
	}

	/**
	 * Put auth type status.
	 *
	 * @param authtypeStatus the authtype status
	 * @param token          the token
	 * @return the authtype lock
	 */
//	private AuthtypeLock putAuthTypeStatus(AuthtypeStatus authtypeStatus, String token) {
//		AuthtypeLock authtypeLock = new AuthtypeLock();
//		authtypeLock.setToken(token);
//		String authType = authtypeStatus.getAuthType();
//		if (authType.equalsIgnoreCase(Category.BIO.getType())) {
//			authType = authType + "-" + authtypeStatus.getAuthSubType();
//		}
//		authtypeLock.setAuthtypecode(authType);
//		LocalDateTime currentDtime = DateUtils.getUTCCurrentDateTime();
//		authtypeLock.setLockrequestDTtimes(currentDtime);
//		authtypeLock.setLockstartDTtimes(currentDtime);
//		if (Objects.nonNull(authtypeStatus.getMetadata())
//				&& authtypeStatus.getMetadata().containsKey(UNLOCK_EXP_TIMESTAMP)) {
//			authtypeLock.setUnlockExpiryDTtimes(
//					DateUtils.parseToLocalDateTime((String) authtypeStatus.getMetadata().get(UNLOCK_EXP_TIMESTAMP)));
//		}
//		authtypeLock.setStatuscode(Boolean.toString(authtypeStatus.getLocked()));
//		authtypeLock.setCreatedBy(EnvUtil.getAppId());
//		authtypeLock.setCrDTimes(currentDtime);
//		authtypeLock.setLangCode(IdAuthCommonConstants.NA);
//		return authtypeLock;
//	}


}
