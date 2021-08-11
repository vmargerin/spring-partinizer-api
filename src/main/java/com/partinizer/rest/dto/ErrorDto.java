package com.partinizer.rest.dto;

public class ErrorDto implements Dto {

	private final String errorMessage;

	public ErrorDto(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
