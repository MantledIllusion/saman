package com.mantledillusion.data.saman.exception;

import com.mantledillusion.data.saman.interfaces.Converter;

public class ConverterTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConverterTypeException(Converter<?, ?> converter) {
		super("The generic source/target type parameters of the converter interface have to be fully defined to concrete types; the converter '"
				+ converter.getClass().getSimpleName() + "' however does not.");
	}
}
