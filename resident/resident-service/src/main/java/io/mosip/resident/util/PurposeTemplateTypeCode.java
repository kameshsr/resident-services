package io.mosip.resident.util;

import io.mosip.resident.constant.RequestType;
import io.mosip.resident.constant.TemplateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Kamesh Shekhar Prasad
 */

@Component
public class PurposeTemplateTypeCode {

	@Autowired
	private EventStatusBasedOnLangCode eventStatusBasedOnLangCode;

    public String getPurposeTemplateTypeCode(RequestType requestType, TemplateType templateType) {
		String purposeTemplateCodeProperty = requestType.getPurposeTemplateCodeProperty(templateType);
		return eventStatusBasedOnLangCode.getTemplateTypeCode(purposeTemplateCodeProperty);
	}
}
