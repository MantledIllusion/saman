package com.mantledillusion.data.saman;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import com.mantledillusion.data.saman.exception.ProcessingException;
import com.mantledillusion.data.saman.exception.ProcessorException;

/**
 * Reference implementation of {@link ProcessingService}.
 */
public class DefaultProcessingService implements ProcessingService {

	private final ProcessorRegistry processorRegistry;
	private boolean wrapRuntimeExceptions = true;

	public DefaultProcessingService(ProcessorRegistry processorRegistry) {
		if (processorRegistry == null) {
			throw new IllegalArgumentException("Cannot create a processing service with a null processor registry");
		}
		this.processorRegistry = processorRegistry;
	}

	private <SourceType, TargetType> TargetType execute(ProcessingService.Processor<SourceType, TargetType> processor,
			SourceType source, ProcessingContext context) {
		try {
			return processor.process(source, new ProcessingDelegate(this, context));
		} catch (RuntimeException e) {
			if (this.wrapRuntimeExceptions) {
				throw new ProcessorException(e);
			}
			throw e;
		} catch (Exception e) {
			throw new ProcessorException(e);
		}
	}

	// ############################################################################################################
	// ############################################# SINGLE INSTANCES #############################################
	// ############################################################################################################

	/**
	 * Returns whether {@link RuntimeException}s are wrapped into
	 * {@link ProcessorException}s if thrown during
	 * {@link Processor#process(Object, ProcessingDelegate)}.
	 * 
	 * @return True if {@link RuntimeException}s should be wrapped, false otherwise
	 */
	public boolean doWrapRuntimeExceptions() {
		return wrapRuntimeExceptions;
	}

