package com.mantledillusion.data.saman.exception;

import com.mantledillusion.data.saman.interfaces.Converter;

/**
 * {@link Exception} that is used to wrap any type of {@link Exception} that is
 * thrown by a {@link Converter} during a conversion.
 */
public class ConversionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param ex
	 *            The exception to wrap that has been thrown during the conversion.
	 */
	public ConversionException(Exception ex) {
		super(ex);
	}
}
