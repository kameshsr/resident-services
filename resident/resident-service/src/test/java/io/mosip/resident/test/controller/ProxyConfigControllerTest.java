package io.mosip.resident.test.controller;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.resident.controller.*;
import io.mosip.resident.handler.service.ResidentConfigService;
import io.mosip.resident.helper.ObjectStoreHelper;
import io.mosip.resident.service.ResidentVidService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Resident proxy config controller test class.
 * 
 * @author Ritik Jain
 */
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ResidentTestBootApplication.class)
@AutoConfigureMockMvc
public class ProxyConfigControllerTest {

	@Mock
	private ResidentConfigService residentConfigService;

	@Mock
	private AuditUtil auditUtil;

	@InjectMocks
	private ProxyConfigController proxyConfigController;

	@Mock
	private DocumentController documentController;

	@Mock
	private IdAuthController idAuthController;

	@Mock
	private IdentityController identityController;

	@Mock
	private ResidentController residentController;

	@Mock
	private ResidentVidService vidService;

	@Mock
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> encryptor;

	@Mock
	@Qualifier("selfTokenRestTemplate")
	private RestTemplate residentRestTemplate;

	@Mock
	private ObjectStoreHelper objectStore;

	@Autowired
	private MockMvc mockMvc;

	private ResponseWrapper responseWrapper;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(proxyConfigController).build();
		Mockito.doNothing().when(auditUtil).setAuditRequestDto(Mockito.any());
	}

	@Test
	public void testGetResidentProperties() throws Exception {
		Mockito.when(residentConfigService.getUIProperties()).thenReturn(responseWrapper);
		mockMvc.perform(MockMvcRequestBuilders.get("/proxy/config/ui-properties")).andExpect(status().isOk());
	}

	@Test
	public void testGetResidentUISchema() throws Exception {
		Mockito.when(residentConfigService.getUISchema()).thenReturn("ui-schema-json");
		mockMvc.perform(MockMvcRequestBuilders.get("/proxy/config/ui-schema")).andExpect(status().isOk());
	}

	@Test
	public void testGetIdentityMapping() throws Exception {
		Mockito.when(residentConfigService.getIdentityMapping()).thenReturn("identity-mapping-json");
		mockMvc.perform(MockMvcRequestBuilders.get("/proxy/config/identity-mapping")).andExpect(status().isOk());
	}

}