	/**
	 * Sets whether {@link RuntimeException}s should be wrapped into
	 * {@link ProcessorException}s if thrown during
	 * {@link Processor#process(Object, ProcessingDelegate)}.
	 * 
	 * @param wrapRuntimeExceptions
	 *            True if {@link RuntimeException}s should be wrapped, false
	 *            otherwise.
	 */
	public void setWrapRuntimeExceptions(boolean wrapRuntimeExceptions) {
		this.wrapRuntimeExceptions = wrapRuntimeExceptions;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <SourceType, TargetType> TargetType processStrictly(Class<SourceType> sourceType, SourceType source,
			Class<TargetType> targetType, ProcessingContext context) {
		if (sourceType == null) {
			throw new ProcessingException("Cannot process using a null source type.");
		} else if (targetType == null) {
			throw new ProcessingException("Cannot process using a null target type.");
		}

		if (sourceType.equals(targetType)) {
			return (TargetType) source;
		}

		return execute(this.processorRegistry.identifyProcessor(sourceType, targetType), source, context);
	}

	// ############################################################################################################
	// ############################################### COLLECTIONS ################################################
	// ############################################################################################################

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processInto(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType,
			ProcessingContext context) {
		return processInto(source, target, context, (sourceElement, ctx) -> process(sourceElement, targetType, ctx));
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processStrictlyInto(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType, ProcessingContext context) {
		return processInto(source, target, context, (sourceElement, ctx) -> processStrictly(sourceType, sourceElement, targetType, ctx));
	}

	private <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processInto(
			SourceCollectionType source, TargetCollectionType target, ProcessingContext context,
			BiFunction<SourceType, ProcessingContext, TargetType> callback) {
		if (source != null && target != null) {
			context = new ProcessingContext(context);
			for (SourceType sourceElement : source) {
				target.add(callback.apply(sourceElement, context));
			}
		}
		return target;
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processIntoAligning(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType,
			BiPredicate<SourceType, TargetType> equalityPredicate, ProcessingContext context) {
		return processIntoAligning(source, target, equalityPredicate, context, (sourceElement, ctx) -> process(sourceElement, targetType, ctx));
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processStrictlyIntoAligning(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType, BiPredicate<SourceType, TargetType> equalityPredicate,
			ProcessingContext context) {
		return processIntoAligning(source, target, equalityPredicate, context, (sourceElement, ctx) -> processStrictly(sourceType, sourceElement, targetType, ctx));
	}

	private <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processIntoAligning(
			SourceCollectionType source, TargetCollectionType target,
			BiPredicate<SourceType, TargetType> equalityPredicate, ProcessingContext context,
			BiFunction<SourceType, ProcessingContext, TargetType> callback) {
		if (equalityPredicate == null) {
			throw new ProcessingException("Cannot align between two collections with a null equality predicate");
		}

		if (target != null) {
			if (source != null) {
				Map<TargetType, SourceType> alignedElements = new IdentityHashMap<>();
				List<SourceType> newElements = new ArrayList<>();
				for (SourceType sourceElement: source) {
					List<TargetType> possibleTargetElements = target.stream().
							filter(targetElement -> equalityPredicate.test(sourceElement, targetElement)).
							collect(Collectors.toList());
					if (possibleTargetElements.isEmpty()) {
						newElements.add(sourceElement);
					} else if (possibleTargetElements.size() == 1) {
						alignedElements.put(possibleTargetElements.iterator().next(), sourceElement);
					} else {
						throw new ProcessingException("The source element '" + sourceElement + "' has " +
								possibleTargetElements.size() + " possible target elements to align with; expecting one or none.");
					}
				}

				context = new ProcessingContext(context);
				Iterator<TargetType> targetElementIterator = target.iterator();
				while (targetElementIterator.hasNext()) {
					TargetType targetElement = targetElementIterator.next();
					if (alignedElements.containsKey(targetElement)) {
						TargetType processedTargetElement = callback.apply(alignedElements.get(targetElement),
								new ProcessingContext(context).set(targetElement));
						if (targetElement != processedTargetElement) {
							throw new ProcessingException("Unable to align collection; processor was expected to map " +
									"onto and return target element instance '" + targetElement + "' provided by in the " +
									ProcessingContext.class.getSimpleName() + ", but it did not.");
						}
					} else {
						targetElementIterator.remove();
					}
				}
				for (SourceType sourceElement: newElements) {
					target.add(callback.apply(sourceElement, context));
				}
			} else {
				target.clear();
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
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue, ProcessingContext context) {
		if (source != null && target != null) {
			context = new ProcessingContext(context);
			for (Entry<SourceTypeKey, SourceTypeValue> entry : source.entrySet()) {
				target.put(process(entry.getKey(), targetTypeKey, context),
						process(entry.getValue(), targetTypeValue, context));
			}
		}
		return target;
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processStrictlyInto(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue, ProcessingContext context) {
		if (source != null && target != null) {
			context = new ProcessingContext(context);
			for (Entry<SourceTypeKey, SourceTypeValue> entry : source.entrySet()) {
				target.put(processStrictly(sourceTypeKey, entry.getKey(), targetTypeKey, context),
						processStrictly(sourceTypeValue, entry.getValue(), targetTypeValue, context));
			}
		}
		return target;
	}

	// ############################################################################################################
	// ################################################# SPECIAL ##################################################
	// ############################################################################################################

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processNamed(
			SourceType source, Class<TargetType> targetType, ProcessingContext context) {
		if (targetType == null) {
			throw new ProcessingException("Cannot process using a null target type.");
		}

		if (source == null) {
			return null;
		} else {
			Class<SourceType> sourceType = source.getDeclaringClass();
			return execute(this.processorRegistry.identifyNamedProcessor(sourceType, targetType), source, context);
		}
	}

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processOrdinal(
			SourceType source, Class<TargetType> targetType, ProcessingContext context) {
		if (targetType == null) {
			throw new ProcessingException("Cannot process using a null target type.");
		}

		if (source == null) {
			return null;
		} else {
			Class<SourceType> sourceType = source.getDeclaringClass();
			return execute(this.processorRegistry.identifyOrdinalProcessor(sourceType, targetType), source, context);
		}
	}
}
