package io.mosip.resident.controller;

import io.micrometer.core.annotation.Timed;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.dto.ResponseWrapper;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.InProgressUpdateEidsService;
import io.mosip.resident.util.AuditEnum;
import io.mosip.resident.util.AuditUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static io.mosip.resident.constant.ResidentConstants.API_RESPONSE_TIME_DESCRIPTION;
import static io.mosip.resident.constant.ResidentConstants.API_RESPONSE_TIME_ID;

/**
 * This class is used to create api for getting in progress update uin event ids .
 * @Author Kamesh Shekhar Prasad
 */
@RestController
@Tag(name="InProgressUpdateEidsController", description="InProgressUpdateEidsController")
public class InProgressUpdateEidsController {

	private static final Logger logger = LoggerConfiguration.logConfig(InProgressUpdateEidsController.class);

    @Autowired
    private AuditUtil auditUtil;

    @Autowired
    private InProgressUpdateEidsService inProgressUpdateEidsService;


	@Timed(value=API_RESPONSE_TIME_ID,description=API_RESPONSE_TIME_DESCRIPTION, percentiles = {0.5, 0.9, 0.95, 0.99} )
    @GetMapping("/get-inprogress-update-eids")
    public ResponseWrapper<Object> getInProgressEids(
                                                  @RequestHeader(name = "time-zone-offset", required = false, defaultValue = "0") int timeZoneOffset,
                                                  @RequestHeader(name = "locale", required = false) String locale) throws ResidentServiceCheckedException, IOException {
        logger.debug("InProgressUpdateEidsController::getInProgressEids()::entry");
        try {

	        logger.debug("InProgressUpdateEidsController::getInProgressEids()::exit");
            return inProgressUpdateEidsService.getInProgressUpdateEids();
        } catch(Exception e) {
			auditUtil.setAuditRequestDto(AuditEnum.GET_ACKNOWLEDGEMENT_DOWNLOAD_URL_FAILURE);
			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(), ExceptionUtils.getStackTrace(e));
//			throw new ResidentServiceCheckedException(e.getErrorCode(), e.getErrorText(), e,
//					Map.of(ResidentConstants.HTTP_STATUS_CODE, HttpStatus.BAD_REQUEST, ResidentConstants.REQ_RES_ID,
//							ackDownloadId));
        }
        return null;
    }
}
