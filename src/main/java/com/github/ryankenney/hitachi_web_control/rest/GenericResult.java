package com.github.ryankenney.hitachi_web_control.rest;

public class GenericResult {

	public static enum RESULT_STATUS {
		OK,
		ERROR
	}

	private RESULT_STATUS status;
	private String errorReference;

	public GenericResult() {}

	public GenericResult(RESULT_STATUS status) {
		this.status = status;
	}

	public GenericResult(RESULT_STATUS status, String errorReference) {
		this.status = status;
		this.errorReference =errorReference;
	}

	public RESULT_STATUS getStatus() {
		return status;
	}

	public void setStatus(RESULT_STATUS status) {
		this.status = status;
	}

	public String getErrorReference() {
		return errorReference;
	}

	public void setErrorReference(String errorReference) {
		this.errorReference = errorReference;
	}
}
