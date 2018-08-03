package com.mantledillusion.data.saman;

/**
 * Interface for {@link Mapper}s.
 * <p>
 * A {@link Mapper} is a specialized {@link Converter} extension that maps a
 * source object's values to an already existing target object instead of
 * creating a new target object instance.
 * <p>
 * While {@link #fetchTarget(Object)} has to be overridden for the
 * {@link Mapper} to be able to retrieve the target object to map to,
 * {@link #persistTarget(Object, Object)} may only be overridden if additional
 * steps are necessary to persist the mapped changes to the target object.
 *
 * @param <SourceType>
 *            The source type to convert from
 * @param <TargetType>
 *            The target type to convert to
 */
public interface Mapper<SourceType, TargetType> extends Converter<SourceType, TargetType> {

	@Override
	default TargetType toTarget(SourceType source, ConversionService service) {
		TargetType target = fetchTarget(source);
		if (target != null) {
			toTarget(source, target, service);
		}
		persistTarget(source, target);
		return target;
	}

	/**
	 * Fetches the target to map the given source into.
	 * 
	 * @param source
	 *            The source to map into the returned target; might be null.
	 * @return The target to map the source into, might be null
	 */
	TargetType fetchTarget(SourceType source);

	/**
	 * Maps the given source's values into the given target.
	 * 
	 * @param source
	 *            The source to map; might be null.
	 * @param target
	 *            The target to map into; might <b>not</b> be null.
	 * @param service
	 *            The calling {@link ConversionService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 */
	void toTarget(SourceType source, TargetType target, ConversionService service);

	/**
	 * Persists the target mapped into from the given source.
	 * 
	 * @param source
	 *            The source that has been mapped from; might be null.
	 * @param target
	 *            The target that has been mapped into; might be null.
	 */
	default void persistTarget(SourceType source, TargetType target) {
		// No operation by default
	}
}
