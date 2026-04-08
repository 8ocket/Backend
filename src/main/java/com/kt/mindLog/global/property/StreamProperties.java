package com.kt.mindLog.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "stream")
public class StreamProperties {
	private final String baseurl;
	private final String messageUri;
	private final String finalizeUri;
	private final String reportUri;
	private final String sessionUri;
}
