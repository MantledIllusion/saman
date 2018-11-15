package com.mantledillusion.data.saman;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.data.saman.ProcessingService.Processor;
import com.mantledillusion.data.saman.exception.AmbiguousProcessorException;
import com.mantledillusion.data.saman.exception.NoProcessorException;
import com.mantledillusion.data.saman.exception.ProcessingException;
import com.mantledillusion.data.saman.exception.ProcessorTypeException;

/**
 * A {@link Map} style registry for {@link Processor}s.
 */
public class ProcessorRegistry {

	private final Map<Class<?>, Map<Class<?>, ProcessingService.Processor<?, ?>>> processorRegistry;

	private ProcessorRegistry(Map<Class<?>, Map<Class<?>, ProcessingService.Processor<?, ?>>> processorRegistry) {
		this.processorRegistry = processorRegistry;
	}

	/**
	 * Identifies a suitable {@link Processor} that is able to process from the
	 * given source to target type.
	 * <p>
	 * In search for a {@link Processor}, the source type's super types will be
	 * iterated through until a processor is found that can process into the target
	 * type.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param sourceType
	 *            The source type to process; might <b>not</b> be null.
	 * @param targetType
	 *            The target type to process to; might <b>not</b> be null.
	 * @return A suitable {@link Processor}, never null
	 * @throws NoProcessorException
	 *             If there wasn't a {@link Processor} to convert from any of the
	 *             source type's super types to the target type.
	 */
	@SuppressWarnings("unchecked")
	public <SourceType, TargetType> Processor<SourceType, TargetType> identifyProcessor(Class<SourceType> sourceType,
			Class<TargetType> targetType) throws NoProcessorException {
		Class<? super SourceType> workType = sourceType;
		if (this.processorRegistry.containsKey(targetType)) {
			Map<Class<?>, ProcessingService.Processor<?, ?>> targetTypeProcessors = this.processorRegistry
					.get(targetType);
			do {
				if (targetTypeProcessors.containsKey(workType)) {
					return (ProcessingService.Processor<SourceType, TargetType>) targetTypeProcessors.get(workType);
				}
				workType = workType.getSuperclass();
			} while (workType != Object.class);
		}

		throw new NoProcessorException(sourceType, targetType);
	}

