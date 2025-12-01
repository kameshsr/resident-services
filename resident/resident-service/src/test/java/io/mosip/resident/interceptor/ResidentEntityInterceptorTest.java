package io.mosip.resident.interceptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.entity.ResidentTransactionEntity;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.helper.ObjectStoreHelper;

@RunWith(MockitoJUnitRunner.class)
public class ResidentEntityInterceptorTest {

	@InjectMocks
	private ResidentEntityInterceptor residentEntityInterceptor;

	@Mock
	private ObjectStoreHelper objectStoreHelper;

	private ResidentTransactionEntity residentTransactionEntity;

	@Before
	public void setup() {
		residentTransactionEntity = new ResidentTransactionEntity();
		residentTransactionEntity.setIndividualId("1234567890");
		ReflectionTestUtils.setField(residentEntityInterceptor, "appId", "resident");
		ReflectionTestUtils.setField(residentEntityInterceptor, "refId", "resident");
	}

	@Test
	public void onSaveTest() {
		when(objectStoreHelper.encryptDecryptData(Mockito.anyString(), Mockito.eq(true), Mockito.anyString(),
				Mockito.anyString())).thenReturn("encryptedData");
		boolean result = residentEntityInterceptor.onSave(residentTransactionEntity, "1", new Object[1],
				new String[] { "individualId" }, new Type[] { StringType.INSTANCE });
		assertTrue(result);
	}

	@Test
	public void onSaveEmptyIdTest() {
		residentTransactionEntity.setIndividualId(null);
		boolean result = residentEntityInterceptor.onSave(residentTransactionEntity, "1", new Object[1],
				new String[] { "individualId" }, new Type[] { StringType.INSTANCE });
		assertFalse(result);
	}

	@Test(expected = ResidentServiceException.class)
	public void onSaveExceptionTest() {
		when(objectStoreHelper.encryptDecryptData(Mockito.anyString(), Mockito.eq(true), Mockito.anyString(),
				Mockito.anyString())).thenThrow(new ResidentServiceException(ResidentErrorCode.ENCRYPT_DECRYPT_ERROR));
		residentEntityInterceptor.onSave(residentTransactionEntity, "1", new Object[1],
				new String[] { "individualId" }, new Type[] { StringType.INSTANCE });
	}

	@Test
	public void onLoadTest() {
		when(objectStoreHelper.encryptDecryptData(Mockito.anyString(), Mockito.eq(false), Mockito.anyString(),
				Mockito.anyString())).thenReturn("decryptedData");
		boolean result = residentEntityInterceptor.onLoad(residentTransactionEntity, "1",
				new Object[] { "encryptedData" }, new String[] { "individualId" }, new Type[] { StringType.INSTANCE });
		assertTrue(result);
	}

	@Test(expected = ResidentServiceException.class)
	public void onLoadTest_exception() {
		when(objectStoreHelper.encryptDecryptData(Mockito.anyString(), Mockito.eq(false), Mockito.anyString(),
				Mockito.anyString())).thenThrow(new ResidentServiceException(ResidentErrorCode.ENCRYPT_DECRYPT_ERROR));
		residentEntityInterceptor.onLoad(residentTransactionEntity, "1",
				new Object[] { "encryptedData" }, new String[] { "individualId" }, new Type[] { StringType.INSTANCE });
	}

	@Test
	public void onFlushDirtyTest() {
		when(objectStoreHelper.encryptDecryptData(Mockito.anyString(), Mockito.eq(true), Mockito.anyString(),
				Mockito.anyString())).thenReturn("encryptedData");
		boolean result = residentEntityInterceptor.onFlushDirty(residentTransactionEntity, "1", new Object[1],
				new Object[1], new String[] { "individualId" }, new Type[] { StringType.INSTANCE });
		assertTrue(result);
	}
}
