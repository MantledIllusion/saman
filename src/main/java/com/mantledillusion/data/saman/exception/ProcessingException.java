package com.mantledillusion.data.saman.exception;

import javax.annotation.processing.Processor;

/**
 * {@link RuntimeException} that might be thrown if the processing by a
 * {@link Processor} cannot be triggered out of various reasons.
 */
public class ProcessingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProcessingException(String message) {
		super(message);
	}
}
