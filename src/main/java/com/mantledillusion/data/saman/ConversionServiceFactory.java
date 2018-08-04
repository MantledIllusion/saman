package com.mantledillusion.data.saman;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.data.saman.ConversionServiceImpl.ConversionFunction;
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
	 *            from; might be null or empty.
	 * @return A new {@link ConversionServiceImpl} instance, never null
	 */
	public static <SourceType, TargetType> ConversionServiceImpl of(Collection<Converter<?, ?>> converters) {
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

		return new ConversionServiceImpl(converterRegistry);
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
