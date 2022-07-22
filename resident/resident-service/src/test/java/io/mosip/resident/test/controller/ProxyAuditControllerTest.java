package io.mosip.resident.test.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.resident.controller.AuthTransactionCallbackController;
import io.mosip.resident.controller.DocumentController;
import io.mosip.resident.controller.IdAuthController;
import io.mosip.resident.controller.ProxyAuditController;
import io.mosip.resident.dto.AuditRequestDTO;
import io.mosip.resident.helper.ObjectStoreHelper;
import io.mosip.resident.service.ResidentVidService;
import io.mosip.resident.test.ResidentTestBootApplication;
import io.mosip.resident.util.AuditUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Resident proxy audit controller test class.
 * 
 * @author Ritik Jain
 */
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ResidentTestBootApplication.class)
@AutoConfigureMockMvc
public class ProxyAuditControllerTest {

	@InjectMocks
	private ProxyAuditController proxyAuditController;

	@Mock
	private AuditUtil auditUtil;

	@Mock
	@Qualifier("selfTokenRestTemplate")
	private RestTemplate residentRestTemplate;

	@Mock
	private AuthTransactionCallbackController authTransactionCallbackController;

	@Mock
	private DocumentController documentController;

	@Mock
	private ObjectStoreHelper objectStore;

	@Mock
	private IdAuthController idAuthController;

	@Mock
	private ResidentVidService vidService;

	@Mock
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> encryptor;

	private AuditRequestDTO auditRequestDTO;

	@Autowired
	private MockMvc mockMvc;

	Gson gson = new GsonBuilder().serializeNulls().create();

	String reqJson;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(proxyAuditController).build();
		auditRequestDTO = new AuditRequestDTO();
		auditRequestDTO.setEventId("RES-SER-1111");
		auditRequestDTO.setModuleId("RES-SER");
		auditRequestDTO.setModuleName("Residence service");
		reqJson = gson.toJson(auditRequestDTO);
	}

	@Test
	public void testAuditLog() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/proxy/audit/log").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(reqJson.getBytes())).andExpect(status().isOk());
	}

}
