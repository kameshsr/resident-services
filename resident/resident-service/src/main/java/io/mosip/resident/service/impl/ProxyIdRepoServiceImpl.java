package io.mosip.resident.service.impl;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.repository.ResidentTransactionRepository;
import io.mosip.resident.service.ProxyIdRepoService;
import io.mosip.resident.util.ResidentServiceRestClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.mosip.resident.constant.ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION;

/**
 * @author Manoj SP
 *
 */
@Service
public class ProxyIdRepoServiceImpl implements ProxyIdRepoService {

	private static final Logger logger = LoggerConfiguration.logConfig(ProxyIdRepoServiceImpl.class);
	private static final String NO_RECORDS_FOUND_ID_REPO_ERROR_CODE = "IDR-IDC-007";
	private static final int ZERO = 0;
	private static final String REGISTRATION_ID = "registrationId";

	@Autowired
	private ResidentServiceRestClient residentServiceRestClient;
	
	@Autowired
	private IdentityServiceImpl identityServiceImpl;

	@Autowired
	private ResidentTransactionRepository residentTransactionRepository;

	@Override
	public ResponseWrapper<?> getRemainingUpdateCountByIndividualId(List<String> attributeList)
			throws ResidentServiceCheckedException {
		try {
			logger.debug("ProxyIdRepoServiceImpl::getRemainingUpdateCountByIndividualId()::entry");
			String individualId=identityServiceImpl.getResidentIndvidualIdFromSession();
			Map<String, Object> pathsegements = new HashMap<String, Object>();
			pathsegements.put("individualId", individualId);
			
			List<String> queryParamName = new ArrayList<String>();
			queryParamName.add("attribute_list");

			List<Object> queryParamValue = new ArrayList<>();
			queryParamValue.add(Objects.isNull(attributeList) ? "" : attributeList.stream().collect(Collectors.joining(",")));
			
			ResponseWrapper<?> responseWrapper = residentServiceRestClient.getApi(ApiName.IDREPO_IDENTITY_UPDATE_COUNT,
					pathsegements, queryParamName, queryParamValue, ResponseWrapper.class);
			if (responseWrapper.getErrors() != null && !responseWrapper.getErrors().isEmpty()){
				if(responseWrapper.getErrors().get(ZERO) != null && !responseWrapper.getErrors().get(ZERO).toString().isEmpty() &&
						responseWrapper.getErrors().get(ZERO).getErrorCode() != null &&
						!responseWrapper.getErrors().get(ZERO).getErrorCode().isEmpty() &&
						responseWrapper.getErrors().get(ZERO).getErrorCode().equalsIgnoreCase(NO_RECORDS_FOUND_ID_REPO_ERROR_CODE)) {
					throw new ResidentServiceCheckedException(ResidentErrorCode.NO_RECORDS_FOUND);
				}else {
					throw new ResidentServiceCheckedException(ResidentErrorCode.UNKNOWN_EXCEPTION);
				}
			}
			logger.debug("ProxyIdRepoServiceImpl::getRemainingUpdateCountByIndividualId()::exit");
			return responseWrapper;
			
		} catch (ApisResourceAccessException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new ResidentServiceCheckedException(API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
					API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage(), e);
		}
	}

	@Override
	public ResponseWrapper<?> discardDraft(String eid) throws ResidentServiceCheckedException{
		try {
			logger.debug("ProxyIdRepoServiceImpl::discardDraft()::entry");
			Map<String, Object> pathsegements = new HashMap<String, Object>();
			pathsegements.put(REGISTRATION_ID, getAidFromEid(eid));
			ResponseWrapper<?> responseWrapper = residentServiceRestClient.postApi(ApiName.IDREPO_IDENTITY_DISCARD_DRAFT.name(),
					MediaType.APPLICATION_JSON, pathsegements, ResponseWrapper.class);
			if (responseWrapper.getErrors() != null && !responseWrapper.getErrors().isEmpty()){
				if(responseWrapper.getErrors().get(ZERO) != null && !responseWrapper.getErrors().get(ZERO).toString().isEmpty() &&
						responseWrapper.getErrors().get(ZERO).getErrorCode() != null &&
						!responseWrapper.getErrors().get(ZERO).getErrorCode().isEmpty()) {
					throw new ResidentServiceCheckedException(ResidentErrorCode.FAILED_TO_DISCARD_DRAFT);
				}else {
					throw new ResidentServiceCheckedException(ResidentErrorCode.UNKNOWN_EXCEPTION);
				}
			}
			logger.debug("ProxyIdRepoServiceImpl::discardDraft()::exit");
			return responseWrapper;

		} catch (ApisResourceAccessException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new ResidentServiceCheckedException(API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
					API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage(), e);
		}
	}

	private String getAidFromEid(String eid) throws ResidentServiceCheckedException {
		String aid = residentTransactionRepository.findAidByEventId(eid);
		if(aid==null){
			throw new ResidentServiceCheckedException(ResidentErrorCode.AID_NOT_FOUND);
		}
		return aid;
	}

}
