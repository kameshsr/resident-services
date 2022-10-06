package io.mosip.resident.service.impl;

import static io.mosip.resident.constant.ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.ProxyIdRepoService;
import io.mosip.resident.util.ResidentServiceRestClient;

/**
 * @author Manoj SP
 *
 */
@Service
public class ProxyIdRepoServiceImpl implements ProxyIdRepoService {

	private static final Logger logger = LoggerConfiguration.logConfig(ProxyIdRepoServiceImpl.class);

	@Autowired
	private ResidentServiceRestClient residentServiceRestClient;
	
	@Autowired
	private IdentityServiceImpl identityServiceImpl;

	@Override
	public ResponseWrapper<?> getRemainingUpdateCountByIndividualId(String idType,
			List<String> attributeList) throws ResidentServiceCheckedException {
		try {
			String individualId=identityServiceImpl.getResidentIndvidualId();
			ResponseWrapper<?> responseWrapper = (ResponseWrapper<?>) residentServiceRestClient.getApi(ApiName.IDREPO_IDENTITY_UPDATE_COUNT,
					List.of(individualId), "attribute_list", attributeList.stream().collect(Collectors.joining(",")),
					ResponseWrapper.class);

			if (responseWrapper.getErrors() != null && !responseWrapper.getErrors().isEmpty()) {
				throw new ResidentServiceCheckedException(ResidentErrorCode.NO_RECORDS_FOUND);
			}
			return responseWrapper;
			
		} catch (ApisResourceAccessException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new ResidentServiceCheckedException(API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
					API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage(), e);
		}
	}
}
