package com.mantledillusion.data.saman.interfaces;

import com.mantledillusion.data.saman.ProcessingService;

/**
 * Interface for {@link Persistor}s.
 * <p>
 * A {@link Persistor} is a combination of a {@link Synchronizer} from source to
 * target and a {@link Converter} from target to source.
 * <p>
 * While {@link #fetchTarget(Object, ProcessingService)} has to be overridden
 * for the {@link Synchronizer} to be able to retrieve the target object to
 * synchronize into, {@link #persistTarget(Object, Object, ProcessingService)}
 * may only be overridden if additional steps are necessary to persist the
 * synchronized changes to the target object.
 *
 * @param <SourceType>
 *            The source type to synchronize from and convert to
 * @param <TargetType>
 *            The target type to synchronize to and convert from
 */
public interface Persistor<SourceType, TargetType>
		extends Synchronizer<SourceType, TargetType>, BiConverter<SourceType, TargetType> {

	@Override
	default TargetType process(SourceType source, ProcessingService service) throws Exception {
		return Synchronizer.super.process(source, service);
	}
}
