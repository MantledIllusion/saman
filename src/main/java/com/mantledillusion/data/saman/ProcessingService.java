package com.mantledillusion.data.saman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mantledillusion.data.saman.context.ProcessingContext;

/**
 * Base for a service holding a pool of {@link Processor}s that it can
 * delegate specific processings to.
 */
public interface ProcessingService {

	public interface Processor<SourceType, TargetType> {
	
		TargetType process(SourceType source, ProcessingContext context) throws Exception;
	}

	public interface BiProcessor<SourceType, TargetType> extends Processor<SourceType, TargetType> {
	
		SourceType reverse(TargetType target, ProcessingContext context) throws Exception;
	}

	// ############################################################################################################
	// ############################################# SINGLE INSTANCES #############################################
	// ############################################################################################################

	/**
	 * Processes the given source.
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <TargetType>
	 *            The target type to process to
	 * @param source
	 *            The source object to process; might be null, although in this case
	 *            no {@link Processor} is called and null is returned.
	 * @param targetType
	 *            The target type to process to; might <b>not</b> be null.
	 * @return The processed target object, always null if the source is null and
	 *         possibly null if the {@link Processor}'s result is null
	 */
	@SuppressWarnings("unchecked")
	default <SourceType, TargetType> TargetType process(SourceType source, Class<TargetType> targetType) {
		return source == null ? null
				: processStrictly((Class<? super SourceType>) source.getClass(), source, targetType);
	}

	/**
	 * Processes a simulated null value of the given source type.
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <TargetType>
	 *            The target type to process to
	 * @param sourceType
	 *            The source type to simulate a null value for; might <b>not</b> be
	 *            null.
	 * @param targetType
	 *            The target type to process to; might <b>not</b> be null.
	 * @return The processed target object, might be null if the {@link Processor}'s result
	 *         is null
	 */
	default <SourceType, TargetType> TargetType processNull(Class<SourceType> sourceType,
			Class<TargetType> targetType) {
		return processStrictly(sourceType, null, targetType);
	}

	/**
	 * Processes the given source ensuring that a {@link Processor} of the given
	 * source/target type is called even if the source object is null.
	 * <p>
	 * This method can be used to force the use of a super source type {@link Processor}.
	 * For example, if B extends A and C is a target type, calling this method with
	 * an B instance, A as source and C as target type will force the use of an
	 * A-&gt;C {@link Processor}, even if an B-&gt;C {@link Processor} is available.
	 * <p>
	 * Functionally, this method basically is the combination of
	 * {@link #process(Object, Class)} and {@link #processNull(Class, Class)}.
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <TargetType>
	 *            The target type to process to
	 * @param sourceType
	 *            The source type to process; might <b>not</b> be null.
	 * @param source
	 *            The source object to process; might be null.
	 * @param targetType
	 *            The target type to process to; might <b>not</b> be null.
	 * @return The processed target object, might be null if the {@link Processor}'s result
	 *         is null
	 */
	<SourceType, TargetType> TargetType processStrictly(Class<SourceType> sourceType, SourceType source,
			Class<TargetType> targetType);

	// ############################################################################################################
	// ############################################### COLLECTIONS ################################################
	// ############################################################################################################

	/**
	 * Processes the given list of source objects to a new list of target objects.
	 * <p>
	 * Uses {@link #process(Object, Class)}.
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <TargetType>
	 *            The target type to process to
	 * @param source
	 *            The list of source objects to process; might be null, although in
	 *            this case null is returned.
	 * @param targetType
	 *            The type to process all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The processed target objects, might be null if the given source
	 *         object list was null
	 */
	default <SourceType, TargetType> List<TargetType> processList(List<SourceType> source,
			Class<TargetType> targetType) {
		return source == null ? null : processInto(source, new ArrayList<>(), targetType);
	}

	/**
	 * Processes the given set of source objects to a new set of target objects.
	 * <p>
	 * Uses {@link #process(Object, Class)}.
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <TargetType>
	 *            The target type to process to
	 * @param source
	 *            The set of source objects to process; might be null, although in
	 *            this case null is returned.
	 * @param targetType
	 *            The type to process all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The processed target objects, might be null if the given source
	 *         object set was null
	 */
	default <SourceType, TargetType> Set<TargetType> processSet(Set<SourceType> source,
			Class<TargetType> targetType) {
		return source == null ? null : processInto(source, new HashSet<>(), targetType);
	}

