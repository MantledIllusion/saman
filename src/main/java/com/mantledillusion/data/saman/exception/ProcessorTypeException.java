package com.mantledillusion.data.saman.exception;

import com.mantledillusion.data.saman.ProcessingService.Processor;

/**
 * {@link RuntimeException} that might be thrown when trying to define a
 * {@link Processor} whose generic parameters are not fully defined.
 */
public class ProcessorTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProcessorTypeException(Processor<?, ?> processor) {
		super("The generic source/target type parameters of the processor interface have to be fully defined to concrete types; the processor '"
				+ processor.getClass().getSimpleName() + "' however does not.");
	}
}
