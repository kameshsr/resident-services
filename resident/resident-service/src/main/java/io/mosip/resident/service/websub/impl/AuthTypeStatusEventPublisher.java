package io.mosip.resident.service.websub.impl;

import io.mosip.resident.constant.IdAuthCommonConstants;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.dto.AuthTypeStatusUpdateAckEvent;
import io.mosip.resident.service.websub.dto.EventModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static io.mosip.resident.constant.IdAuthConfigKeyConstants.AUTH_TYPE_STATUS_ACK_TOPIC;

/**
 * The Class CredentialStoreStatusEventPublisher.
 * 
 */
@Component
public class AuthTypeStatusEventPublisher extends BaseWebSubEventsInitializer {

	/** The Constant logger. */
	private static final Logger logger = LoggerConfiguration.logConfig(AuthTypeStatusEventPublisher.class);

	/** The credential status update topic. */
	@Value("${" + AUTH_TYPE_STATUS_ACK_TOPIC + "}")
	private String authTypeStatusAcknlowedgeTopic;
	
	/**
	 * Do subscribe.
	 */
	@Override
	protected void doSubscribe() {
		//Nothing to do here since we are just publishing event for this topic
	}

	/**
	 * Try register topic partner service events.
	 */
	private void tryRegisterTopic() {
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", "",
					"Trying to register topic: " + getTopic());
			webSubHelper.registerTopic(getTopic());
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", "",
					"Registered topic: " + getTopic());
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", e.getClass().toString(),
					"Error registering topic: " + getTopic() + "\n" + e.getMessage());
		}
	}


	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister", this.getClass().getSimpleName(),
				"Registering hotlist event topic..");
		tryRegisterTopic();
	}
	
	public void publishEvent(String status, String requestId, LocalDateTime updatedDTimes) {
		AuthTypeStatusUpdateAckEvent credentialStatusUpdateEvent = createEvent(requestId, status, updatedDTimes);
		EventModel<AuthTypeStatusUpdateAckEvent> eventModel = webSubHelper.createEventModel(getTopic(), credentialStatusUpdateEvent);
		webSubHelper.publishEvent(getTopic(), eventModel);
	}
	
	/**
	 * Creates the credential status update event.
	 *
	 * @param requestId the request id
	 * @param status the status
	 * @param updatedTimestamp the updated timestamp
	 * @return the credential status update event
	 */
	private AuthTypeStatusUpdateAckEvent createEvent(String requestId, String status, LocalDateTime updatedTimestamp) {
		AuthTypeStatusUpdateAckEvent event = new AuthTypeStatusUpdateAckEvent();
		event.setStatus(status);
		event.setRequestId(requestId);
		event.setTimestamp(updatedTimestamp);
		return event;
	}

	/**
	 * @return the authTypeStatusAcknlowedgeTopic
	 */
	public String getTopic() {
		return authTypeStatusAcknlowedgeTopic;
	}
	
}
