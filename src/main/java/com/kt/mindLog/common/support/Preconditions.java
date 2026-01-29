package com.kt.mindLog.common.support;

import com.kt.mindLog.common.exception.CustomException;
import com.kt.mindLog.common.exception.ErrorCode;

public class Preconditions {
	public static void validate(boolean expression, ErrorCode errorCode) {
		if (!expression) {
			throw new CustomException(errorCode);
		}
	}
}