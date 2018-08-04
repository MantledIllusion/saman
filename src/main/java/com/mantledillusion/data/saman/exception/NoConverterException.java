package com.mantledillusion.data.saman.exception;

public class NoConverterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public <SourceType, TargetType> NoConverterException(Class<SourceType> sourceType, Class<TargetType> targetType) {
		super("No converter available to convert from '"+sourceType.getSimpleName()+"' or one of its super types to '"+targetType.getSimpleName()+"'.");
	}
}
