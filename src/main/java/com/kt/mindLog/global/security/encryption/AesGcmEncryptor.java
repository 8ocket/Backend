package com.kt.mindLog.global.security.encryption;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AesGcmEncryptor {

	private static final int GCM_IV_LENGTH = 12;
	private static final int GCM_TAG_LENGTH = 128;

	private final EncryptionKeyProvider keyProvider;

	public String encrypt(String plainText) {
		try {
			byte[] iv = new byte[GCM_IV_LENGTH];
			new SecureRandom().nextBytes(iv);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, keyProvider.getSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH, iv));

			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			// IV + 암호화된 데이터를 합쳐서 Base64로 인코딩
			byte[] combined = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

			return Base64.getEncoder().encodeToString(combined);
		} catch (Exception e) {
			throw new RuntimeException("암호화 실패", e);
		}
	}

	public String decrypt(String encryptedText) {
		try {
			byte[] combined = Base64.getDecoder().decode(encryptedText);

			byte[] iv = new byte[GCM_IV_LENGTH];
			System.arraycopy(combined, 0, iv, 0, iv.length);

			byte[] encrypted = new byte[combined.length - iv.length];
			System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, keyProvider.getSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH, iv));

			return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("복호화 실패", e);
		}
	}
}
