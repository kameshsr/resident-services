package io.mosip.resident.controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;
import io.mosip.resident.config.LoggerConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name="WebSubUpdateAuthTypeController", description="WebSubUpdateAuthTypeController")
public class WebSubUpdateAuthTypeController {

    private static Logger logger = LoggerConfiguration.logConfig(WebSubUpdateAuthTypeController.class);

    @Autowired
    //@Qualifier("subscriptionExtendedClient")
    SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe;


    @PostMapping(value = "/callback/authTypeCallback/{partnerId}/", consumes = "application/json")
    @Operation(summary = "WebSubUpdateAuthTypeController", description = "WebSubUpdateAuthTypeController",
            tags = {"WebSubUpdateAuthTypeController"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))})

    //@PreAuthenticateContentAndVerifyIntent(secret = "${"+ WebSubConstants.RESIDENT_WEBSUB_AUTHTYPE_CALLBACK_SECRET +"}", callback = "${resident-websub-auth-type-callback-relative-url}", topic = "${resident-topic-auth-type-status-updated}")
    public void authTypeCallback(@RequestBody EventModel eventModel, @PathVariable("partnerId") String partnerId) {
                logger.info("WebSubUpdateAuthTypeController");

    }
}

