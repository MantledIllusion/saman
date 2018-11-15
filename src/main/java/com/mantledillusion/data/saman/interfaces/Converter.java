package com.mantledillusion.data.saman.interfaces;

import com.mantledillusion.data.saman.ProcessingService;
import com.mantledillusion.data.saman.ProcessingService.Processor;
import com.mantledillusion.data.saman.context.ProcessingContext;

/**
 * An interface for {@link Converter}s.
 * <p>
 * A {@link Converter} is basically a function from a source to a target type.
 *
 * @param <SourceType>
 *            The source type to convert from
 * @param <TargetType>
 *            The target type to convert to
 */
public interface Converter<SourceType, TargetType> extends Processor<SourceType, TargetType> {

	@Override
	default TargetType process(SourceType source, ProcessingContext context) throws Exception {
		return toTarget(source, context);
	}
	
	/**
	 * Converts the given source to a target.
	 * 
	 * @param source
	 *            The source to convert; might be null.
	 * @param context
	 *            The context of the calling {@link ProcessingService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 * @return The target the given source has been converted into, might be null
	 * @throws Exception
	 *             Any type of {@link Exception} the conversion might cause.
	 */
	TargetType toTarget(SourceType source, ProcessingContext context) throws Exception;
}