	/**
	 * Processes the given collection of source objects into the given collection of
	 * target objects.
	 * <p>
	 * Uses {@link #process(Object, Class)}.
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <SourceCollectionType>
	 *            The {@link Collection} type of the source elements
	 * @param <TargetType>
	 *            The target type to process to
	 * @param <TargetCollectionType>
	 *            The {@link Collection} type of the target elements
	 * @param source
	 *            The collection of source objects to process; might be null.
	 * @param target
	 *            The target collection of objects to process into; might be null
	 *            although in this case null is returned.
	 * @param targetType
	 *            The type to process all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The given target collection, might be null if the given target
	 *         collection was null
	 */
	<SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processInto(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType);

	/**
	 * Processes the given list of source objects to a new list of target objects
	 * ensuring that a {@link Processor} of the given source/target type is called even if
	 * the source element is null.
	 * <p>
	 * Uses {@link #processStrictly(Class, Object, Class)};
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <TargetType>
	 *            The target type to process to
	 * @param sourceType
	 *            The source type to process; might <b>not</b> be null.
	 * @param source
	 *            The list of source objects to process; might be null, although in
	 *            this case null is returned.
	 * @param targetType
	 *            The type to process all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The processed target objects, might be null if the given source
	 *         object list was null
	 */
	default <SourceType, TargetType> List<TargetType> processListStrictly(Class<SourceType> sourceType,
			List<SourceType> source, Class<TargetType> targetType) {
		return source == null ? null : processStrictlyInto(sourceType, source, new ArrayList<>(), targetType);
	}

	/**
	 * Processes the given set of source objects to a new set of target objects
	 * ensuring that a {@link Processor} of the given source/target type is called even if
	 * the source element is null.
	 * <p>
	 * Uses {@link #processStrictly(Class, Object, Class)};
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <TargetType>
	 *            The target type to process to
	 * @param sourceType
	 *            The source type to process; might <b>not</b> be null.
	 * @param source
	 *            The set of source objects to process; might be null, although in
	 *            this case null is returned.
	 * @param targetType
	 *            The type to process all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The processed target objects, might be null if the given source
	 *         object set was null
	 */
	default <SourceType, TargetType> Set<TargetType> processSetStrictly(Class<SourceType> sourceType,
			Set<SourceType> source, Class<TargetType> targetType) {
		return source == null ? null : processStrictlyInto(sourceType, source, new HashSet<>(), targetType);
	}

	/**
	 * Processes the given collection of source objects into the given collection of
	 * target objects ensuring that a {@link Processor} of the given source/target type is
	 * called even if the source element is null.
	 * <p>
	 * Uses {@link #processStrictly(Class, Object, Class)};
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <SourceCollectionType>
	 *            The {@link Collection} type of the source elements
	 * @param <TargetType>
	 *            The target type to process to
	 * @param <TargetCollectionType>
	 *            The {@link Collection} type of the target elements
	 * @param sourceType
	 *            The source type to process; might <b>not</b> be null.
	 * @param source
	 *            The collection of source objects to process; might be null.
	 * @param target
	 *            The target collection of objects to process into; might be null
	 *            although in this case null is returned.
	 * @param targetType
	 *            The type to process all of the source objects to; might <b>not</b>
	 *            be null.
	 * @return The given target collection, might be null if the given target
	 *         collection was null
	 */
	<SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processStrictlyInto(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType);

	// ############################################################################################################
	// ################################################### MAP ####################################################
	// ############################################################################################################

	/**
	 * Processes the given list of source objects to a new map of target objects.
	 * <p>
	 * Uses {@link #process(Object, Class)}.
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
	 *            The map of source objects to process; might be null, although in
	 *            this case null is returned.
	 * @param targetTypeKey
	 *            The type to process all of the source key objects to; might
	 *            <b>not</b> be null.
	 * @param targetTypeValue
	 *            The type to process all of the source value objects to; might
	 *            <b>not</b> be null.
	 * @return The processed target objects, might be null if the given source
	 *         object set was null
	 */
	default <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processMap(
			Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
			Class<TargetTypeValue> targetTypeValue) {
		return source == null ? null : processInto(source, new HashMap<>(), targetTypeKey, targetTypeValue);
	}

