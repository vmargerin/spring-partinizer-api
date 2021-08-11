package com.partinizer.domain;

public class Result<T> {

	private final boolean success;

	private final T target;

	private final String message;

	public Result(final boolean success, final T target, final String message) {
		this.success = success;
		this.target = target;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getTarget() {
		return target;
	}

	public String getMessage() {
		return message;
	}

}
