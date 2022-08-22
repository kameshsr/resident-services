package io.mosip.resident.controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.service.AcknowledgementService;
import io.mosip.resident.util.AuditUtil;
import io.mosip.resident.util.EventEnum;
import io.mosip.resident.validator.RequestValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@Tag(name="AcknowledgementController", description="AcknowledgementController")
public class AcknowledgementController {

    private static final Logger logger = LoggerConfiguration.logConfig(ResidentController.class);

    @Autowired
    private AuditUtil auditUtil;

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private AcknowledgementService acknowledgementService;

    @PreAuthorize("@scopeValidator.hasAllScopes("
            + "@authorizedScopes.getGetAcknowledgement()"
            + ")")
    @GetMapping("/ack/download/pdf/event/{eventId}/language/{languageCode}")
    public ResponseEntity<Object> getAcknowledgement(@PathVariable("eventId") String eventId,
                                                  @PathVariable("languageCode") String languageCode) {
        logger.debug("AcknowledgementController::acknowledgement()::entry");
        auditUtil.setAuditRequestDto(EventEnum.getEventEnumWithValue(EventEnum.GET_ACKNOWLEDGEMENT_DOWNLOAD_URL, "acknowledgement"));
        requestValidator.validateEventIdLanguageCode(eventId, languageCode);
        byte[] pdfBytes = acknowledgementService.getAcknowledgementPDF(eventId, languageCode);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));
        auditUtil.setAuditRequestDto(EventEnum.GET_ACKNOWLEDGEMENT_DOWNLOAD_URL_SUCCESS);
        logger.debug("AcknowledgementController::acknowledgement()::exit");
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
                .header("Content-Disposition", "attachment; filename=\"" +
                        eventId + ".pdf\"")
                .body(resource);
    }
}
