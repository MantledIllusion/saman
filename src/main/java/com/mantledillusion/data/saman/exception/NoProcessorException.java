package com.mantledillusion.data.saman.exception;

import javax.annotation.processing.Processor;

/**
 * {@link RuntimeException} that might be thrown when there is no
 * {@link Processor} available to process a specific source type or one of its
 * super types to a specific target type.
 */
public class NoProcessorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public <SourceType, TargetType> NoProcessorException(Class<SourceType> sourceType, Class<TargetType> targetType) {
		super("No processor available to process an instance of '" + sourceType.getSimpleName()
				+ "' or one of its super types to '" + targetType.getSimpleName() + "'.");
	}
}
