package com.qjsonlib;

import java.io.IOException;

public class JsonException extends IOException {
	private static final long serialVersionUID = 1L;

	public JsonException(String msg) {
		super(msg);
	}

	public JsonException(Throwable cause) {
		initCause(cause);
	}

	public JsonException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
