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
	
	private ProcessingServiceFactory() {}
	
	// ############################################################################################################
	// ############################################# SERVICE CREATION #############################################
	// ############################################################################################################

	/**
	 * Factory method.
	 * <p>
	 * Creates a new {@link ProcessingServiceImpl} of the given {@link ProcessingService.Processor}s.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param processors
	 *            The {@link ProcessingService.Processor}s to build the {@link ProcessingServiceImpl}
	 *            from; might be empty or contain nulls.
	 * @return A new {@link ProcessingServiceImpl} instance, never null
	 */
	public static <SourceType, TargetType> ProcessingServiceImpl of(ProcessingService.Processor<?, ?>... processors) {
		return of(Arrays.asList(processors));
	}

	/**
	 * Factory method.
	 * <p>
	 * Creates a new {@link ProcessingServiceImpl} of the given {@link ProcessingService.Processor}s.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param processors
	 *            The {@link ProcessingService.Processor}s to build the {@link ProcessingServiceImpl}
	 *            from; might be null, empty or contain nulls.
	 * @return A new {@link ProcessingServiceImpl} instance, never null
	 */
	public static <SourceType, TargetType> ProcessingServiceImpl of(Collection<ProcessingService.Processor<?, ?>> processors) {
		Map<Class<?>, Map<Class<?>, ProcessingService.Processor<?, ?>>> processorRegistry = new HashMap<>();

		if (processors != null) {
			for (ProcessingService.Processor<?, ?> processor : processors) {
				if (processor != null) {
					Map<TypeVariable<?>, Type> types = TypeUtils.getTypeArguments(processor.getClass(), ProcessingService.Processor.class);
					Class<?> sourceType = validateProcessorTypeParameter(processor, types.get(ProcessingService.Processor.class.getTypeParameters()[0]));
					Class<?> targetType = validateProcessorTypeParameter(processor, types.get(ProcessingService.Processor.class.getTypeParameters()[1]));

					@SuppressWarnings("unchecked")
					ProcessingService.Processor<SourceType, TargetType> toTargetConverter = (ProcessingService.Processor<SourceType, TargetType>) processor;
					ProcessingService.Processor<SourceType, TargetType> function = (source, conversionService) -> toTargetConverter
							.process(source, conversionService);
					
					addFunction(sourceType, targetType, processorRegistry, function);
					
					if (processor instanceof ProcessingService.BiProcessor) {
						@SuppressWarnings("unchecked")
						ProcessingService.BiProcessor<SourceType, TargetType> toSourceProcessor = (ProcessingService.BiProcessor<SourceType, TargetType>) processor;
						ProcessingService.Processor<TargetType, SourceType> function2 = (target, processingService) -> toSourceProcessor.reverse(target, processingService);
						
						addFunction(targetType, sourceType, processorRegistry, function2);
					}
				}
			}
		}

		return new ProcessingServiceImpl(processorRegistry);
	}

	private static Class<?> validateProcessorTypeParameter(ProcessingService.Processor<?, ?> converter, Type typeParameter) {
		if (typeParameter instanceof Class) {
			return (Class<?>) typeParameter;
		} else if (typeParameter instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) typeParameter).getRawType();
		} else {
			throw new ProcessorTypeException(converter);
		}
	}
	
	private static <SourceType, TargetType> void addFunction(Class<?> sourceType, Class<?> targetType, 
			Map<Class<?>, Map<Class<?>, ProcessingService.Processor<?, ?>>> processingRegistry, ProcessingService.Processor<SourceType, TargetType> function) {
		if (!processingRegistry.containsKey(targetType)) {
			processingRegistry.put(targetType, new HashMap<>());
		} else if (processingRegistry.get(targetType).containsKey(sourceType)) {
			throw new AmbiguousProcessorException(sourceType, targetType);
		}
		
		processingRegistry.get(targetType).put(sourceType, function);
	}
}
