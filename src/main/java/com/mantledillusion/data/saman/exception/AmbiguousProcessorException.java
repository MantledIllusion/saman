package com.mantledillusion.data.saman.exception;

import com.mantledillusion.data.saman.ProcessingServiceFactory.Processor;

/**
 * {@link RuntimeException} that might be thrown if 2 or more {@link Processor}s
 * are defined for processing the same source to the same target type.
 */
public class AmbiguousProcessorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public <SourceType, TargetType> AmbiguousProcessorException(Class<SourceType> sourceType,
			Class<TargetType> targetType) {
		super("Multiple processors detected for the processing of '" + sourceType.getSimpleName()
				+ "' or one of its super types to '" + targetType.getSimpleName() + "'.");
	}
}
