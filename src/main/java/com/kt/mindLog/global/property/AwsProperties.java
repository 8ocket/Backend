package com.kt.mindLog.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {
	private final String region;
	private final S3 s3;
	private final Credentials credentials;

	public AwsProperties(String region, S3 s3, Credentials credentials) {
		this.region = region;
		this.s3 = s3;
		this.credentials = credentials;
	}

	@Getter
	public static class S3 {
		private final String bucket;

		public S3(String bucket) {
			this.bucket = bucket;
		}
	}

	@Getter
	public static class Credentials {
		private final String accessKey;
		private final String secretKey;

		public Credentials(String accessKey, String secretKey) {
			this.accessKey = accessKey;
			this.secretKey = secretKey;
		}
	}
}
