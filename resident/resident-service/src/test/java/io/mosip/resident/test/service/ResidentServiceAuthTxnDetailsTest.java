package io.mosip.resident.test.service;

import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.dto.AutnTxnDto;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.impl.PartnerServiceImpl;
import io.mosip.resident.service.impl.ResidentServiceImpl;
import io.mosip.resident.util.AuditUtil;
import io.mosip.resident.validator.RequestValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class ResidentServiceAuthTxnDetailsTest {

    @Mock
    private AuditUtil audit;

    @InjectMocks
    private ResidentServiceImpl residentServiceImpl;

    @Mock
    private RequestValidator validator;

    @Mock
    private PartnerServiceImpl partnerServiceImpl;
    List<AutnTxnDto> details = null;

    @Before
    public void setup() throws ResidentServiceCheckedException, ApisResourceAccessException, IOException {
        details = new ArrayList<>();
        Mockito.when(partnerServiceImpl.getPartnerDetails(Mockito.anyString())).thenReturn(new ArrayList<>());
        Mockito.when(residentServiceImpl.getAuthTxnDetails("8251649601",null, null, "UIN")).thenReturn(details);
        Mockito.doNothing().when(audit).setAuditRequestDto(Mockito.any());
    }

    @Test
    public void testGetTxnDetails() throws ResidentServiceCheckedException {
        String individualId = "8251649601";
        Integer pageStart = 1;
        Integer pageSize = 1;

        assertEquals(0, residentServiceImpl.getAuthTxnDetails(individualId, pageStart, pageSize, "UIN").size());
    }

    @Test
    public void testGetTxnDetailsNullCheck() throws ResidentServiceCheckedException {
        String individualId = "8251649601";
        Integer pageSize = 1;

        assertEquals(0, residentServiceImpl.getAuthTxnDetails(individualId, null, pageSize, "UIN").size());
        assertEquals(0, residentServiceImpl.getAuthTxnDetails(individualId, null, null, "UIN").size());
    }




}
