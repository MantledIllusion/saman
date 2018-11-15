package com.mantledillusion.data.saman.context;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import com.mantledillusion.data.saman.ProcessingService;

public class ProcessingContext implements ProcessingService {

	private final ProcessingService service;
	private final Map<Class<?>, Object> context = new WeakHashMap<>();

	public ProcessingContext(ProcessingService service) {
		this.service = service;
	}
	
	public <T> boolean has(Class<T> valueType) {
		return this.context.containsKey(valueType);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> valueType) {
		if (valueType == null) {
			throw new IllegalArgumentException("No context value available for a null value type");
		} else if (!this.context.containsKey(valueType)) {
			throw new IllegalStateException("No context value available for type "+valueType.getName());
		}
		return (T) this.context.get(valueType);
	}
	
	public <T> ProcessingContext set(T value) {
		if (value == null) {
			throw new IllegalArgumentException("Cannot set a null context value");
		}
		this.context.put(value.getClass(), value);
		return this;
	}
	
	public <T> void remove(Class<T> valueType) {
		if (valueType == null) {
			throw new IllegalArgumentException("Cannot remove a context value by a null value type");
		}
		this.context.remove(valueType);
	}
	
	public void clear() {
		this.context.clear();
	}

	// DELEGATED
	
	@Override
	public <SourceType, TargetType> TargetType processStrictly(Class<SourceType> sourceType, SourceType source,
			Class<TargetType> targetType) {
		return this.service.processStrictly(sourceType, source, targetType);
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processInto(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType) {
		return this.service.processInto(source, target, targetType);
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processStrictlyInto(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType) {
		return this.service.processStrictlyInto(sourceType, source, target, targetType);
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processInto(
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
		return this.service.processInto(source, target, targetTypeKey, targetTypeValue);
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processStrictlyInto(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
		return this.service.processStrictlyInto(sourceTypeKey, sourceTypeValue, source, target, targetTypeKey, targetTypeValue);
	}

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processNamed(
			SourceType source, Class<TargetType> targetType) {
		return this.service.processNamed(source, targetType);
	}

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processOrdinal(
			SourceType source, Class<TargetType> targetType) {
		return this.service.processOrdinal(source, targetType);
	}
}
