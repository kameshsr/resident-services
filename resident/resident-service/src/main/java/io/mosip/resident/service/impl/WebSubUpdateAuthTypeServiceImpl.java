package io.mosip.resident.service.impl;

import io.mosip.idrepository.core.dto.AuthtypeStatus;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.constant.RequestType;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.constant.TemplateType;
import io.mosip.resident.dto.NotificationRequestDtoV2;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.entity.ResidentTransactionEntity;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.repository.ResidentTransactionRepository;
import io.mosip.resident.service.NotificationService;
import io.mosip.resident.service.WebSubUpdateAuthTypeService;
import io.mosip.resident.util.AuditUtil;
import io.mosip.resident.util.EventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSubUpdateAuthTypeServiceImpl implements WebSubUpdateAuthTypeService {

    private static final Logger logger = LoggerConfiguration.logConfig(WebSubUpdateAuthTypeServiceImpl.class);

    private static final String AUTH_TYPES = "authTypes";
    @Autowired
    private AuditUtil auditUtil;

    @Autowired
    private IdentityServiceImpl identityServiceImpl;
    
    @Autowired
	private NotificationService notificationService;

    @Autowired
    private ResidentTransactionRepository residentTransactionRepository;

    @Override
    public void updateAuthTypeStatus(EventModel eventModel) throws ResidentServiceCheckedException, ApisResourceAccessException {
        logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
                LoggerFileConstant.APPLICATIONID.toString(), "WebSubUpdateAuthTypeServiceImpl::updateAuthTypeStatus()::entry");
        auditUtil.setAuditRequestDto(EventEnum.UPDATE_AUTH_TYPE_STATUS);
        try{
            logger.info( "WebSubUpdateAuthTypeServiceImpl::updateAuthTypeStatus()::partnerId");
            String eventId = insertInResidentTransactionTable(eventModel,"COMPLETED");
            sendNotificationV2(TemplateType.SUCCESS,eventId);
        }
        catch (Exception e) {
            logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
                    LoggerFileConstant.APPLICATIONID.toString(), "WebSubUpdateAuthTypeServiceImpl::updateAuthTypeStatus()::exception");
            String eventId = insertInResidentTransactionTable(eventModel,"FAILED");
            sendNotificationV2(TemplateType.FAILURE, eventId);
            throw new ResidentServiceCheckedException(ResidentErrorCode.RESIDENT_WEBSUB_UPDATE_AUTH_TYPE_FAILED.getErrorCode(),
                    ResidentErrorCode.RESIDENT_WEBSUB_UPDATE_AUTH_TYPE_FAILED.getErrorMessage(), e);
        }
    }

    private String insertInResidentTransactionTable(EventModel eventModel,  String status) {

        logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
                LoggerFileConstant.APPLICATIONID.toString(), "WebSubUpdateAuthTypeServiceImpl::insertInResidentTransactionTable()::entry");
        String eventId="";
        List<ResidentTransactionEntity> residentTransactionEntity = null;
        try {
            List<AuthtypeStatus> authTypeStatusList = (List<AuthtypeStatus>) eventModel.getEvent().getData().get(AUTH_TYPES);
            if(authTypeStatusList!=null){
                residentTransactionEntity = residentTransactionRepository.findByRequestTrnId(authTypeStatusList.get(0).getRequestId());
            }
            if(residentTransactionEntity!=null){
                residentTransactionEntity.stream().forEach(residentTransactionEntity1 -> {
                    residentTransactionEntity1.setStatusCode(status);
                });
                residentTransactionRepository.saveAll(residentTransactionEntity);
            }
            if(residentTransactionEntity!=null){
                eventId = residentTransactionEntity.get(0).getEventId();
            }
        }
        catch (Exception e) {
            logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
                    LoggerFileConstant.APPLICATIONID.toString(), "WebSubUpdateAuthTypeServiceImpl::insertInResidentTransactionTable()::exception");
        }
        return eventId;
    }
    
    private NotificationResponseDTO sendNotificationV2(TemplateType templateType, String eventId) throws ResidentServiceCheckedException, ApisResourceAccessException {

		NotificationRequestDtoV2 notificationRequestDtoV2 = new NotificationRequestDtoV2();
		String id = identityServiceImpl.getResidentIndvidualId();
		notificationRequestDtoV2.setId(id);
		notificationRequestDtoV2.setRequestType(RequestType.AUTH_TYPE_LOCK_UNLOCK);
		notificationRequestDtoV2.setTemplateType(templateType);
		notificationRequestDtoV2.setEventId(eventId);

		return notificationService.sendNotification(notificationRequestDtoV2);
	}
}
