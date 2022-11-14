package io.mosip.resident.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.constant.RequestType;
import io.mosip.resident.constant.ResidentConstants;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.controller.ResidentController;
import io.mosip.resident.dto.DownloadCardRequestDTO;
import io.mosip.resident.dto.DownloadPersonalizedCardDto;
import io.mosip.resident.dto.MainRequestDTO;
import io.mosip.resident.entity.ResidentTransactionEntity;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.helper.ObjectStoreHelper;
import io.mosip.resident.repository.ResidentTransactionRepository;
import io.mosip.resident.service.DownloadCardService;
import io.mosip.resident.service.IdAuthService;
import io.mosip.resident.util.AuditUtil;
import io.mosip.resident.util.EventEnum;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TemplateUtil;
import io.mosip.resident.util.Utilities;
import io.mosip.resident.util.Utilitiy;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static io.mosip.resident.constant.RegistrationConstants.SUCCESS;

/**
 * @author Kamesh Shekhar Prasad
 * This class is used to create service class implementation of download card api.
 */
@Service
public class DownloadCardServiceImpl implements DownloadCardService {

    private static final String AID = "AID";
    private static final String LANGUAGE = "language";
    private static final String VALUE = "value";
    @Autowired
    private ResidentController residentController;

    @Autowired
    private Utilities utilities;

    @Autowired
    private AuditUtil audit;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectStoreHelper objectStoreHelper;

    @Autowired
    private ResidentServiceRestClient residentServiceRestClient;

    @Autowired
    private IdAuthService idAuthService;

    @Autowired
    private ResidentServiceImpl residentService;

    @Autowired
    private Utilitiy utilitiy;

    @Autowired
    private IdentityServiceImpl identityService;

    @Autowired
    private TemplateUtil templateUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment environment;

    @Autowired
    private ResidentTransactionRepository residentTransactionRepository;

    private static final Logger logger = LoggerConfiguration.logConfig(DownloadCardServiceImpl.class);

