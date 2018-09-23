package com.mantledillusion.data.saman;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mantledillusion.data.saman.exception.ProcessingException;
import com.mantledillusion.data.saman.exception.ProcessorException;
import com.mantledillusion.data.saman.ProcessingServiceFactory.Processor;
import com.mantledillusion.data.saman.exception.NoProcessorException;

class ProcessingServiceImpl implements ProcessingService {

	private final Map<Class<?>, Map<Class<?>, Processor<?, ?>>> processorRegistry;

	ProcessingServiceImpl(Map<Class<?>, Map<Class<?>, Processor<?, ?>>> processorRegistry) {
		this.processorRegistry = processorRegistry;
	}

	private <SourceType, TargetType> TargetType execute(Processor<SourceType, TargetType> processor, SourceType source) {
		try {
			return processor.process(source, this);
		} catch (Exception e) {
			throw new ProcessorException(e);
		}
	}
	
	// ############################################################################################################
	// ############################################# SINGLE INSTANCES #############################################
	// ############################################################################################################

	@Override
	@SuppressWarnings("unchecked")
	public <SourceType, TargetType> TargetType processStrictly(Class<SourceType> sourceType, SourceType source,
			Class<TargetType> targetType) {
		if (sourceType == null) {
			throw new ProcessingException("Cannot process using a null source type.");
		} else if (targetType == null) {
			throw new ProcessingException("Cannot process using a null target type.");
		}

		if (sourceType.equals(targetType)) {
			return (TargetType) source;
		}

		Class<? super SourceType> workType = sourceType;
		if (this.processorRegistry.containsKey(targetType)) {
			Map<Class<?>, Processor<?, ?>> targetTypeProcessors = this.processorRegistry.get(targetType);
			do {
				if (targetTypeProcessors.containsKey(workType)) {
					return execute((Processor<SourceType, TargetType>) targetTypeProcessors.get(workType), source);
				}
				workType = workType.getSuperclass();
			} while (workType != Object.class);
		}

		throw new NoProcessorException(sourceType, targetType);
	}

	// ############################################################################################################
	// ############################################### COLLECTIONS ################################################
	// ############################################################################################################

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processInto(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType) {
		if (source != null && target != null) {
			for (SourceType sourceElement : source) {
				target.add(process(sourceElement, targetType));
			}
		}
		return target;
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processStrictlyInto(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType) {
		if (source != null && target != null) {
			for (SourceType sourceElement : source) {
				target.add(processStrictly(sourceType, sourceElement, targetType));
			}
		}
		return target;
	}

	// ############################################################################################################
	// ################################################### MAP ####################################################
	// ############################################################################################################

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processInto(
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
		if (source != null && target != null) {
			for (Entry<SourceTypeKey, SourceTypeValue> entry : source.entrySet()) {
				target.put(process(entry.getKey(), targetTypeKey), process(entry.getValue(), targetTypeValue));
			}
		}
		return target;
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processStrictlyInto(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
		if (source != null && target != null) {
			for (Entry<SourceTypeKey, SourceTypeValue> entry : source.entrySet()) {
				target.put(processStrictly(sourceTypeKey, entry.getKey(), targetTypeKey),
						processStrictly(sourceTypeValue, entry.getValue(), targetTypeValue));
			}
		}
		return target;
	}

	// ############################################################################################################
	// ################################################# SPECIAL ##################################################
	// ############################################################################################################

	@Override
	@SuppressWarnings("unchecked")
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processNamed(
			SourceType source, Class<TargetType> targetType) {
		if (targetType == null) {
			throw new ProcessingException("Cannot process using a null target type.");
		}
		
		if (source == null) {
			return null;
		} else {
			Class<SourceType> sourceType = source.getDeclaringClass();

			if (!this.processorRegistry.containsKey(sourceType)) {
				this.processorRegistry.put(sourceType, new HashMap<>());
			}
			Processor<SourceType, TargetType> function;
			if (!this.processorRegistry.get(sourceType).containsKey(targetType)) {
				for (SourceType value : sourceType.getEnumConstants()) {
					try {
						Enum.valueOf(targetType, value.name());
					} catch (IllegalArgumentException e) {
						throw new ProcessingException("The type '" + sourceType.getSimpleName() + "' cannot be mapped to '"
								+ targetType.getSimpleName() + "' by name; there is at least one enum value ('"
								+ value.name() + "') where there is no equally named value in the target enum type.");
					}
				}

				function = (sourceValue, processingService) -> sourceValue == null ? null
						: Enum.valueOf(targetType, sourceValue.name());
				this.processorRegistry.get(sourceType).put(targetType, function);
			} else {
				function = (Processor<SourceType, TargetType>) this.processorRegistry.get(sourceType)
						.get(targetType);
			}

			return execute(function, source);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processOrdinal(
			SourceType source, Class<TargetType> targetType) {
		if (targetType == null) {
			throw new ProcessingException("Cannot process using a null target type.");
		}
		
		if (source == null) {
			return null;
		} else {
			Class<SourceType> sourceType = source.getDeclaringClass();

			if (!this.processorRegistry.containsKey(sourceType)) {
				this.processorRegistry.put(sourceType, new HashMap<>());
			}
			Processor<SourceType, TargetType> function;
			if (!this.processorRegistry.get(sourceType).containsKey(targetType)) {
				if (sourceType.getEnumConstants().length != targetType.getEnumConstants().length) {
					throw new ProcessingException("The type '" + sourceType.getSimpleName() + "' cannot be mapped to '"
							+ targetType.getSimpleName()
							+ "' by ordinal; the amount of enum values are differing between source/target enum type: ("
							+ sourceType.getEnumConstants().length + "|" + targetType.getEnumConstants().length + ").");
				}

				function = (sourceValue, processingService) -> sourceValue == null ? null
						: targetType.getEnumConstants()[sourceValue.ordinal()];
				this.processorRegistry.get(sourceType).put(targetType, function);
			} else {
				function = (Processor<SourceType, TargetType>) this.processorRegistry.get(sourceType)
						.get(targetType);
			}

			return execute(function, source);
		}
	}
}
