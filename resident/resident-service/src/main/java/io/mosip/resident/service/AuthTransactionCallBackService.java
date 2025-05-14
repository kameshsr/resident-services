package io.mosip.resident.service;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import io.mosip.resident.dto.AuthTransactionCallbackRequestDto;
import org.springframework.stereotype.Service;

import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;

@Service
public interface AuthTransactionCallBackService {
    public void updateAuthTransactionCallBackService(AuthTransactionCallbackRequestDto authTransactionCallbackRequestDto) throws ResidentServiceCheckedException, ApisResourceAccessException, NoSuchAlgorithmException;
}
