package io.mosip.resident.controller;

import com.fasterxml.jackson.databind.ObjectMapper;


import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ResidentConstants;
import io.mosip.resident.dto.AuthTypeStatusEventDTO;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.UpdateAuthtypeStatusService;
import io.mosip.resident.util.AuditUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static io.mosip.resident.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET;

/**
 * The InternalUpdateAuthTypeController use to fetch Auth Transaction.
 *
 * @author Kamesh Shekhar Prasad
 */
@RestController
@Tag(name = "websub-update-auth-type-controller", description = "websub Update Auth Type Controller")
public class WebSubUpdateAuthTypeController {

	/**
	 * The logger.
	 */
	private static Logger logger = LoggerConfiguration.logConfig(WebSubUpdateAuthTypeController.class);

	@Autowired
	private UpdateAuthtypeStatusService authtypeStatusService;

	@Autowired
	private AuditUtil auditUtil;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	@Qualifier("subscriptionExtendedClient")
	SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe;

	@PostMapping(value = "/callback/authTypeCallback/{partnerId}", consumes = "application/json")
	@Operation(summary = "updateAuthtypeStatus", description = "updateAuthtypeStatus", tags = {"internal-update-auth-type-controller"})

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))})

//	@PostMapping(value = "/callback",consumes = "application/json")
//	@PreAuthenticateContentAndVerifyIntent(secret = "Kslk30SNF2AChs2",callback = "/callback",topic = "http://websubpubtopic.com")
//	public void printPost(@RequestBody DataBody body) {
//		System.out.println(body.getData());
//	}

	@PreAuthenticateContentAndVerifyIntent(secret = "Kslk30SNF2AChs2", callback = "${ida-websub-auth-type-callback-relative-url}", topic = "${ida-topic-auth-type-status-updated}")
	public void updateAuthtypeStatus(@RequestBody EventModel eventModel, @PathVariable("partnerId") String partnerId, @PathVariable("AUTH_TYPE_STATUS_UPDATE_ACK ") String authTypeStatusUpdateAck) throws ResidentServiceException {
		{
			if (eventModel.getEvent() != null && eventModel.getEvent().getData() != null) {
				AuthTypeStatusEventDTO event = mapper.convertValue(eventModel.getEvent().getData(), AuthTypeStatusEventDTO.class);
				try {
					logger.debug(ResidentConstants.SESSION_ID, "updateAuthtypeStatus", this.getClass().getCanonicalName(), "handling updateAuthtypeStatus event for partnerId: " + partnerId);

					authtypeStatusService.updateAuthTypeStatus(event.getTokenId(), event.getAuthTypeStatusList(), partnerId, authTypeStatusUpdateAck);


				} catch (ResidentServiceException e) {
					logger.error(ResidentConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());

					throw new ResidentServiceException(e.getErrorCode(), e.getErrorText(), e);
				}
			}

		}

	}
}
