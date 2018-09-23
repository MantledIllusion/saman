package com.mantledillusion.data.saman.interfaces;

import com.mantledillusion.data.saman.ProcessingService;
import com.mantledillusion.data.saman.ProcessingService.Processor;

/**
 * Interface for {@link Synchronizer}s.
 * <p>
 * A {@link Synchronizer} is a specialized {@link ProcessingService.Processor} extension that
 * synchronizes a source object's values into an already existing target object
 * instead of creating a new target object instance.
 * <p>
 * While {@link #fetchTarget(Object, ProcessingService)} has to be overridden
 * for the {@link Synchronizer} to be able to retrieve the target object to
 * synchronize into, {@link #persistTarget(Object, Object, ProcessingService)}
 * may only be overridden if additional steps are necessary to persist the
 * synchronized changes to the target object.
 *
 * @param <SourceType>
 *            The source type to synchronize from
 * @param <TargetType>
 *            The target type to synchronize to
 */
public interface Synchronizer<SourceType, TargetType> extends Processor<SourceType, TargetType> {

	@Override
	default TargetType process(SourceType source, ProcessingService service) throws Exception {
		TargetType target = fetchTarget(source, service);
		if (target != null) {
			toTarget(source, target, service);
		}
		return persistTarget(target, source, service);
	}

	/**
	 * Fetches the target to synchronize the given source into.
	 * 
	 * @param source
	 *            The source to map into the returned target; might be null.
	 * @param service
	 *            The calling {@link ProcessingService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 * @return The target to map the source into, might be null
	 * @throws Exception
	 *             Any type of {@link Exception} the fetching might cause.
	 */
	TargetType fetchTarget(SourceType source, ProcessingService service) throws Exception;

	/**
	 * Synchronizes the given source's values into the given target.
	 * 
	 * @param source
	 *            The source to synchronize; might be null.
	 * @param target
	 *            The target to synchronize into; might <b>not</b> be null.
	 * @param service
	 *            The calling {@link ProcessingService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 * @throws Exception
	 *             Any type of {@link Exception} the synchronization might cause.
	 */
	void toTarget(SourceType source, TargetType target, ProcessingService service) throws Exception;

	/**
	 * Persists the target synchronized into from the given source.
	 * 
	 * @param target
	 *            The target that has been synchronized into; might be null.
	 * @param source
	 *            The source that has been synchronized from; might be null.
	 * @param service
	 *            The calling {@link ProcessingService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 * @return The persisted target which might have changed due to persisting,
	 *         might be null
	 * @throws Exception
	 *             Any type of {@link Exception} the persisting might cause.
	 */
	default TargetType persistTarget(TargetType target, SourceType source, ProcessingService service) throws Exception {
		return target; // No operation by default
	}
}
