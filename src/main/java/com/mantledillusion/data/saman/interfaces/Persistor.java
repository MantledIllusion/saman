package com.mantledillusion.data.saman.interfaces;

/**
 * Interface for {@link Persistor}s.
 * <p>
 * A {@link Persistor} is a combination of a {@link Synchronizer} from source to target and a {@link Converter} from target to source.
 * <p>
 * While {@link #fetchTarget(Object)} has to be overridden for the
 * {@link Synchronizer} to be able to retrieve the target object to synchronize
 * into, {@link #persistTarget(Object, Object)} may only be overridden if
 * additional steps are necessary to persist the synchronized changes to the
 * target object.
 *
 * @param <SourceType>
 *            The source type to synchronize from
 * @param <TargetType>
 *            The target type to synchronize to
 */
public interface Persistor<SourceType, TargetType> extends Synchronizer<SourceType, TargetType>, BiConverter<SourceType, TargetType> {
	
}
