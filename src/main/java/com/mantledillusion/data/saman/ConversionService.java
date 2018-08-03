package com.mantledillusion.data.saman;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.data.saman.exception.ConversionException;

public class ConversionService {

	private interface ConversionFunction<S, T> {

		T convert(S s, ConversionService service) throws Exception;
	}

	private final Map<Class<?>, Map<Class<?>, ConversionFunction<?, ?>>> converterRegistry;

	private ConversionService(Map<Class<?>, Map<Class<?>, ConversionFunction<?, ?>>> converterRegistry) {
		this.converterRegistry = converterRegistry;
	}

	// ############################################################################################################
	// ############################################# SINGLE INSTANCES #############################################
	// ############################################################################################################

	/**
	 * Converts the given source.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param source
	 *            The source object to convert; might be null, although in this case
	 *            no converter is called and null is returned.
	 * @param targetType
	 *            The target type to convert to; might <b>not</b> be null.
	 * @return The converted target object, always null if the source is null and
	 *         possibly null if the conversion's result is null
	 */
	@SuppressWarnings("unchecked")
	public <SourceType, TargetType> TargetType convert(SourceType source, Class<TargetType> targetType) {
		if (source == null) {
			return null;
		} else {
			return convertStrictly((Class<? super SourceType>) source.getClass(), source, targetType);
		}
	}

	/**
	 * Converts a simulated null value of the given source type.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param sourceType
	 *            The source type to simulate a null value for; might <b>not</b> be
	 *            null.
	 * @param targetType
	 *            The target type to convert to; might <b>not</b> be null.
	 * @return The converted target object, might be null if the conversion's result
	 *         is null
	 */
	public <SourceType, TargetType> TargetType convertNull(Class<SourceType> sourceType, Class<TargetType> targetType) {
		return convertStrictly(sourceType, null, targetType);
	}

	/**
	 * Converts the given source ensuring that a converter of the given
	 * source/target type is called even if the source object is null.
	 * <p>
	 * This method can be used to force the use of a super source type converter.
	 * For example, if B extends A and C is a target type, calling this method with
	 * an B instance, A as source and C as target type will force the use of an
	 * A-&gt;C converter, even if an B-&gt;C converter is available.
	 * <p>
	 * Functionally, this method basically is the combination of
	 * {@link #convert(Object, Class)} and {@link #convertNull(Class, Class)}.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param sourceType
	 *            The source type to convert; might <b>not</b> be null.
	 * @param source
	 *            The source object to convert; might be null.
	 * @param targetType
	 *            The target type to convert to; might <b>not</b> be null.
	 * @return The converted target object, might be null if the conversion's result
	 *         is null
	 */
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

