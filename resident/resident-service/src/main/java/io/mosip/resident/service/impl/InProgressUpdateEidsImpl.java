package io.mosip.resident.service.impl;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.dto.ResponseWrapper;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.InProgressUpdateEidsService;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * This class is used to create service class implementation for getting in progress event ids for update uin API.
 * @Author Kamesh Shekhar Prasad
 */
@Service
public class InProgressUpdateEidsImpl implements InProgressUpdateEidsService {

    private static final Logger logger = LoggerConfiguration.logConfig(InProgressUpdateEidsImpl.class);

    @Override
    public ResponseWrapper<Object> getInProgressUpdateEids() throws ResidentServiceCheckedException, IOException {
        return null;
    }
}

