package com.mantledillusion.data.saman;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.data.saman.ConversionServiceImpl.ConversionFunction;
import com.mantledillusion.data.saman.exception.AmbiguousConverterException;
import com.mantledillusion.data.saman.exception.ConverterTypeException;
import com.mantledillusion.data.saman.interfaces.BiConverter;
import com.mantledillusion.data.saman.interfaces.Converter;

public class ConversionServiceFactory {

	private ConversionServiceFactory() {}
	


	// ############################################################################################################
	// ############################################# SERVICE CREATION #############################################
	// ############################################################################################################

	/**
	 * Factory method.
	 * <p>
	 * Creates a new {@link ConversionServiceImpl} of the given {@link Converter}s.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param converters
	 *            The {@link Converter}s to build the {@link ConversionServiceImpl}
	 *            from; might be empty or contain nulls.
	 * @return A new {@link ConversionServiceImpl} instance, never null
	 */
	public static <SourceType, TargetType> ConversionServiceImpl of(Converter<?, ?>... converters) {
		return of(Arrays.asList(converters));
	}

	/**
	 * Factory method.
	 * <p>
	 * Creates a new {@link ConversionServiceImpl} of the given {@link Converter}s.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param converters
	 *            The {@link Converter}s to build the {@link ConversionServiceImpl}
	 *            from; might be null, empty or contain nulls.
	 * @return A new {@link ConversionServiceImpl} instance, never null
	 */
	public static <SourceType, TargetType> ConversionServiceImpl of(Collection<Converter<?, ?>> converters) {
		Map<Class<?>, Map<Class<?>, ConversionFunction<?, ?>>> converterRegistry = new HashMap<>();

		if (converters != null) {
			for (Converter<?, ?> converter : converters) {
				if (converter != null) {
					Map<TypeVariable<?>, Type> types = TypeUtils.getTypeArguments(converter.getClass(), Converter.class);
					Class<?> sourceType = validateConverterTypeParameter(converter, types.get(Converter.class.getTypeParameters()[0]));
					Class<?> targetType = validateConverterTypeParameter(converter, types.get(Converter.class.getTypeParameters()[1]));

					@SuppressWarnings("unchecked")
					Converter<SourceType, TargetType> toTargetConverter = (Converter<SourceType, TargetType>) converter;
					ConversionFunction<SourceType, TargetType> function = (source, conversionService) -> toTargetConverter
							.toTarget(source, conversionService);
					
					addFunction(sourceType, targetType, converterRegistry, function);
					
					if (converter instanceof BiConverter) {
						@SuppressWarnings("unchecked")
						BiConverter<SourceType, TargetType> toSourceConverter = (BiConverter<SourceType, TargetType>) converter;
						ConversionFunction<TargetType, SourceType> function2 = (target, conversionService) -> toSourceConverter.toSource(target, conversionService);
						
						addFunction(targetType, sourceType, converterRegistry, function2);
					}
				}
			}
		}

		return new ConversionServiceImpl(converterRegistry);
	}

	private static Class<?> validateConverterTypeParameter(Converter<?, ?> converter, Type typeParameter) {
		if (typeParameter instanceof Class) {
			return (Class<?>) typeParameter;
		} else if (typeParameter instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) typeParameter).getRawType();
		} else {
			throw new ConverterTypeException(converter);
		}
	}
	
	private static <SourceType, TargetType> void addFunction(Class<?> sourceType, Class<?> targetType, 
			Map<Class<?>, Map<Class<?>, ConversionFunction<?, ?>>> converterRegistry, ConversionFunction<SourceType, TargetType> function) {
		if (!converterRegistry.containsKey(targetType)) {
			converterRegistry.put(targetType, new HashMap<>());
		} else if (converterRegistry.get(targetType).containsKey(sourceType)) {
			throw new AmbiguousConverterException(sourceType, targetType);
		}
		
		converterRegistry.get(targetType).put(sourceType, function);
	}
}
