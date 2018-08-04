package com.mantledillusion.data.saman;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mantledillusion.data.saman.exception.ConversionException;
import com.mantledillusion.data.saman.exception.NoConverterException;

class ConversionServiceImpl implements ConversionService {

	interface ConversionFunction<S, T> {

		T convert(S s, ConversionServiceImpl service) throws Exception;
	}

	private final Map<Class<?>, Map<Class<?>, ConversionFunction<?, ?>>> converterRegistry;

	ConversionServiceImpl(Map<Class<?>, Map<Class<?>, ConversionFunction<?, ?>>> converterRegistry) {
		this.converterRegistry = converterRegistry;
	}

	// ############################################################################################################
	// ############################################# SINGLE INSTANCES #############################################
	// ############################################################################################################

	@Override
	@SuppressWarnings("unchecked")
	public <SourceType, TargetType> TargetType convertStrictly(Class<SourceType> sourceType, SourceType source,
			Class<TargetType> targetType) {
		if (sourceType == null) {
			throw new IllegalArgumentException("Cannot convert using a null source type.");
		} else if (targetType == null) {
			throw new IllegalArgumentException("Cannot convert using a null target type.");
		}

		Class<? super SourceType> workType = sourceType;
		if (this.converterRegistry.containsKey(targetType)) {
			Map<Class<?>, ConversionFunction<?, ?>> targetTypeConverters = this.converterRegistry.get(targetType);
			do {
				if (targetTypeConverters.containsKey(workType)) {
					try {
						return ((ConversionFunction<SourceType, TargetType>) targetTypeConverters.get(workType))
								.convert(source, this);
					} catch (Exception e) {
						throw new ConversionException(e);
					}
				}
				workType = workType.getSuperclass();
			} while (workType != Object.class);
		}

		throw new NoConverterException(sourceType, targetType);
	}

	// ############################################################################################################
	// ############################################### COLLECTIONS ################################################
	// ############################################################################################################

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType convertInto(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType) {
		if (source != null && target != null) {
			for (SourceType sourceElement : source) {
				target.add(convert(sourceElement, targetType));
			}
		}
		return target;
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType convertStrictlyInto(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType) {
		if (source != null && target != null) {
			for (SourceType sourceElement : source) {
				target.add(convertStrictly(sourceType, sourceElement, targetType));
			}
		}
		return target;
	}

	// ############################################################################################################
	// ################################################### MAP ####################################################
	// ############################################################################################################

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> convertInto(
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
		if (source != null && target != null) {
			target = new HashMap<>();
			for (Entry<SourceTypeKey, SourceTypeValue> entry : source.entrySet()) {
				target.put(convert(entry.getKey(), targetTypeKey), convert(entry.getValue(), targetTypeValue));
			}
		}
		return target;
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> convertStrictlyInto(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
		if (source != null && target != null) {
			target = new HashMap<>();
			for (Entry<SourceTypeKey, SourceTypeValue> entry : source.entrySet()) {
				target.put(convertStrictly(sourceTypeKey, entry.getKey(), targetTypeKey),
						convertStrictly(sourceTypeValue, entry.getValue(), targetTypeValue));
			}
		}
		return target;
	}

	// ############################################################################################################
	// ################################################# SPECIAL ##################################################
	// ############################################################################################################

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType convertNamed(
			SourceType source, Class<TargetType> targetType) {
		if (targetType == null) {
			throw new IllegalArgumentException("Cannot convert using a null target type.");
		}
		return source == null ? null : Enum.valueOf(targetType, source.name());
	}

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType convertOrdinal(
			SourceType source, Class<TargetType> targetType) {
		if (targetType == null) {
			throw new IllegalArgumentException("Cannot convert using a null target type.");
		} else if (source != null && targetType.getEnumConstants().length >= source.ordinal()) {
			throw new IllegalArgumentException("The source value '" + source.name() + "'s ordinal " + source.ordinal()
					+ " is out of range for the target type '" + targetType.getSimpleName() + "'s ordinal range (0|"
					+ targetType.getEnumConstants().length + ").");
		}
		return source == null ? null : targetType.getEnumConstants()[source.ordinal()];
	}
}
