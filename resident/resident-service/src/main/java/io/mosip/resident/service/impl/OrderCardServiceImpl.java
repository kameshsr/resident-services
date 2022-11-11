package io.mosip.resident.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.ConsentStatusType;
import io.mosip.resident.constant.EventStatusFailure;
import io.mosip.resident.constant.EventStatusInProgress;
import io.mosip.resident.constant.RequestType;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.constant.TemplateType;
import io.mosip.resident.dto.NotificationRequestDtoV2;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.ResidentCredentialRequestDto;
import io.mosip.resident.dto.ResidentCredentialResponseDto;
import io.mosip.resident.entity.ResidentTransactionEntity;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.repository.ResidentTransactionRepository;
import io.mosip.resident.service.NotificationService;
import io.mosip.resident.service.OrderCardService;
import io.mosip.resident.service.ResidentCredentialService;
import io.mosip.resident.util.AuditUtil;
import io.mosip.resident.util.EventEnum;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.Utilitiy;

/**
 * Order card service implementation class.
 * 
 * @author Ritik Jain
 */
@Component
public class OrderCardServiceImpl implements OrderCardService {

	private static final String PARTNER_TYPE = "partnerType";
	private static final String ORGANIZATION_NAME = "organizationName";

	@Autowired
	private ResidentCredentialService residentCredentialService;

	@Autowired
	private IdentityServiceImpl identityServiceImpl;

	@Autowired
	@Qualifier("restClientWithSelfTOkenRestTemplate")
	private ResidentServiceRestClient restClientWithSelfTOkenRestTemplate;

	@Autowired
	private AuditUtil auditUtil;

	@Autowired
	private Utilitiy utility;

	@Autowired
	NotificationService notificationService;
	
	@Autowired
    private ProxyPartnerManagementServiceImpl proxyPartnerManagementServiceImpl;

	@Autowired
	private ResidentTransactionRepository residentTransactionRepository;

	@Value("${mosip.resident.order.card.payment.enabled}")
	private boolean isPaymentEnabled;

	private static final Logger logger = LoggerConfiguration.logConfig(OrderCardServiceImpl.class);

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public ResidentCredentialResponseDto sendPhysicalCard(ResidentCredentialRequestDto requestDto)
			throws ResidentServiceCheckedException, ApisResourceAccessException {
		logger.debug("OrderCardServiceImpl::sendPhysicalCard()::entry");
		ResidentCredentialResponseDto residentCredentialResponseDto = new ResidentCredentialResponseDto();
		String individualId = identityServiceImpl.getResidentIndvidualId();
		ResidentTransactionEntity residentTransactionEntity = createResidentTransactionEntity(requestDto, individualId);
		if (requestDto.getConsent() == null || requestDto.getConsent().equalsIgnoreCase(ConsentStatusType.DENIED.name())
				|| requestDto.getConsent().trim().isEmpty() || requestDto.getConsent().equals("null") || !requestDto.getConsent().equalsIgnoreCase(ConsentStatusType.ACCEPTED.name())) {
			checkConsent(requestDto.getConsent(), residentTransactionEntity);
		} else {

			if (isPaymentEnabled) {
				checkOrderStatus(requestDto.getTransactionID(), individualId, residentTransactionEntity);
			}
			residentCredentialResponseDto = residentCredentialService.reqCredential(requestDto, individualId);
			updateResidentTransaction(residentTransactionEntity, residentCredentialResponseDto);
			sendNotificationV2(individualId, RequestType.ORDER_PHYSICAL_CARD,
					TemplateType.REQUEST_RECEIVED, residentTransactionEntity.getEventId(), null);
			logger.debug("OrderCardServiceImpl::sendPhysicalCard()::exit");

		}

		return residentCredentialResponseDto;
	}

	private void checkConsent(String consent, ResidentTransactionEntity residentTransactionEntity)
			throws ResidentServiceCheckedException {
		try {
			residentTransactionEntity.setStatusCode(EventStatusFailure.FAILED.name());
			throw new ResidentServiceCheckedException(ResidentErrorCode.CONSENT_DENIED.getErrorCode(),
					ResidentErrorCode.CONSENT_DENIED.getErrorMessage());
		} catch (Exception e) {
			throw new ResidentServiceCheckedException(ResidentErrorCode.CONSENT_DENIED.getErrorCode(),
					ResidentErrorCode.CONSENT_DENIED.getErrorMessage());
		} finally {
			residentTransactionRepository.save(residentTransactionEntity);
		}

	}

