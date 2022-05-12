package io.mosip.resident.service.websub.impl;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.retry.WithRetry;
import io.mosip.kernel.websub.api.exception.WebSubClientException;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ResidentConstants;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.helper.WebSubHelper;
import io.mosip.resident.service.websub.WebSubEventSubcriber;
import io.mosip.resident.service.websub.WebSubEventTopicRegistrar;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * The Class BaseWebSubEventsInitializer.
 */
@Component
public abstract class BaseWebSubEventsInitializer implements WebSubEventTopicRegistrar, WebSubEventSubcriber {
	
	private static final Logger logger = LoggerConfiguration.logConfig(BaseWebSubEventsInitializer.class);
	
	/** The Constant EVENT_TYPE_PLACEHOLDER. */
	public static final String EVENT_TYPE_PLACEHOLDER = "{eventType}";
	
	@Autowired
	protected WebSubHelper webSubHelper;
	

	/**
	 * Subscribe.
	 *
	 * @param enableTester the enable tester
	 */
	@Override
	@WithRetry
	public void subscribe(Supplier<Boolean> enableTester) {
		if(enableTester == null || enableTester.get()) {
			try {
				doSubscribe();
			} catch (WebSubClientException e) {
				logger.error(ResidentConstants.SESSION_ID, "subscribe",  this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
				throw new ResidentServiceException(e);
			}
		} else {
			logger.info(ResidentConstants.SESSION_ID, "subscribe",  this.getClass().getSimpleName(), "This websub subscriber is disabled.");
		}
	}
	
	/**
	 * Do subscribe.
	 */
	protected abstract void doSubscribe();
	
	/**
	 * Register.
	 *
	 * @param enableTester the enable tester
	 */
	@Override
	public void register(Supplier<Boolean> enableTester) {
		if(enableTester == null || enableTester.get()) {
			doRegister();
		} else {
			logger.info(ResidentConstants.SESSION_ID, "register",  this.getClass().getSimpleName(), "This websub subscriber is disabled.");
		}
	}
	
	/**
	 * Do register.
	 */
	protected abstract void doRegister();
	
	protected void tryRegisterTopicEvent(String eventTopic) {
		try {
			logger.debug(this.getClass().getCanonicalName(), "tryRegisterTopicEvent", "",
					"Trying to register topic: " + eventTopic);
			webSubHelper.registerTopic(eventTopic);
			logger.info(this.getClass().getCanonicalName(), "tryRegisterTopicEvent", "",
					"Registered topic: " + eventTopic);
		} catch (Exception e) {
			logger.info(this.getClass().getCanonicalName(), "tryRegisterTopicEvent", e.getClass().toString(),
					"Error registering topic: " + eventTopic + "\n" + e.getMessage());
		}
	}

	protected void subscribeForEvent(String eventTopic, String callbackUrl, String callbackSecret) {
		try {
			SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
			subscriptionRequest.setCallbackURL(callbackUrl);
			subscriptionRequest.setSecret(callbackSecret);
			subscriptionRequest.setTopic(eventTopic);
			logger.debug(ResidentConstants.SESSION_ID, "subscribeForHotlistEvent", "",
					"Trying to subscribe to topic: " + eventTopic + " callback-url: "
							+ callbackUrl);
			webSubHelper.subscribe(subscriptionRequest);
			logger.info(ResidentConstants.SESSION_ID, "subscribeForHotlistEvent", "",
					"Subscribed to topic: " + eventTopic);
		} catch (Exception e) {
			logger.info(ResidentConstants.SESSION_ID, "subscribeForHotlistEvent", e.getClass().toString(),
					"Error subscribing topic: " + eventTopic + "\n" + e.getMessage());
			throw e;
		}
	}

}
