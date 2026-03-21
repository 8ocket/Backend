package com.kt.mindLog.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {
	private final String region;
	private final S3 s3;

	public AwsProperties(String region, S3 s3) {
		this.region = region;
		this.s3 = s3;
	}

	@Getter
	public static class S3 {
		private final String bucket;

		public S3(String bucket) {
			this.bucket = bucket;
		}
	}
}
