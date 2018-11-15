package com.mantledillusion.data.saman;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.mantledillusion.data.saman.exception.ProcessingException;
import com.mantledillusion.data.saman.exception.ProcessorException;
import com.mantledillusion.data.saman.context.ProcessingContext;

/**
 * Reference implementation of {@link ProcessingService}.
 */
public class DefaultProcessingService implements ProcessingService {

	private final ProcessorRegistry processorRegistry;

	public DefaultProcessingService(ProcessorRegistry processorRegistry) {
		if (processorRegistry == null) {
			throw new IllegalArgumentException("Cannot create a processing service with a null processor registry");
		}
		this.processorRegistry = processorRegistry;
	}

	private <SourceType, TargetType> TargetType execute(ProcessingService.Processor<SourceType, TargetType> processor,
			SourceType source) {
		try {
			return processor.process(source, new ProcessingContext(this));
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

		return execute(this.processorRegistry.identifyProcessor(sourceType, targetType), source);
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
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processNamed(
			SourceType source, Class<TargetType> targetType) {
		if (targetType == null) {
			throw new ProcessingException("Cannot process using a null target type.");
		}

		if (source == null) {
			return null;
		} else {
			Class<SourceType> sourceType = source.getDeclaringClass();
			return execute(this.processorRegistry.identifyNamedProcessor(sourceType, targetType), source);
		}
	}

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processOrdinal(
			SourceType source, Class<TargetType> targetType) {
		if (targetType == null) {
			throw new ProcessingException("Cannot process using a null target type.");
		}

		if (source == null) {
			return null;
		} else {
			Class<SourceType> sourceType = source.getDeclaringClass();
			return execute(this.processorRegistry.identifyOrdinalProcessor(sourceType, targetType), source);
		}
	}
}
