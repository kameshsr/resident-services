package io.mosip.resident.service;

import io.mosip.resident.dto.ResponseWrapper;
import io.mosip.resident.exception.ResidentServiceCheckedException;

import java.io.IOException;

/**
 * This class is used to create service class for getting in-progress update eids API.
 * @Author Kamesh Shekhar Prasad
 */
public interface InProgressUpdateEidsService {
    ResponseWrapper<Object> getInProgressUpdateEids() throws ResidentServiceCheckedException, IOException;
}
