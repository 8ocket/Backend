package com.kt.mindLog.global.security.encryption;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

@Component
public class EncryptionKeyProvider {
	@Value("${encryption.secret-key}")
	private String rawKey;

	public SecretKeySpec getSecretKey() {
		byte[] keyBytes = rawKey.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length != 32) {
			throw new CustomException(ErrorCode.INVALID_CRYPTO_KEY);
		}
		return new SecretKeySpec(keyBytes, "AES");
	}
}