	/**
	 * Identifies a suitable {@link Processor} that is able to process from the
	 * given source to target {@link Enum}.
	 * <p>
	 * If a {@link Processor} exists that is able to convert from the given specific
	 * source to target {@link Enum}, it is used directly.
	 * <p>
	 * Otherwise it is attempted to create a {@link Processor} that works by using
	 * the {@link Enum}'s names; this requires the names of {@link Enum} values in
	 * both types to be equal.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param sourceType
	 *            The source type to process; might <b>not</b> be null.
	 * @param targetType
	 *            The target type to process to; might <b>not</b> be null.
	 * @return A suitable {@link Processor}, never null
	 * @throws ProcessingException
	 *             If there was no {@link Processor} pre registered and creating a
	 *             simple one that uses names was not possible.
	 */
	@SuppressWarnings("unchecked")
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> Processor<SourceType, TargetType> identifyNamedProcessor(
			Class<SourceType> sourceType, Class<TargetType> targetType) {
		if (!this.processorRegistry.containsKey(sourceType)) {
			this.processorRegistry.put(sourceType, new HashMap<>());
		}
		ProcessingService.Processor<SourceType, TargetType> processor;
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

			processor = (sourceValue, processingService) -> sourceValue == null ? null
					: Enum.valueOf(targetType, sourceValue.name());
			this.processorRegistry.get(sourceType).put(targetType, processor);
		} else {
			processor = (ProcessingService.Processor<SourceType, TargetType>) this.processorRegistry.get(sourceType)
					.get(targetType);
		}
		return processor;
	}

	/**
	 * Identifies a suitable {@link Processor} that is able to process from the
	 * given source to target {@link Enum}.
	 * <p>
	 * If a {@link Processor} exists that is able to convert from the given specific
	 * source to target {@link Enum}, it is used directly.
	 * <p>
	 * Otherwise it is attempted to create a {@link Processor} that works by using
	 * the {@link Enum}'s ordinals; this requires the amount of {@link Enum} values
	 * in both types to be equal.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param sourceType
	 *            The source type to process; might <b>not</b> be null.
	 * @param targetType
	 *            The target type to process to; might <b>not</b> be null.
	 * @return A suitable {@link Processor}, never null
	 * @throws ProcessingException
	 *             If there was no {@link Processor} pre registered and creating a
	 *             simple one that uses orginals was not possible.
	 */
	@SuppressWarnings("unchecked")
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> Processor<SourceType, TargetType> identifyOrdinalProcessor(
			Class<SourceType> sourceType, Class<TargetType> targetType) throws ProcessingException {
		if (!this.processorRegistry.containsKey(targetType)) {
			this.processorRegistry.put(targetType, new HashMap<>());
		}
		ProcessingService.Processor<SourceType, TargetType> processor;
		if (!this.processorRegistry.get(targetType).containsKey(sourceType)) {
			if (sourceType.getEnumConstants().length != targetType.getEnumConstants().length) {
				throw new ProcessingException("The type '" + sourceType.getSimpleName() + "' cannot be mapped to '"
						+ targetType.getSimpleName()
						+ "' by ordinal; the amount of enum values are differing between source/target enum type: ("
						+ sourceType.getEnumConstants().length + "|" + targetType.getEnumConstants().length + ").");
			}

			processor = (sourceValue, processingService) -> sourceValue == null ? null
					: targetType.getEnumConstants()[sourceValue.ordinal()];
			this.processorRegistry.get(targetType).put(sourceType, processor);
		} else {
			processor = (ProcessingService.Processor<SourceType, TargetType>) this.processorRegistry.get(targetType)
					.get(sourceType);
		}
		return processor;
	}

	// ############################################################################################################
	// ############################################# SERVICE CREATION #############################################
	// ############################################################################################################

	/**
	 * Factory method.
	 * <p>
	 * Creates a new {@link ProcessorRegistry} of the given
	 * {@link ProcessingService.Processor}s.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param processors
	 *            The {@link ProcessingService.Processor}s to build the
	 *            {@link ProcessorRegistry} from; might be empty or contain nulls.
	 * @return A new {@link ProcessorRegistry} instance, never null
	 */
	public static <SourceType, TargetType> ProcessorRegistry of(ProcessingService.Processor<?, ?>... processors) {
		return of(Arrays.asList(processors));
	}

	/**
	 * Factory method.
	 * <p>
	 * Creates a new {@link ProcessorRegistry} of the given
	 * {@link ProcessingService.Processor}s.
	 * 
	 * @param <SourceType>
	 *            The source type to convert from
	 * @param <TargetType>
	 *            The target type to convert to
	 * @param processors
	 *            The {@link ProcessingService.Processor}s to build the
	 *            {@link ProcessorRegistry} from; might be null, empty or contain
	 *            nulls.
	 * @return A new {@link ProcessorRegistry} instance, never null
	 */
	public static <SourceType, TargetType> ProcessorRegistry of(
			Collection<ProcessingService.Processor<?, ?>> processors) {
		Map<Class<?>, Map<Class<?>, ProcessingService.Processor<?, ?>>> processorRegistry = new HashMap<>();

		if (processors != null) {
			for (ProcessingService.Processor<?, ?> processor : processors) {
				if (processor != null) {
					Map<TypeVariable<?>, Type> types = TypeUtils.getTypeArguments(processor.getClass(),
							ProcessingService.Processor.class);
					Class<?> sourceType = validateProcessorTypeParameter(processor,
							types.get(ProcessingService.Processor.class.getTypeParameters()[0]));
					Class<?> targetType = validateProcessorTypeParameter(processor,
							types.get(ProcessingService.Processor.class.getTypeParameters()[1]));

					@SuppressWarnings("unchecked")
					ProcessingService.Processor<SourceType, TargetType> toTargetConverter = (ProcessingService.Processor<SourceType, TargetType>) processor;
					ProcessingService.Processor<SourceType, TargetType> function = (source,
							conversionService) -> toTargetConverter.process(source, conversionService);

					addFunction(sourceType, targetType, processorRegistry, function);

					if (processor instanceof ProcessingService.BiProcessor) {
						@SuppressWarnings("unchecked")
						ProcessingService.BiProcessor<SourceType, TargetType> toSourceProcessor = (ProcessingService.BiProcessor<SourceType, TargetType>) processor;
						ProcessingService.Processor<TargetType, SourceType> function2 = (target,
								processingService) -> toSourceProcessor.reverse(target, processingService);

						addFunction(targetType, sourceType, processorRegistry, function2);
					}
				}
			}
		}

		return new ProcessorRegistry(processorRegistry);
	}

	private static Class<?> validateProcessorTypeParameter(ProcessingService.Processor<?, ?> converter,
			Type typeParameter) {
		if (typeParameter instanceof Class) {
			return (Class<?>) typeParameter;
		} else if (typeParameter instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) typeParameter).getRawType();
		} else {
			throw new ProcessorTypeException(converter);
		}
	}

	private static <SourceType, TargetType> void addFunction(Class<?> sourceType, Class<?> targetType,
			Map<Class<?>, Map<Class<?>, ProcessingService.Processor<?, ?>>> processingRegistry,
			ProcessingService.Processor<SourceType, TargetType> function) {
		if (!processingRegistry.containsKey(targetType)) {
			processingRegistry.put(targetType, new HashMap<>());
		} else if (processingRegistry.get(targetType).containsKey(sourceType)) {
			throw new AmbiguousProcessorException(sourceType, targetType);
		}

		processingRegistry.get(targetType).put(sourceType, function);
	}
}