	private ResidentTransactionEntity createResidentTransactionEntity(ResidentCredentialRequestDto requestDto, String individualId)
			throws ApisResourceAccessException {
		ResidentTransactionEntity residentTransactionEntity = utility.createEntity();
		residentTransactionEntity.setEventId(UUID.randomUUID().toString());
		residentTransactionEntity.setRequestTypeCode(RequestType.ORDER_PHYSICAL_CARD.name());
		residentTransactionEntity.setRefId(utility.convertToMaskDataFormat(individualId));
		residentTransactionEntity.setRequestedEntityId(requestDto.getIssuer());
		Map<String, ?> partnerDetail = proxyPartnerManagementServiceImpl.getPartnerDetailFromPartnerId(requestDto.getIssuer());
		residentTransactionEntity.setRequestedEntityName((String) partnerDetail.get(ORGANIZATION_NAME));
		residentTransactionEntity.setRequestedEntityType((String) partnerDetail.get(PARTNER_TYPE));
		residentTransactionEntity.setTokenId(identityServiceImpl.getResidentIdaToken());
		residentTransactionEntity.setAuthTypeCode(identityServiceImpl.getResidentAuthenticationMode());
		residentTransactionEntity.setRequestSummary("in-progress");
		residentTransactionEntity.setConsent(requestDto.getConsent());
		// TODO: need to fix transaction ID (need partner's end transactionId)
		residentTransactionEntity.setRequestTrnId(requestDto.getTransactionID());
		return residentTransactionEntity;
	}

	private void updateResidentTransaction(ResidentTransactionEntity residentTransEntity,
			ResidentCredentialResponseDto residentCredentialResponseDto) {
		residentTransEntity.setStatusCode(EventStatusInProgress.NEW.name());
		residentTransEntity.setAid(residentCredentialResponseDto.getRequestId());
		residentTransactionRepository.save(residentTransEntity);
	}

	private void checkOrderStatus(String transactionId, String individualId,
			ResidentTransactionEntity residentTransactionEntity) throws ResidentServiceCheckedException {
		logger.debug("OrderCardServiceImpl::checkOrderStatus()::entry");
		List<String> pathsegments = null;

		List<String> queryParamName = new ArrayList<String>();
		queryParamName.add("transactionId");
		queryParamName.add("individualId");

		List<Object> queryParamValue = new ArrayList<>();
		queryParamValue.add(transactionId);
		queryParamValue.add(individualId);

		try {
			ResponseWrapper<?> responseWrapper = (ResponseWrapper<?>) restClientWithSelfTOkenRestTemplate.getApi(
					ApiName.GET_ORDER_STATUS_URL, pathsegments, queryParamName, queryParamValue, ResponseWrapper.class);

			residentTransactionEntity.setStatusCode(EventStatusInProgress.PAYMENT_CONFIRMED.name());

		} catch (ApisResourceAccessException e) {
			residentTransactionEntity.setStatusCode(EventStatusFailure.PAYMENT_FAILED.name());

			logger.error("Error occured in checking order status %s", e.getMessage());
			auditUtil.setAuditRequestDto(EventEnum.CHECK_ORDER_STATUS_EXCEPTION);
			sendNotificationV2(individualId, RequestType.ORDER_PHYSICAL_CARD, TemplateType.FAILURE,
					residentTransactionEntity.getEventId(), null);
			throw new ResidentServiceCheckedException(ResidentErrorCode.PAYMENT_REQUIRED.getErrorCode(),
					ResidentErrorCode.PAYMENT_REQUIRED.getErrorMessage());
		} finally {
			residentTransactionRepository.save(residentTransactionEntity);
		}
		logger.debug("OrderCardServiceImpl::checkOrderStatus()::exit");
	}

	private NotificationResponseDTO sendNotificationV2(String id, RequestType requestType, TemplateType templateType,
			String eventId, Map<String, Object> additionalAttributes) throws ResidentServiceCheckedException {
		NotificationRequestDtoV2 notificationRequestDtoV2 = new NotificationRequestDtoV2();
		notificationRequestDtoV2.setId(id);
		notificationRequestDtoV2.setRequestType(requestType);
		notificationRequestDtoV2.setTemplateType(templateType);
		notificationRequestDtoV2.setEventId(eventId);
		notificationRequestDtoV2.setAdditionalAttributes(additionalAttributes);
		return notificationService.sendNotification(notificationRequestDtoV2);
	}

}