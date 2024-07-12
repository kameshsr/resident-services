package io.mosip.resident.util;

import io.mosip.resident.constant.*;
import io.mosip.resident.entity.ResidentTransactionEntity;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.impl.DescriptionForLangCode;
import io.mosip.resident.service.impl.ReplacePlaceholderValueInTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Kamesh Shekhar Prasad
 */

@Component
public class SummaryForLangCode {

    @Autowired
    private DescriptionForLangCode descriptionForLangCode;

    @Autowired
    private TemplateValueFromTemplateTypeCodeAndLangCode templateValueFromTemplateTypeCodeAndLangCode;

    @Autowired
    private ReplacePlaceholderValueInTemplate replacePlaceholderValueInTemplate;

    @Autowired
    private EventStatusBasedOnLangCode eventStatusBasedOnLangCode;

    public String getSummaryTemplateTypeCode(RequestType requestType, TemplateType templateType) {
        String summaryTemplateCodeProperty = requestType.getSummaryTemplateCodeProperty(templateType);
        return eventStatusBasedOnLangCode.getTemplateTypeCode(summaryTemplateCodeProperty);
    }

    public String getSummaryForLangCode(ResidentTransactionEntity residentTransactionEntity, String langCode, String statusCode,
                                        RequestType requestType)
            throws ResidentServiceCheckedException {
        TemplateType templateType;
        if (statusCode.equalsIgnoreCase(EventStatus.SUCCESS.name())) {
            templateType = TemplateType.SUCCESS;
        } else if (statusCode.equalsIgnoreCase(EventStatusCanceled.CANCELED.name())) {
            templateType = TemplateType.CANCELED;
        } else if (residentTransactionEntity.getStatusCode().equalsIgnoreCase(EventStatusInProgress.IDENTITY_UPDATED.name())) {
            templateType = TemplateType.REGPROC_SUCCESS;
        } else {
            return descriptionForLangCode.getDescriptionForLangCode(residentTransactionEntity, langCode, statusCode, requestType);
        }
        String templateTypeCode = getSummaryTemplateTypeCode(requestType, templateType);
        String fileText = templateValueFromTemplateTypeCodeAndLangCode.getTemplateValueFromTemplateTypeCodeAndLangCode(langCode, templateTypeCode);
        return replacePlaceholderValueInTemplate.replacePlaceholderValueInTemplate(residentTransactionEntity, fileText, requestType, langCode);
    }
}
