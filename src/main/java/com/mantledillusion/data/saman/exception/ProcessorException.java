package com.mantledillusion.data.saman.exception;

import com.mantledillusion.data.saman.ProcessingServiceFactory.Processor;

/**
 * {@link Exception} that is used to wrap any type of {@link Exception} that is
 * thrown by a {@link Processor} during processing.
 */
public class ProcessorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProcessorException(Exception ex) {
		super(ex);
	}
}
