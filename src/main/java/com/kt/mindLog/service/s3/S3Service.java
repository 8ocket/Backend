package com.kt.mindLog.service.s3;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${spring.servlet.multipart.max-file-size}")
	private long maxFileSize;

	private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

	public String uploadProfileImage(MultipartFile profile) {

		validateFile(profile);

		String fileName = "profile/" + UUID.randomUUID() + "_" + profile.getOriginalFilename();

		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(profile.getSize());
			metadata.setContentType(profile.getContentType());

			amazonS3.putObject(
				new PutObjectRequest(bucket, fileName, profile.getInputStream(), metadata)
			);
		} catch (AmazonS3Exception e) {
			log.error("S3 upload failed. Status Code: {}, Error Message: {}", e.getStatusCode(), e.getMessage());
			throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);

		} catch (IOException e) {
			log.error("File upload IO exception: {}", e.getMessage(), e);
			throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
		}

		return amazonS3.getUrl(bucket, fileName).toString();
	}

	private void validateFile(MultipartFile profile) {
		// 빈 파일 여부 검증
		Preconditions.validate(!profile.isEmpty(), ErrorCode.EMPTY_FILE);

		// 이미지 파일 타입 검증
		Preconditions.validate(
			profile.getContentType() != null && profile.getContentType().startsWith("image"),
			ErrorCode.INVALID_FILE_TYPE
		);

		// 파일 크기 검증
		Preconditions.validate(profile.getSize() <= maxFileSize, ErrorCode.FILE_SIZE_EXCEEDED);

		// 확장자 검증
		String extension = extractExtenstion(profile.getOriginalFilename());
		Preconditions.validate(
			ALLOWED_EXTENSIONS.contains(extension.toLowerCase()),
			ErrorCode.INVALID_FILE_EXTENSION
		);
	}

	private String extractExtenstion(String fileName) {
		Preconditions.validate(
			fileName != null && fileName.contains("."),
			ErrorCode.INVALID_FILE_EXTENSION
		);
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
}
