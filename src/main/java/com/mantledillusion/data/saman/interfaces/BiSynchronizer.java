package com.mantledillusion.data.saman.interfaces;

import com.mantledillusion.data.saman.ProcessingService;
import com.mantledillusion.data.saman.ProcessingServiceFactory.BiProcessor;

/**
 * Interface for {@link BiSynchronizer}s.
 * <p>
 * A {@link BiSynchronizer} is a specialized {@link Synchronizer} extension that
 * is also able to handle the back synchronization.
 * <p>
 * While {@link #fetchTarget(Object)} has to be overridden for the
 * {@link BiSynchronizer} to be able to retrieve the target object to
 * synchronize into, {@link #persistTarget(Object, Object)} may only be
 * overridden if additional steps are necessary to persist the synchronized
 * changes to the target object.
 * <p>
 * While {@link #fetchSource(Object)} has to be overridden for the
 * {@link BiSynchronizer} to be able to retrieve the source object to
 * synchronize into, {@link #persistSource(Object, Object)} may only be
 * overridden if additional steps are necessary to persist the synchronized
 * changes to the source object.
 *
 * @param <SourceType>
 *            The source type to synchronize from
 * @param <TargetType>
 *            The target type to synchronize to
 */
public interface BiSynchronizer<SourceType, TargetType>
		extends Synchronizer<SourceType, TargetType>, BiProcessor<SourceType, TargetType> {

	@Override
	default SourceType reverse(TargetType target, ProcessingService service) throws Exception {
		SourceType source = fetchSource(target, service);
		if (source != null) {
			toSource(target, source, service);
		}
		return persistSource(source, target, service);
	}

	/**
	 * Fetches the source to synchronize the given target into.
	 * 
	 * @param target
	 *            The target to synchronize into the returned source; might be null.
	 * @return The source to synchronize the target into, might be null
	 * @param service
	 *            The calling {@link ProcessingService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 * @throws Exception
	 *             Any type of {@link Exception} the fetching might cause.
	 */
	SourceType fetchSource(TargetType target, ProcessingService service) throws Exception;

	/**
	 * Synchronizes the given target's values into the given source.
	 * 
	 * @param target
	 *            The target to synchronize; might be null.
	 * @param source
	 *            The source to synchronize into; might <b>not</b> be null.
	 * @param service
	 *            The calling {@link ProcessingService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 * @throws Exception
	 *             Any type of {@link Exception} the synchronization might cause.
	 */
	void toSource(TargetType target, SourceType source, ProcessingService service) throws Exception;

	/**
	 * Persists the source synchronized into from the given target.
	 * 
	 * @param source
	 *            The source that has been synchronized into; might be null.
	 * @param target
	 *            The target that has been synchronized from; might be null.
	 * @param service
	 *            The calling {@link ProcessingService} instance that might be used
	 *            as a callback if the conversion of sub objects of the given source
	 *            might be performed by the service as well; might <b>not</b> be
	 *            null.
	 * @return The persisted source which might have changed due to persisting,
	 *         might be null
	 * @throws Exception
	 *             Any type of {@link Exception} the persisting might cause.
	 */
	default SourceType persistSource(SourceType source, TargetType target, ProcessingService service) throws Exception {
		return source; // No operation by default
	}
}
