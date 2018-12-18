package com.github.ryankenney.web_dimmer.util;

@SuppressWarnings("serial")
public class RuntimeInterruptedException extends RuntimeException {

	public RuntimeInterruptedException(InterruptedException cause) {
		super(cause);
	}

}