    @Override
    public byte[] getDownloadCardPDF(MainRequestDTO<DownloadCardRequestDTO> downloadCardRequestDTOMainRequestDTO) {
        String rid = null;
        try {
            if (idAuthService.validateOtp(downloadCardRequestDTOMainRequestDTO.getRequest().getTransactionId(),
                    getUINForIndividualId(downloadCardRequestDTOMainRequestDTO.getRequest().getIndividualId())
                    , downloadCardRequestDTOMainRequestDTO.getRequest().getOtp())) {
                String individualId = downloadCardRequestDTOMainRequestDTO.getRequest().getIndividualId();
                String idType = templateUtil.getIndividualIdType(individualId);
                if (idType.equalsIgnoreCase(AID)) {
                    rid = individualId;
                } else {
                    rid = utilities.getRidByIndividualId(individualId);
                }
            } else {
                logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
                        LoggerFileConstant.APPLICATIONID.toString(),
                        ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage());
                audit.setAuditRequestDto(EventEnum.CREDENTIAL_REQ_EXCEPTION);
                throw new ResidentServiceException(ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorCode(),
                        ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage());
            }
        } catch (ApisResourceAccessException e) {
            audit.setAuditRequestDto(EventEnum.RID_DIGITAL_CARD_REQ_EXCEPTION);
            throw new ResidentServiceException(
                    ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
                    ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage(), e);
        } catch (OtpValidationFailedException e) {
            audit.setAuditRequestDto(EventEnum.REQ_CARD);
            throw new ResidentServiceException(ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorCode(), e.getErrorText(),
                    e);
        }
        return residentService.getUINCard(rid);
    }

    @Override
    public byte[] downloadPersonalizedCard(MainRequestDTO<DownloadPersonalizedCardDto> downloadPersonalizedCardMainRequestDTO) {
        String encodeHtml = downloadPersonalizedCardMainRequestDTO.getRequest().getHtml();
        byte[] decodedData;
        String password=null;
        try {
            decodedData = CryptoUtil.decodeURLSafeBase64(encodeHtml);
            List<String> attributeValues = getAttributeList();
            if(Boolean.parseBoolean(this.environment.getProperty(ResidentConstants.IS_PASSWORD_FLAG_ENABLED))){
                password = utilitiy.getPassword(attributeValues);
            }
        }
        catch (Exception e) {
            audit.setAuditRequestDto(EventEnum.DOWNLOAD_PERSONALIZED_CARD);
            logger.error("Unable to convert html to pdf RootCause- "+e);
            throw new ResidentServiceException(ResidentErrorCode.DOWNLOAD_PERSONALIZED_CARD, e);
        }
        return utilitiy.signPdf(new ByteArrayInputStream(decodedData), password);
    }

    private List<String> getAttributeList() throws IOException, ApisResourceAccessException {
        Map<String, Object> identityAttributes = null;
        List<String> attributeValues = new ArrayList<>();
        try {
            identityAttributes = (Map<String, Object>) identityService.getIdentityAttributes(
                    identityService.getResidentIndvidualId(), this.environment.getProperty(ResidentConstants.RESIDENT_IDENTITY_SCHEMATYPE));
        } catch (ResidentServiceCheckedException e) {
            audit.setAuditRequestDto(EventEnum.DOWNLOAD_PERSONALIZED_CARD);
            logger.error("Unable to get attributes- "+e);
            throw new ResidentServiceException(ResidentErrorCode.DOWNLOAD_PERSONALIZED_CARD, e);
        } catch (IOException e) {
            audit.setAuditRequestDto(EventEnum.DOWNLOAD_PERSONALIZED_CARD);
            logger.error("Unable to get attributes- "+e);
            throw new IOException(ResidentErrorCode.DOWNLOAD_PERSONALIZED_CARD.getErrorCode(), e);
        } catch (ApisResourceAccessException e) {
            audit.setAuditRequestDto(EventEnum.DOWNLOAD_PERSONALIZED_CARD);
            logger.error("Unable to get attributes- "+e);
            throw new ApisResourceAccessException(ResidentErrorCode.DOWNLOAD_PERSONALIZED_CARD.getErrorCode(), e);
        }
        String attributeProperty = this.environment.getProperty(ResidentConstants.PASSWORD_ATTRIBUTE);
        List<String> attributeList = List.of(attributeProperty.split("\\|"));

        for (String attribute : attributeList) {
            Object attributeObject = identityAttributes.get(attribute);
            if (attributeObject instanceof List) {
                List<Map<String, Object>> attributeMapObject = (List<Map<String, Object>>) attributeObject;
                for (Map<String, Object> attributeInLanguage : attributeMapObject) {
                    /**
                     * 1st language code is taken from mandatory/optional languages properties
                     */
                    String languageCode = utilities.getLanguageCode();
                    if (attributeInLanguage.containsKey(LANGUAGE) &&
                            attributeInLanguage.get(LANGUAGE).toString().equalsIgnoreCase(languageCode)) {
                        attributeValues.add((String) attributeInLanguage.get(VALUE));
                    }
                }
            } else {
                attributeValues.add((String) attributeObject);
            }
        }
        return attributeValues;
    }

    @Override
    public String getFileName() {
        ResidentTransactionEntity residentTransactionEntity = utilitiy.createEntity();
        String eventId = UUID.randomUUID().toString();
        residentTransactionEntity.setEventId(eventId);
        residentTransactionEntity.setRequestTypeCode(RequestType.DOWNLOAD_PERSONALIZED_CARD.name());
        try {
            residentTransactionEntity.setRefId(utilitiy.convertToMaskDataFormat(identityService.getResidentIndvidualId()));
            residentTransactionEntity.setTokenId(identityService.getResidentIdaToken());
        } catch (ApisResourceAccessException e) {
            logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
                    LoggerFileConstant.APPLICATIONID.toString(),
                    ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode()
                            + ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorMessage()
                            + ExceptionUtils.getStackTrace(e));
            audit.setAuditRequestDto(EventEnum.getEventEnumWithValue(EventEnum.API_NOT_AVAILABLE,
                    eventId, "Download personalized card"));
            throw new ResidentServiceException(ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode(),
                    ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorMessage(), e);
        }
        residentTransactionEntity.setRequestSummary(SUCCESS);
        return utilitiy.getFileName(eventId, Objects.requireNonNull(this.environment.getProperty
                (ResidentConstants.DOWNLOAD_PERSONALIZED_CARD_NAMING_CONVENTION_PROPERTY)));
    }

    private String getUINForIndividualId(String individualId)  {
        String idType = templateUtil.getIndividualIdType(individualId);
        if(idType.equalsIgnoreCase(IdType.UIN.toString()) || idType.equalsIgnoreCase(IdType.VID.toString())){
            return individualId;
        } else {
            try {
                return identityService.getIndividualIdForAid(individualId);
            } catch (ResidentServiceCheckedException e) {
                audit.setAuditRequestDto(EventEnum.RID_DIGITAL_CARD_REQ_EXCEPTION);
                throw new ResidentServiceException(
                        ResidentErrorCode.AID_NOT_FOUND.getErrorCode(),
                        ResidentErrorCode.AID_NOT_FOUND.getErrorMessage(), e);
            } catch (ApisResourceAccessException e) {
                audit.setAuditRequestDto(EventEnum.RID_DIGITAL_CARD_REQ_EXCEPTION);
                throw new ResidentServiceException(
                        ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
                        ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage(), e);
            }
        }
    }

}
