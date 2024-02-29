package io.mosip.resident.service;

import java.util.List;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.resident.dto.DraftResidentResponseDto;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import org.springframework.http.ResponseEntity;

public interface ProxyIdRepoService {

	ResponseWrapper<?> getRemainingUpdateCountByIndividualId(List<String> attributeList)
			throws ResidentServiceCheckedException;

    ResponseWrapper<DraftResidentResponseDto> getPendingDrafts() throws ResidentServiceCheckedException;

    ResponseEntity<Object> discardDraft(String eid) throws ResidentServiceCheckedException;
}
