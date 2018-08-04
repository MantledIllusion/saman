package com.mantledillusion.data.saman.exception;

public class AmbiguousConverterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public <SourceType, TargetType> AmbiguousConverterException(Class<SourceType> sourceType,
			Class<TargetType> targetType) {
		super("Multiple converters detected for the conversion from '" + sourceType.getSimpleName()
				+ "' or one of its super types to '" + targetType.getSimpleName() + "'.");
	}
}
