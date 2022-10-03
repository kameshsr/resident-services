package io.mosip.resident.controller;

import static io.mosip.resident.constant.ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.idrepository.core.util.EnvUtil;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.resident.dto.UpdateCountDto;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.ProxyIdRepoService;
import io.mosip.resident.util.AuditUtil;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@Import(EnvUtil.class)
@ActiveProfiles("test")
public class ProxyIdRepoControllerTest {

	@InjectMocks
	private ProxyIdRepoController controller;

	@Mock
	private ProxyIdRepoService service;
	
	@Mock
	private AuditUtil auditUtil;

	@Test
	public void testGetRemainingUpdateCountByIndividualId() throws ResidentServiceCheckedException {
		List<UpdateCountDto> responseWrapper = new ArrayList<>();
		responseWrapper.add(new UpdateCountDto());
		when(service.getRemainingUpdateCountByIndividualId(any(), any())).thenReturn(responseWrapper);
		ResponseEntity<ResponseWrapper<List<UpdateCountDto>>> response = controller
				.getRemainingUpdateCountByIndividualId("", List.of());
		assertEquals(new UpdateCountDto(), response.getBody().getResponse().get(0));
	}

	@Test
	public void testGetRemainingUpdateCountByIndividualIdException() throws ResidentServiceCheckedException {
		when(service.getRemainingUpdateCountByIndividualId(any(), any()))
				.thenThrow(new ResidentServiceCheckedException(API_RESOURCE_ACCESS_EXCEPTION));
		ResponseEntity<ResponseWrapper<List<UpdateCountDto>>> response = controller
				.getRemainingUpdateCountByIndividualId("", List.of());
		assertEquals(List.of(new ServiceError(API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
				API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage())), response.getBody().getErrors());
	}
}
