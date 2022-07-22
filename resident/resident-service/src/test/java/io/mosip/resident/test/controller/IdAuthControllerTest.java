package io.mosip.resident.test.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.resident.controller.IdAuthController;
import io.mosip.resident.dto.IdAuthRequestDto;
import io.mosip.resident.dto.RequestWrapper;
import io.mosip.resident.helper.ObjectStoreHelper;
import io.mosip.resident.service.DocumentService;
import io.mosip.resident.service.ResidentVidService;
import io.mosip.resident.service.impl.IdAuthServiceImpl;
import io.mosip.resident.test.ResidentTestBootApplication;
import io.mosip.resident.util.AuditUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Resident IdAuth controller test class.
 * 
 * @author Ritik Jain
 */
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ResidentTestBootApplication.class)
@AutoConfigureMockMvc
public class IdAuthControllerTest {

	@Mock
	private IdAuthServiceImpl idAuthService;

	@Mock
	private AuditUtil auditUtil;

	@Mock
	@Qualifier("selfTokenRestTemplate")
	private RestTemplate residentRestTemplate;
	
	@Mock
	private ResidentVidService vidService;

	@InjectMocks
	private IdAuthController idAuthController;

	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private DocumentService docService;
	
	@Mock
	private ObjectStoreHelper objectStore;

	Gson gson = new GsonBuilder().serializeNulls().create();

	String reqJson;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(idAuthController).build();
		RequestWrapper<IdAuthRequestDto> requestWrapper = new RequestWrapper<IdAuthRequestDto>();
		IdAuthRequestDto idAuthRequestDto = new IdAuthRequestDto();
		idAuthRequestDto.setTransactionID("1234567890");
		idAuthRequestDto.setIndividualId("8251649601");
		idAuthRequestDto.setOtp("111111");
		requestWrapper.setRequest(idAuthRequestDto);
		reqJson = gson.toJson(requestWrapper);
		Mockito.doNothing().when(auditUtil).setAuditRequestDto(Mockito.any());
	}

	@Test
	public void testValidateOtp() throws Exception {
		Boolean authStatus = true;
		Mockito.when(idAuthService.validateOtp(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(authStatus);
		mockMvc.perform(MockMvcRequestBuilders.post("/req/validateOTP").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(reqJson.getBytes())).andExpect(status().isOk());
	}

}
