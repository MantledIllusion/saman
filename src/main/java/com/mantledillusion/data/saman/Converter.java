package com.mantledillusion.data.saman;

/**
 * An interface for {@link Converter}s.
 * <p>
 * A converter is basically a function from a source to a target type.
 *
 * @param <SourceType>
 *            The source type to convert from
 * @param <TargetType>
 *            The target type to convert to
 */
public interface Converter<SourceType, TargetType> {

	/**
	 * Converts the given source to a target.
	 * 
	 * @param source
	 *            The source to convert; might be null.
	 * @param service
	 *            The calling {@link ConversionService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 * @return The target the given source has been converted into, might be null
	 * @throws Exception
	 *             Any type of {@link Exception} the conversion might cause.
	 */
	TargetType toTarget(SourceType source, ConversionService service) throws Exception;
}