	/**
	 * Processes the given map of source objects into the given collection of target
	 * objects.
	 * <p>
	 * Uses {@link #process(Object, Class)}.
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
	 *            The map of source objects to process; might be null, although in
	 *            this case null is returned.
	 * @param target
	 *            The target map of objects to process into; might be null although
	 *            in this case null is returned.
	 * @param targetTypeKey
	 *            The type to process all of the source key objects to; might
	 *            <b>not</b> be null.
	 * @param targetTypeValue
	 *            The type to process all of the source value objects to; might
	 *            <b>not</b> be null.
	 * @return The processed target objects, might be null if the given source
	 *         object set was null
	 */
	<SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processInto(
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue);

	/**
	 * Processes the given map of source objects to a new map of target objects
	 * ensuring that a {@link Processor} of the given source/target type is called even if
	 * the source element is null.
	 * <p>
	 * Uses {@link #processStrictly(Class, Object, Class)};
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
	 *            The source key type to process; might <b>not</b> be null.
	 * @param sourceTypeValue
	 *            The source value type to process; might <b>not</b> be null.
	 * @param source
	 *            The map of source objects to process; might be null, although in
	 *            this case null is returned.
	 * @param targetTypeKey
	 *            The type to process all of the source key objects to; might
	 *            <b>not</b> be null.
	 * @param targetTypeValue
	 *            The type to process all of the source value objects to; might
	 *            <b>not</b> be null.
	 * @return The processed target objects, might be null if the given source
	 *         object set was null
	 */
	default <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processMapStrictly(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
			Class<TargetTypeValue> targetTypeValue) {
		return source == null ? null
				: processStrictlyInto(sourceTypeKey, sourceTypeValue, source, new HashMap<>(), targetTypeKey,
						targetTypeValue);
	}

	/**
	 * Processes the given map of source objects into the given collection of target
	 * objects ensuring that a {@link Processor} of the given source/target type is called
	 * even if the source element is null.
	 * <p>
	 * Uses {@link #processStrictly(Class, Object, Class)};
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
	 *            The source key type to process; might <b>not</b> be null.
	 * @param sourceTypeValue
	 *            The source value type to process; might <b>not</b> be null.
	 * @param source
	 *            The map of source objects to process; might be null, although in
	 *            this case null is returned.
	 * @param target
	 *            The target map of objects to process into; might be null although
	 *            in this case null is returned.
	 * @param targetTypeKey
	 *            The type to process all of the source key objects to; might
	 *            <b>not</b> be null.
	 * @param targetTypeValue
	 *            The type to process all of the source value objects to; might
	 *            <b>not</b> be null.
	 * @return The processed target objects, might be null if the given source
	 *         object set was null
	 */
	<SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processStrictlyInto(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue);

	// ############################################################################################################
	// ################################################# SPECIAL ##################################################
	// ############################################################################################################

	/**
	 * Processes the given enumerated source object using its name.
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <TargetType>
	 *            The target type to process to
	 * @param source
	 *            The source object to process; might be null, although in this case
	 *            null is returned.
	 * @param targetType
	 *            The enumerated target type to process to; might <b>not</b> be
	 *            null.
	 * @return The processed target object, always null if the source is null, never
	 *         null otherwise
	 */
	<SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processNamed(
			SourceType source, Class<TargetType> targetType);

	/**
	 * Processes the given enumerated source object using its ordinal.
	 * 
	 * @param <SourceType>
	 *            The source type to process from
	 * @param <TargetType>
	 *            The target type to process to
	 * @param source
	 *            The source object to process; might be null, although in this case
	 *            null is returned.
	 * @param targetType
	 *            The enumerated target type to process to; might <b>not</b> be
	 *            null.
	 * @return The processed target object, always null if the source is null, never
	 *         null otherwise
	 */
	<SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processOrdinal(
			SourceType source, Class<TargetType> targetType);
}
