package com.kt.mindLog.global.security.encryption;

import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@Component
@RequiredArgsConstructor
public class EncryptionConverter implements AttributeConverter<String, String> {
	private final AesGcmEncryptor encryptor;

	@Override
	public String convertToDatabaseColumn(String plainText) {
		return encryptor.encrypt(plainText);
	}

	@Override
	public String convertToEntityAttribute(String encryptedText) {
		return encryptor.decrypt(encryptedText);
	}
}
