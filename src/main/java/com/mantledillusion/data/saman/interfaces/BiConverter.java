package com.mantledillusion.data.saman.interfaces;

import com.mantledillusion.data.saman.ConversionService;

/**
 * An interface for {@link BiConverter}s.
 * <p>
 * A {@link BiConverter} is a specialized {@link Converter} extension that is
 * also able to handle the back conversion.
 *
 * @param <SourceType>
 *            The source type to convert from
 * @param <TargetType>
 *            The target type to convert to
 */
public interface BiConverter<SourceType, TargetType> extends Converter<SourceType, TargetType> {

	/**
	 * Converts the given target back to a source.
	 * 
	 * @param target
	 *            The target to convert; might be null.
	 * @param service
	 *            The calling {@link ConversionService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 * @return The source the given target has been converted into, might be null
	 * @throws Exception
	 *             Any type of {@link Exception} the conversion might cause.
	 */
	SourceType toSource(TargetType target, ConversionService service) throws Exception;
}
