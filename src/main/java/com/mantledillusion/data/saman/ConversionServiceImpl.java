package com.mantledillusion.data.saman;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mantledillusion.data.saman.exception.ConversionException;
import com.mantledillusion.data.saman.exception.ConverterException;
import com.mantledillusion.data.saman.exception.NoConverterException;

class ConversionServiceImpl implements ConversionService {

	interface ConversionFunction<S, T> {

		T convert(S s, ConversionServiceImpl service) throws Exception;
	}

	private final Map<Class<?>, Map<Class<?>, ConversionFunction<?, ?>>> converterRegistry;

	ConversionServiceImpl(Map<Class<?>, Map<Class<?>, ConversionFunction<?, ?>>> converterRegistry) {
		this.converterRegistry = converterRegistry;
	}

	private <SourceType, TargetType> TargetType execute(ConversionFunction<SourceType, TargetType> function, SourceType source) {
		try {
			return function.convert(source, this);
		} catch (Exception e) {
			throw new ConverterException(e);
		}
	}
	
	// ############################################################################################################
	// ############################################# SINGLE INSTANCES #############################################
	// ############################################################################################################

	@Override
	@SuppressWarnings("unchecked")
	public <SourceType, TargetType> TargetType convertStrictly(Class<SourceType> sourceType, SourceType source,
			Class<TargetType> targetType) {
		if (sourceType == null) {
			throw new ConversionException("Cannot convert using a null source type.");
		} else if (targetType == null) {
			throw new ConversionException("Cannot convert using a null target type.");
		}

		if (sourceType.equals(targetType)) {
			return (TargetType) source;
		}

		Class<? super SourceType> workType = sourceType;
		if (this.converterRegistry.containsKey(targetType)) {
			Map<Class<?>, ConversionFunction<?, ?>> targetTypeConverters = this.converterRegistry.get(targetType);
			do {
				if (targetTypeConverters.containsKey(workType)) {
					return execute((ConversionFunction<SourceType, TargetType>) targetTypeConverters.get(workType), source);
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
	@SuppressWarnings("unchecked")
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType convertNamed(
			SourceType source, Class<TargetType> targetType) {
		if (targetType == null) {
			throw new ConversionException("Cannot convert using a null target type.");
		}
		
		if (source == null) {
			return null;
		} else {
			Class<SourceType> sourceType = source.getDeclaringClass();

			if (!this.converterRegistry.containsKey(sourceType)) {
				this.converterRegistry.put(sourceType, new HashMap<>());
			}
			ConversionFunction<SourceType, TargetType> function;
			if (!this.converterRegistry.get(sourceType).containsKey(targetType)) {
				for (SourceType value : sourceType.getEnumConstants()) {
					try {
						Enum.valueOf(targetType, value.name());
					} catch (IllegalArgumentException e) {
						throw new ConversionException("The type '" + sourceType.getSimpleName() + "' cannot be mapped to '"
								+ targetType.getSimpleName() + "' by name; there is at least one enum value ('"
								+ value.name() + "') where there is no equally named value in the target enum type.");
					}
				}

				function = (sourceValue, conversionService) -> sourceValue == null ? null
						: Enum.valueOf(targetType, sourceValue.name());
				this.converterRegistry.get(sourceType).put(targetType, function);
			} else {
				function = (ConversionFunction<SourceType, TargetType>) this.converterRegistry.get(sourceType)
						.get(targetType);
			}

			return execute(function, source);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType convertOrdinal(
			SourceType source, Class<TargetType> targetType) {
		if (targetType == null) {
			throw new ConversionException("Cannot convert using a null target type.");
		}
		
		if (source == null) {
			return null;
		} else {
			Class<SourceType> sourceType = source.getDeclaringClass();

			if (!this.converterRegistry.containsKey(sourceType)) {
				this.converterRegistry.put(sourceType, new HashMap<>());
			}
			ConversionFunction<SourceType, TargetType> function;
			if (!this.converterRegistry.get(sourceType).containsKey(targetType)) {
				if (sourceType.getEnumConstants().length != targetType.getEnumConstants().length) {
					throw new ConversionException("The type '" + sourceType.getSimpleName() + "' cannot be mapped to '"
							+ targetType.getSimpleName()
							+ "' by ordinal; the amount of enum values are differing between source/target enum type: ("
							+ sourceType.getEnumConstants().length + "|" + targetType.getEnumConstants().length + ").");
				}

				function = (sourceValue, conversionService) -> sourceValue == null ? null
						: targetType.getEnumConstants()[sourceValue.ordinal()];
				this.converterRegistry.get(sourceType).put(targetType, function);
			} else {
				function = (ConversionFunction<SourceType, TargetType>) this.converterRegistry.get(sourceType)
						.get(targetType);
			}

			return execute(function, source);
		}
	}
}