		throw new RuntimeException("No converter for " + source.getClass().getSimpleName()
				+ " or any of its super types when converting to the target type " + targetType.getSimpleName());
	}

	// ############################################################################################################
	// ############################################### COLLECTIONS ################################################
	// ############################################################################################################

	/**
	 * Converts the given list of source objects to a new list of target objects.
	 * <p>
	 * Uses {@link #convert(Object, Class)}.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param source
	 *            The list of source objects to convert; might be null, although in
	 *            this case null is returned.
	 * @param targetType
	 *            The type to convert all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The converted target objects, might be null if the given source
	 *         object list was null
	 */
	public <SourceType, TargetType> List<TargetType> convertList(List<SourceType> source,
			Class<TargetType> targetType) {
		return source == null ? null : convertInto(source, new ArrayList<>(), targetType);
	}

	/**
	 * Converts the given set of source objects to a new set of target objects.
	 * <p>
	 * Uses {@link #convert(Object, Class)}.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param source
	 *            The set of source objects to convert; might be null, although in
	 *            this case null is returned.
	 * @param targetType
	 *            The type to convert all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The converted target objects, might be null if the given source
	 *         object set was null
	 */
	public <SourceType, TargetType> Set<TargetType> convertSet(Set<SourceType> source, Class<TargetType> targetType) {
		return source == null ? null : convertInto(source, new HashSet<>(), targetType);
	}

	/**
	 * Converts the given collection of source objects into the given collection of
	 * target objects.
	 * <p>
	 * Uses {@link #convert(Object, Class)}.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <SourceCollectionType>
	 *            The {@link Collection} type of the source elements
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param <TargetCollectionType>
	 *            The {@link Collection} type of the target elements
	 * @param source
	 *            The collection of source objects to convert; might be null.
	 * @param target
	 *            The target collection of objects to convert into; might be null
	 *            although in this case null is returned.
	 * @param targetType
	 *            The type to convert all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The given target collection, might be null if the given target
	 *         collection was null
	 */
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType convertInto(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType) {
		if (source != null && target != null) {
			for (SourceType sourceElement : source) {
				target.add(convert(sourceElement, targetType));
			}
		}
		return target;
	}

	/**
	 * Converts the given list of source objects to a new list of target objects
	 * ensuring that a converter of the given source/target type is called even if
	 * the source element is null.
	 * <p>
	 * Uses {@link #convertStrictly(Class, Object, Class)};
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param sourceType
	 *            The source type to convert; might <b>not</b> be null.
	 * @param source
	 *            The list of source objects to convert; might be null, although in
	 *            this case null is returned.
	 * @param targetType
	 *            The type to convert all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The converted target objects, might be null if the given source
	 *         object list was null
	 */
	public <SourceType, TargetType> List<TargetType> convertListStrictly(Class<SourceType> sourceType,
			List<SourceType> source, Class<TargetType> targetType) {
		return source == null ? null : convertStrictlyInto(sourceType, source, new ArrayList<>(), targetType);
	}

	/**
	 * Converts the given set of source objects to a new set of target objects
	 * ensuring that a converter of the given source/target type is called even if
	 * the source element is null.
	 * <p>
	 * Uses {@link #convertStrictly(Class, Object, Class)};
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param sourceType
	 *            The source type to convert; might <b>not</b> be null.
	 * @param source
	 *            The set of source objects to convert; might be null, although in
	 *            this case null is returned.
	 * @param targetType
	 *            The type to convert all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The converted target objects, might be null if the given source
	 *         object set was null
	 */
	public <SourceType, TargetType> Set<TargetType> convertSetStrictly(Class<SourceType> sourceType,
			Set<SourceType> source, Class<TargetType> targetType) {
		return source == null ? null : convertStrictlyInto(sourceType, source, new HashSet<>(), targetType);
	}

	/**
	 * Converts the given collection of source objects into the given collection of
	 * target objects ensuring that a converter of the given source/target type is
	 * called even if the source element is null.
	 * <p>
	 * Uses {@link #convertStrictly(Class, Object, Class)};
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <SourceCollectionType>
	 *            The {@link Collection} type of the source elements
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param <TargetCollectionType>
	 *            The {@link Collection} type of the target elements
	 * @param sourceType
	 *            The source type to convert; might <b>not</b> be null.
	 * @param source
	 *            The collection of source objects to convert; might be null.
	 * @param target
	 *            The target collection of objects to convert into; might be null
	 *            although in this case null is returned.
	 * @param targetType
	 *            The type to convert all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The given target collection, might be null if the given target
	 *         collection was null
	 */
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

	/**
	 * Converts the given map of source objects into the given collection of target
	 * objects.
	 * <p>
	 * Uses {@link #convert(Object, Class)}.
	 * 
	 * @param <SourceTypeKey>
	 *            The source type of the key elements
	 * @param <SourceTypeValue>
	 *            The source type of the value elements
	 * @param <TargetTypeKey>
	 *            The target type of the key elements
	 * @param <TargetTypeValue>
	 *            The target type of the value elements
	 * @param source
	 *            The map of source objects to convert; might be null, although in
	 *            this case null is returned.
	 * @param targetTypeKey
	 *            The type to convert all of the source key objects to; might
	 *            <b>not</b> be null.
	 * @param targetTypeValue
	 *            The type to convert all of the source value objects to; might
	 *            <b>not</b> be null.
	 * @return The converted target objects, might be null if the given source
	 *         object set was null
	 */
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> convertMap(
			Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
			Class<TargetTypeValue> targetTypeValue) {
		Map<TargetTypeKey, TargetTypeValue> target = null;
		if (source != null) {
			target = new HashMap<>();
			for (Entry<SourceTypeKey, SourceTypeValue> entry : source.entrySet()) {
				target.put(convert(entry.getKey(), targetTypeKey), convert(entry.getValue(), targetTypeValue));
			}
		}
		return target;
	}

	/**
	 * Converts the given map of source objects into the given collection of target
	 * objects ensuring that a converter of the given source/target type is called
	 * even if the source element is null.
	 * <p>
	 * Uses {@link #convertStrictly(Class, Object, Class)};
	 * 
	 * @param <SourceTypeKey>
	 *            The source type of the key elements
	 * @param <SourceTypeValue>
	 *            The source type of the value elements
	 * @param <TargetTypeKey>
	 *            The target type of the key elements
	 * @param <TargetTypeValue>
	 *            The target type of the value elements
	 * @param sourceTypeKey
	 *            The source key type to convert; might <b>not</b> be null.
	 * @param sourceTypeValue
	 *            The source value type to convert; might <b>not</b> be null.
	 * @param source
	 *            The map of source objects to convert; might be null, although in
	 *            this case null is returned.
	 * @param targetTypeKey
	 *            The type to convert all of the source key objects to; might
	 *            <b>not</b> be null.
	 * @param targetTypeValue
	 *            The type to convert all of the source value objects to; might
	 *            <b>not</b> be null.
	 * @return The converted target objects, might be null if the given source
	 *         object set was null
	 */
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> convertMapStrictly(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
			Class<TargetTypeValue> targetTypeValue) {
		Map<TargetTypeKey, TargetTypeValue> target = null;
		if (source != null) {
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

	/**
	 * Converts the given enumerated source object using its name.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param source
	 *            The source object to convert; might be null, although in this case
	 *            null is returned.
	 * @param targetType
	 *            The enumerated target type to convert to; might <b>not</b> be
	 *            null.
	 * @return The converted target object, always null if the source is null, never
	 *         null otherwise
	 */
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType convertNamed(
			SourceType source, Class<TargetType> targetType) {
		if (targetType == null) {
			throw new IllegalArgumentException("Cannot convert using a null target type.");
		}
		return source == null ? null : Enum.valueOf(targetType, source.name());
	}

	/**
	 * Converts the given enumerated source object using its ordinal.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param source
	 *            The source object to convert; might be null, although in this case
	 *            null is returned.
	 * @param targetType
	 *            The enumerated target type to convert to; might <b>not</b> be
	 *            null.
	 * @return The converted target object, always null if the source is null, never
	 *         null otherwise
	 */
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

	// ############################################################################################################
	// ############################################# SERVICE CREATION #############################################
	// ############################################################################################################

	/**
	 * Factory method.
	 * <p>
	 * Creates a new {@link ConversionService} of the given {@link Converter}s.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param converters
	 *            The {@link Converter}s to build the {@link ConversionService}
	 *            from; might be null or empty.
	 * @return A new {@link ConversionService} instance, never null
	 */
	public static <SourceType, TargetType> ConversionService of(Collection<Converter<?, ?>> converters) {
		Map<Class<?>, Map<Class<?>, ConversionFunction<?, ?>>> converterRegistry = new HashMap<>();

		if (converters != null) {
			for (Converter<?, ?> converter : converters) {
				Map<TypeVariable<?>, Type> types = TypeUtils.getTypeArguments(converter.getClass(), Converter.class);
				Class<?> sourceType = validateConverterTypeParameter(types.get(Converter.class.getTypeParameters()[0]));
				Class<?> targetType = validateConverterTypeParameter(types.get(Converter.class.getTypeParameters()[1]));

				if (!converterRegistry.containsKey(targetType)) {
					converterRegistry.put(targetType, new HashMap<>());
				}

				@SuppressWarnings("unchecked")
				Converter<SourceType, TargetType> typedConverter = (Converter<SourceType, TargetType>) converter;
				ConversionFunction<SourceType, TargetType> function = (source, conversionService) -> typedConverter
						.toTarget(source, conversionService);
				converterRegistry.get(targetType).put(sourceType, function);
			}
		}

		return new ConversionService(converterRegistry);
	}

	private static Class<?> validateConverterTypeParameter(Type typeParameter) {
		if (typeParameter instanceof Class) {
			return (Class<?>) typeParameter;
		} else if (typeParameter instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) typeParameter).getRawType();
		} else {
			throw new RuntimeException("Wrong converter generic param type");
		}
	}
}
