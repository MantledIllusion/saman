package com.mantledillusion.data.saman;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.data.saman.exception.AmbiguousProcessorException;
import com.mantledillusion.data.saman.exception.ProcessorTypeException;

public class ProcessingServiceFactory {
	
	public interface Processor<SourceType, TargetType> {

		TargetType process(SourceType source, ProcessingService service) throws Exception;
	}
	
	public interface BiProcessor<SourceType, TargetType> extends Processor<SourceType, TargetType> {

		SourceType reverse(TargetType target, ProcessingService service) throws Exception;
	}

	private ProcessingServiceFactory() {}
	
	// ############################################################################################################
	// ############################################# SERVICE CREATION #############################################
	// ############################################################################################################

	/**
	 * Factory method.
	 * <p>
	 * Creates a new {@link ProcessingServiceImpl} of the given {@link Processor}s.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param processors
	 *            The {@link Processor}s to build the {@link ProcessingServiceImpl}
	 *            from; might be empty or contain nulls.
	 * @return A new {@link ProcessingServiceImpl} instance, never null
	 */
	public static <SourceType, TargetType> ProcessingServiceImpl of(Processor<?, ?>... processors) {
		return of(Arrays.asList(processors));
	}

	/**
	 * Factory method.
	 * <p>
	 * Creates a new {@link ProcessingServiceImpl} of the given {@link Processor}s.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param processors
	 *            The {@link Processor}s to build the {@link ProcessingServiceImpl}
	 *            from; might be null, empty or contain nulls.
	 * @return A new {@link ProcessingServiceImpl} instance, never null
	 */
	public static <SourceType, TargetType> ProcessingServiceImpl of(Collection<Processor<?, ?>> processors) {
		Map<Class<?>, Map<Class<?>, Processor<?, ?>>> processorRegistry = new HashMap<>();

		if (processors != null) {
			for (Processor<?, ?> processor : processors) {
				if (processor != null) {
					Map<TypeVariable<?>, Type> types = TypeUtils.getTypeArguments(processor.getClass(), Processor.class);
					Class<?> sourceType = validateProcessorTypeParameter(processor, types.get(Processor.class.getTypeParameters()[0]));
					Class<?> targetType = validateProcessorTypeParameter(processor, types.get(Processor.class.getTypeParameters()[1]));

					@SuppressWarnings("unchecked")
					Processor<SourceType, TargetType> toTargetConverter = (Processor<SourceType, TargetType>) processor;
					Processor<SourceType, TargetType> function = (source, conversionService) -> toTargetConverter
							.process(source, conversionService);
					
					addFunction(sourceType, targetType, processorRegistry, function);
					
					if (processor instanceof BiProcessor) {
						@SuppressWarnings("unchecked")
						BiProcessor<SourceType, TargetType> toSourceProcessor = (BiProcessor<SourceType, TargetType>) processor;
						Processor<TargetType, SourceType> function2 = (target, processingService) -> toSourceProcessor.reverse(target, processingService);
						
						addFunction(targetType, sourceType, processorRegistry, function2);
					}
				}
			}
		}

		return new ProcessingServiceImpl(processorRegistry);
	}

	private static Class<?> validateProcessorTypeParameter(Processor<?, ?> converter, Type typeParameter) {
		if (typeParameter instanceof Class) {
			return (Class<?>) typeParameter;
		} else if (typeParameter instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) typeParameter).getRawType();
		} else {
			throw new ProcessorTypeException(converter);
		}
	}
	
	private static <SourceType, TargetType> void addFunction(Class<?> sourceType, Class<?> targetType, 
			Map<Class<?>, Map<Class<?>, Processor<?, ?>>> processingRegistry, Processor<SourceType, TargetType> function) {
		if (!processingRegistry.containsKey(targetType)) {
			processingRegistry.put(targetType, new HashMap<>());
		} else if (processingRegistry.get(targetType).containsKey(sourceType)) {
			throw new AmbiguousProcessorException(sourceType, targetType);
		}
		
		processingRegistry.get(targetType).put(sourceType, function);
	}
}
