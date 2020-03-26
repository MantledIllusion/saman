package com.mantledillusion.data.saman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Base for a service holding a pool of {@link Processor}s that it can delegate
 * specific processings to.
 */
public interface ProcessingService {

    /**
     * Processes the given source.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The source object to process; might be null, although in this case
     *                     no {@link Processor} is called and null is returned.
     * @param targetType   The target type to process to; might <b>not</b> be null.
     * @return The processed target object, always null if the source is null and
     * possibly null if the {@link Processor}'s result is null
     */
    @SuppressWarnings("unchecked")
    default <SourceType, TargetType> TargetType process(SourceType source,
                                                        Class<TargetType> targetType) {
        return source == null ? null : processStrictly((Class<? super SourceType>) source.getClass(), source, targetType, null);
    }

    /**
     * Processes the given source.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The source object to process; might be null, although in this case
     *                     no {@link Processor} is called and null is returned.
     * @param targetType   The target type to process to; might <b>not</b> be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target object, always null if the source is null and
     * possibly null if the {@link Processor}'s result is null
     */
    @SuppressWarnings("unchecked")
    default <SourceType, TargetType> TargetType process(SourceType source,
                                                        Class<TargetType> targetType,
                                                        ProcessingContext context) {
        return source == null ? null : processStrictly((Class<? super SourceType>) source.getClass(), source, targetType, context);
    }

    // ############################################################################################################
    // ############################################# SINGLE INSTANCES #############################################
    // ############################################################################################################

    /**
     * Processes a simulated null value of the given source type.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to simulate a null value for; might <b>not</b> be
     *                     null.
     * @param targetType   The target type to process to; might <b>not</b> be null.
     * @return The processed target object, might be null if the {@link Processor}'s
     * result is null
     */
    default <SourceType, TargetType> TargetType processNull(Class<SourceType> sourceType,
                                                            Class<TargetType> targetType) {
        return processStrictly(sourceType, null, targetType, null);
    }

    /**
     * Processes a simulated null value of the given source type.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to simulate a null value for; might <b>not</b> be
     *                     null.
     * @param targetType   The target type to process to; might <b>not</b> be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target object, might be null if the {@link Processor}'s
     * result is null
     */
    default <SourceType, TargetType> TargetType processNull(Class<SourceType> sourceType,
                                                            Class<TargetType> targetType,
                                                            ProcessingContext context) {
        return processStrictly(sourceType, null, targetType, context);
    }

    /**
     * Processes the given source ensuring that a {@link Processor} of the given
     * source/target type is called even if the source object is null.
     * <p>
     * This method can be used to force the use of a super source type
     * {@link Processor}. For example, if B extends A and C is a target type,
     * calling this method with an B instance, A as source and C as target type will
     * force the use of an A-&gt;C {@link Processor}, even if an B-&gt;C
     * {@link Processor} is available.
     * <p>
     * Functionally, this method basically is the combination of
     * {@link #process(Object, Class)} and
     * {@link #processNull(Class, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The source object to process; might be null.
     * @param targetType   The target type to process to; might <b>not</b> be null.
     * @return The processed target object, might be null if the {@link Processor}'s
     * result is null
     */
    default <SourceType, TargetType> TargetType processStrictly(Class<SourceType> sourceType,
                                                                SourceType source,
                                                                Class<TargetType> targetType) {
        return processStrictly(sourceType, source, targetType, null);
    }

    /**
     * Processes the given source ensuring that a {@link Processor} of the given
     * source/target type is called even if the source object is null.
     * <p>
     * This method can be used to force the use of a super source type
     * {@link Processor}. For example, if B extends A and C is a target type,
     * calling this method with an B instance, A as source and C as target type will
     * force the use of an A-&gt;C {@link Processor}, even if an B-&gt;C
     * {@link Processor} is available.
     * <p>
     * Functionally, this method basically is the combination of
     * {@link #process(Object, Class)} and
     * {@link #processNull(Class, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The source object to process; might be null.
     * @param targetType   The target type to process to; might <b>not</b> be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target object, might be null if the {@link Processor}'s
     * result is null
     */
    <SourceType, TargetType> TargetType processStrictly(Class<SourceType> sourceType,
                                                        SourceType source,
                                                        Class<TargetType> targetType,
                                                        ProcessingContext context);

    /**
     * Processes the given list of source objects to a new list of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The list of source objects to process; might be null, although in
     *                     this case null is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @return The processed target objects, might be null if the given source
     * object list was null
     */
    default <SourceType, TargetType> List<TargetType> processList(List<SourceType> source,
                                                                  Class<TargetType> targetType) {
        return source == null ? null : processInto(source, new ArrayList<>(), targetType);
    }

    /**
     * Processes the given list of source objects to a new list of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The list of source objects to process; might be null, although in
     *                     this case null is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given source
     * object list was null
     */
    default <SourceType, TargetType> List<TargetType> processList(List<SourceType> source,
                                                                  Class<TargetType> targetType,
                                                                  ProcessingContext context) {
        return source == null ? null : processInto(source, new ArrayList<>(), targetType, context);
    }

    // ############################################################################################################
    // ############################################### COLLECTIONS ################################################
    // ############################################################################################################

    /**
     * Processes the given list of source objects to a new list of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The list of source objects to process; might be null.
     * @param target       The target list of objects to process into; might be null,
     *                     in this case a new list is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @return The processed target objects, might be null if the given target
     * object list was null
     */
    default <SourceType, TargetType> List<TargetType> processList(List<SourceType> source,
                                                                  List<TargetType> target,
                                                                  Class<TargetType> targetType) {
        return processInto(source, target == null ? new ArrayList<>() : target, targetType);
    }

    /**
     * Processes the given list of source objects to a new list of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The list of source objects to process; might be null.
     * @param target       The target list of objects to process into; might be null,
     *                     in this case a new list is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given target
     * object list was null
     */
    default <SourceType, TargetType> List<TargetType> processList(List<SourceType> source, List<TargetType> target,
                                                                  Class<TargetType> targetType, ProcessingContext context) {
        return processInto(source, target == null ? new ArrayList<>() : target, targetType, context);
    }

    /**
     * Processes the given list of source objects into the given list of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType>      The source type to process from
     * @param <TargetType>      The target type to process to
     * @param source            The list of source objects to process; might be null.
     * @param target            The target list of objects to process into; might be null
     *                          although in this case null is returned.
     * @param targetType        The type to process all of the source objects to; might <b>not</b>
     *                          be null.
     * @param equalityPredicate The {@link BiPredicate} to use for checking equality between source
     *                          and target objects, determining the latter to be reused; might
     *                          <b>not</b> be null.
     * @return The given target list, might be null if the given target
     * list was null
     */
    default <SourceType, TargetType> List<TargetType> processListAligning(List<SourceType> source,
                                                                          List<TargetType> target,
                                                                          Class<TargetType> targetType,
                                                                          BiPredicate<SourceType, TargetType> equalityPredicate) {
        return processIntoAligning(source, target == null ? new ArrayList<>() : target, targetType, equalityPredicate);
    }

    /**
     * Processes the given list of source objects into the given list of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType>      The source type to process from
     * @param <TargetType>      The target type to process to
     * @param source            The list of source objects to process; might be null.
     * @param target            The target list of objects to process into; might be null
     *                          although in this case null is returned.
     * @param targetType        The type to process all of the source objects to; might <b>not</b>
     *                          be null.
     * @param equalityPredicate The {@link BiPredicate} to use for checking equality between source
     *                          and target objects, determining the latter to be reused; might
     *                          <b>not</b> be null.
     * @param context           Predefined {@link ProcessingContext}; might be null.
     * @return The given target list, might be null if the given target
     * list was null
     */
    default <SourceType, TargetType> List<TargetType> processListAligning(List<SourceType> source,
                                                                          List<TargetType> target,
                                                                          Class<TargetType> targetType,
                                                                          BiPredicate<SourceType, TargetType> equalityPredicate,
                                                                          ProcessingContext context) {
        return processIntoAligning(source, target == null ? new ArrayList<>() : target, targetType, equalityPredicate, context);
    }

    /**
     * Processes the given set of source objects to a new set of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The set of source objects to process; might be null, although in
     *                     this case null is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSet(Set<SourceType> source,
                                                                Class<TargetType> targetType) {
        return source == null ? null : processInto(source, new HashSet<>(), targetType);
    }

    /**
     * Processes the given set of source objects to a new set of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The set of source objects to process; might be null, although in
     *                     this case null is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSet(Set<SourceType> source,
                                                                Class<TargetType> targetType,
                                                                ProcessingContext context) {
        return source == null ? null : processInto(source, new HashSet<>(), targetType, context);
    }

    /**
     * Processes the given set of source objects to a new set of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The list of source objects to process; might be null.
     * @param target       The target set of objects to process into; might be null,
     *                     in this case a new set is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @return The processed target objects, might be null if the given target
     * object set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSet(Set<SourceType> source,
                                                                Set<TargetType> target,
                                                                Class<TargetType> targetType) {
        return processInto(source, target == null ? new HashSet<>() : target, targetType);
    }

    /**
     * Processes the given set of source objects to a new set of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The list of source objects to process; might be null.
     * @param target       The target set of objects to process into; might be null,
     *                     in this case a new set is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given target
     * object set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSet(Set<SourceType> source,
                                                                Set<TargetType> target,
                                                                Class<TargetType> targetType,
                                                                ProcessingContext context) {
        return processInto(source, target == null ? new HashSet<>() : target, targetType, context);
    }

    /**
     * Processes the given set of source objects into the given set of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType>      The source type to process from
     * @param <TargetType>      The target type to process to
     * @param source            The list of source objects to process; might be null.
     * @param target            The target set of objects to process into; might be null
     *                          although in this case null is returned.
     * @param targetType        The type to process all of the source objects to; might <b>not</b>
     *                          be null.
     * @param equalityPredicate The {@link BiPredicate} to use for checking equality between source
     *                          and target objects, determining the latter to be reused; might
     *                          <b>not</b> be null.
     * @return The given target set, might be null if the given target
     * set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSetAligning(Set<SourceType> source,
                                                                        Set<TargetType> target,
                                                                        Class<TargetType> targetType,
                                                                        BiPredicate<SourceType, TargetType> equalityPredicate) {
        return processIntoAligning(source, target == null ? new HashSet<>() : target, targetType, equalityPredicate);
    }

    /**
     * Processes the given set of source objects into the given set of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType>      The source type to process from
     * @param <TargetType>      The target type to process to
     * @param source            The list of source objects to process; might be null.
     * @param target            The target set of objects to process into; might be null
     *                          although in this case null is returned.
     * @param targetType        The type to process all of the source objects to; might <b>not</b>
     *                          be null.
     * @param equalityPredicate The {@link BiPredicate} to use for checking equality between source
     *                          and target objects, determining the latter to be reused; might
     *                          <b>not</b> be null.
     * @param context           Predefined {@link ProcessingContext}; might be null.
     * @return The given target set, might be null if the given target
     * set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSetAligning(Set<SourceType> source,
                                                                        Set<TargetType> target,
                                                                        Class<TargetType> targetType,
                                                                        BiPredicate<SourceType, TargetType> equalityPredicate,
                                                                        ProcessingContext context) {
        return processIntoAligning(source, target == null ? new HashSet<>() : target, targetType, equalityPredicate, context);
    }

    /**
     * Processes the given collection of source objects into the given collection of
     * target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType>           The source type to process from
     * @param <SourceCollectionType> The {@link Collection} type of the source elements
     * @param <TargetType>           The target type to process to
     * @param <TargetCollectionType> The {@link Collection} type of the target elements
     * @param source                 The collection of source objects to process; might be null.
     * @param target                 The target collection of objects to process into; might be null
     *                               although in this case null is returned.
     * @param targetType             The type to process all of the source objects to; might <b>not</b>
     *                               be null.
     * @return The given target collection, might be null if the given target
     * collection was null
     */
    default <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType
    processInto(SourceCollectionType source,
                TargetCollectionType target,
                Class<TargetType> targetType) {
        return processInto(source, target, targetType, null);
    }

    /**
     * Processes the given collection of source objects into the given collection of
     * target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType>           The source type to process from
     * @param <SourceCollectionType> The {@link Collection} type of the source elements
     * @param <TargetType>           The target type to process to
     * @param <TargetCollectionType> The {@link Collection} type of the target elements
     * @param source                 The collection of source objects to process; might be null.
     * @param target                 The target collection of objects to process into; might be null
     *                               although in this case null is returned.
     * @param targetType             The type to process all of the source objects to; might <b>not</b>
     *                               be null.
     * @param context                Predefined {@link ProcessingContext}; might be null.
     * @return The given target collection, might be null if the given target
     * collection was null
     */
    <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType
    processInto(SourceCollectionType source,
                TargetCollectionType target,
                Class<TargetType> targetType,
                ProcessingContext context);

    /**
     * Processes the given collection of source objects into the given collection of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType>           The source type to process from
     * @param <SourceCollectionType> The {@link Collection} type of the source elements
     * @param <TargetType>           The target type to process to
     * @param <TargetCollectionType> The {@link Collection} type of the target elements
     * @param source                 The collection of source objects to process; might be null.
     * @param target                 The target collection of objects to process into; might be null
     *                               although in this case null is returned.
     * @param targetType             The type to process all of the source objects to; might <b>not</b>
     *                               be null.
     * @param equalityPredicate      The {@link BiPredicate} to use for checking equality between source
     *                               and target objects, determining the latter to be reused; might
     *                               <b>not</b> be null.
     * @return The given target collection, might be null if the given target
     * collection was null
     */
    default <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType
    processIntoAligning(SourceCollectionType source,
                        TargetCollectionType target,
                        Class<TargetType> targetType,
                        BiPredicate<SourceType, TargetType> equalityPredicate) {
        return processIntoAligning(source, target, targetType, equalityPredicate, null);
    }

    /**
     * Processes the given collection of source objects into the given collection of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceType>           The source type to process from
     * @param <SourceCollectionType> The {@link Collection} type of the source elements
     * @param <TargetType>           The target type to process to
     * @param <TargetCollectionType> The {@link Collection} type of the target elements
     * @param source                 The collection of source objects to process; might be null.
     * @param target                 The target collection of objects to process into; might be null
     *                               although in this case null is returned.
     * @param targetType             The type to process all of the source objects to; might <b>not</b>
     *                               be null.
     * @param equalityPredicate      The {@link BiPredicate} to use for checking equality between source
     *                               and target objects, determining the latter to be reused; might
     *                               <b>not</b> be null.
     * @param context                Predefined {@link ProcessingContext}; might be null.
     * @return The given target collection, might be null if the given target
     * collection was null
     */
    <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType
    processIntoAligning(SourceCollectionType source,
                        TargetCollectionType target,
                        Class<TargetType> targetType,
                        BiPredicate<SourceType, TargetType> equalityPredicate,
                        ProcessingContext context);

    /**
     * Processes the given list of source objects to a new list of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The list of source objects to process; might be null, although in
     *                     this case null is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @return The processed target objects, might be null if the given source
     * object list was null
     */
    default <SourceType, TargetType> List<TargetType> processListStrictly(Class<SourceType> sourceType,
                                                                          List<SourceType> source,
                                                                          Class<TargetType> targetType) {
        return source == null ? null : processStrictlyInto(sourceType, source, new ArrayList<>(), targetType);
    }

    /**
     * Processes the given list of source objects to a new list of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The list of source objects to process; might be null, although in
     *                     this case null is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given source
     * object list was null
     */
    default <SourceType, TargetType> List<TargetType> processListStrictly(Class<SourceType> sourceType,
                                                                          List<SourceType> source,
                                                                          Class<TargetType> targetType,
                                                                          ProcessingContext context) {
        return source == null ? null : processStrictlyInto(sourceType, source, new ArrayList<>(), targetType, context);
    }

    /**
     * Processes the given list of source objects to a new list of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The list of source objects to process; might be null.
     * @param target       The target list of objects to process into; might be null,
     *                     in this case a new list is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @return The processed target objects, might be null if the given target
     * object list was null
     */
    default <SourceType, TargetType> List<TargetType> processListStrictly(Class<SourceType> sourceType,
                                                                          List<SourceType> source,
                                                                          List<TargetType> target,
                                                                          Class<TargetType> targetType) {
        return processStrictlyInto(sourceType, source, target == null ? new ArrayList<>() : target, targetType);
    }

    /**
     * Processes the given list of source objects to a new list of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The list of source objects to process; might be null.
     * @param target       The target list of objects to process into; might be null,
     *                     in this case a new list is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given target
     * object list was null
     */
    default <SourceType, TargetType> List<TargetType> processListStrictly(Class<SourceType> sourceType,
                                                                          List<SourceType> source,
                                                                          List<TargetType> target,
                                                                          Class<TargetType> targetType,
                                                                          ProcessingContext context) {
        return processStrictlyInto(sourceType, source, target == null ? new ArrayList<>() : target, targetType, context);
    }

    /**
     * Processes the given list of source objects into the given list of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)}.
     *
     * @param <SourceType>      The source type to process from
     * @param <TargetType>      The target type to process to
     * @param sourceType        The source type to process; might <b>not</b> be null.
     * @param source            The list of source objects to process; might be null.
     * @param target            The target list of objects to process into; might be null
     *                          although in this case null is returned.
     * @param targetType        The type to process all of the source objects to; might <b>not</b>
     *                          be null.
     * @param equalityPredicate The {@link BiPredicate} to use for checking equality between source
     *                          and target objects, determining the latter to be reused; might
     *                          <b>not</b> be null.
     * @return The given target list, might be null if the given target
     * list was null
     */
    default <SourceType, TargetType> List<TargetType> processListStrictlyAligning(Class<SourceType> sourceType,
                                                                                  List<SourceType> source,
                                                                                  List<TargetType> target,
                                                                                  Class<TargetType> targetType,
                                                                                  BiPredicate<SourceType, TargetType> equalityPredicate) {
        return processStrictlyIntoAligning(sourceType, source, target == null ? new ArrayList<>() : target, targetType, equalityPredicate);
    }

    /**
     * Processes the given list of source objects into the given list of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)}.
     *
     * @param <SourceType>      The source type to process from
     * @param <TargetType>      The target type to process to
     * @param sourceType        The source type to process; might <b>not</b> be null.
     * @param source            The list of source objects to process; might be null.
     * @param target            The target list of objects to process into; might be null
     *                          although in this case null is returned.
     * @param targetType        The type to process all of the source objects to; might <b>not</b>
     *                          be null.
     * @param equalityPredicate The {@link BiPredicate} to use for checking equality between source
     *                          and target objects, determining the latter to be reused; might
     *                          <b>not</b> be null.
     * @param context           Predefined {@link ProcessingContext}; might be null.
     * @return The given target list, might be null if the given target
     * list was null
     */
    default <SourceType, TargetType> List<TargetType> processListStrictlyAligning(Class<SourceType> sourceType,
                                                                                  List<SourceType> source,
                                                                                  List<TargetType> target,
                                                                                  Class<TargetType> targetType,
                                                                                  BiPredicate<SourceType, TargetType> equalityPredicate,
                                                                                  ProcessingContext context) {
        return processStrictlyIntoAligning(sourceType, source, target == null ? new ArrayList<>() : target, targetType, equalityPredicate, context);
    }

    /**
     * Processes the given set of source objects to a new set of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The set of source objects to process; might be null, although in
     *                     this case null is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSetStrictly(Class<SourceType> sourceType,
                                                                        Set<SourceType> source,
                                                                        Class<TargetType> targetType) {
        return source == null ? null : processStrictlyInto(sourceType, source, new HashSet<>(), targetType);
    }

    /**
     * Processes the given set of source objects to a new set of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The set of source objects to process; might be null, although in
     *                     this case null is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSetStrictly(Class<SourceType> sourceType,
                                                                        Set<SourceType> source,
                                                                        Class<TargetType> targetType,
                                                                        ProcessingContext context) {
        return source == null ? null : processStrictlyInto(sourceType, source, new HashSet<>(), targetType, context);
    }

    /**
     * Processes the given set of source objects to a new set of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The list of source objects to process; might be null.
     * @param target       The target set of objects to process into; might be null,
     *                     in this case a new set is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @return The processed target objects, might be null if the given target
     * object set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSetStrictly(Class<SourceType> sourceType,
                                                                        Set<SourceType> source,
                                                                        Set<TargetType> target,
                                                                        Class<TargetType> targetType) {
        return processStrictlyInto(sourceType, source, target == null ? new HashSet<>() : target, targetType);
    }

    /**
     * Processes the given set of source objects to a new set of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param sourceType   The source type to process; might <b>not</b> be null.
     * @param source       The list of source objects to process; might be null.
     * @param target       The target set of objects to process into; might be null,
     *                     in this case a new set is returned.
     * @param targetType   The type to process all of the source objects to; might <b>not</b>
     *                     be null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given target
     * object set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSetStrictly(Class<SourceType> sourceType,
                                                                        Set<SourceType> source,
                                                                        Set<TargetType> target,
                                                                        Class<TargetType> targetType,
                                                                        ProcessingContext context) {
        return processStrictlyInto(sourceType, source, target == null ? new HashSet<>() : target, targetType, context);
    }

    /**
     * Processes the given set of source objects into the given set of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)}.
     *
     * @param <SourceType>      The source type to process from
     * @param <TargetType>      The target type to process to
     * @param sourceType        The source type to process; might <b>not</b> be null.
     * @param source            The set of source objects to process; might be null.
     * @param target            The target set of objects to process into; might be null
     *                          although in this case null is returned.
     * @param targetType        The type to process all of the source objects to; might <b>not</b>
     *                          be null.
     * @param equalityPredicate The {@link BiPredicate} to use for checking equality between source
     *                          and target objects, determining the latter to be reused; might
     *                          <b>not</b> be null.
     * @return The given target set, might be null if the given target
     * set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSetStrictlyAligning(Class<SourceType> sourceType,
                                                                                Set<SourceType> source,
                                                                                Set<TargetType> target,
                                                                                Class<TargetType> targetType,
                                                                                BiPredicate<SourceType, TargetType> equalityPredicate) {
        return processStrictlyIntoAligning(sourceType, source, target == null ? new HashSet<>() : target, targetType, equalityPredicate);
    }

    /**
     * Processes the given set of source objects into the given set of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)}.
     *
     * @param <SourceType>      The source type to process from
     * @param <TargetType>      The target type to process to
     * @param sourceType        The source type to process; might <b>not</b> be null.
     * @param source            The set of source objects to process; might be null.
     * @param target            The target set of objects to process into; might be null
     *                          although in this case null is returned.
     * @param targetType        The type to process all of the source objects to; might <b>not</b>
     *                          be null.
     * @param equalityPredicate The {@link BiPredicate} to use for checking equality between source
     *                          and target objects, determining the latter to be reused; might
     *                          <b>not</b> be null.
     * @param context           Predefined {@link ProcessingContext}; might be null.
     * @return The given target set, might be null if the given target
     * set was null
     */
    default <SourceType, TargetType> Set<TargetType> processSetStrictlyAligning(Class<SourceType> sourceType,
                                                                                Set<SourceType> source,
                                                                                Set<TargetType> target,
                                                                                Class<TargetType> targetType,
                                                                                BiPredicate<SourceType, TargetType> equalityPredicate,
                                                                                ProcessingContext context) {
        return processStrictlyIntoAligning(sourceType, source, target == null ? new HashSet<>() : target, targetType, equalityPredicate, context);
    }

    /**
     * Processes the given collection of source objects into the given collection of
     * target objects ensuring that a {@link Processor} of the given source/target
     * type is called even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType>           The source type to process from
     * @param <SourceCollectionType> The {@link Collection} type of the source elements
     * @param <TargetType>           The target type to process to
     * @param <TargetCollectionType> The {@link Collection} type of the target elements
     * @param sourceType             The source type to process; might <b>not</b> be null.
     * @param source                 The collection of source objects to process; might be null.
     * @param target                 The target collection of objects to process into; might be null
     *                               although in this case null is returned.
     * @param targetType             The type to process all of the source objects to; might <b>not</b>
     *                               be null.
     * @return The given target collection, might be null if the given target
     * collection was null
     */
    default <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType
    processStrictlyInto(Class<SourceType> sourceType,
                        SourceCollectionType source,
                        TargetCollectionType target,
                        Class<TargetType> targetType) {
        return processStrictlyInto(sourceType, source, target, targetType, null);
    }

    /**
     * Processes the given collection of source objects into the given collection of
     * target objects ensuring that a {@link Processor} of the given source/target
     * type is called even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceType>           The source type to process from
     * @param <SourceCollectionType> The {@link Collection} type of the source elements
     * @param <TargetType>           The target type to process to
     * @param <TargetCollectionType> The {@link Collection} type of the target elements
     * @param sourceType             The source type to process; might <b>not</b> be null.
     * @param source                 The collection of source objects to process; might be null.
     * @param target                 The target collection of objects to process into; might be null
     *                               although in this case null is returned.
     * @param targetType             The type to process all of the source objects to; might <b>not</b>
     *                               be null.
     * @param context                Predefined {@link ProcessingContext}; might be null.
     * @return The given target collection, might be null if the given target
     * collection was null
     */
    <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType
    processStrictlyInto(Class<SourceType> sourceType,
                        SourceCollectionType source,
                        TargetCollectionType target,
                        Class<TargetType> targetType,
                        ProcessingContext context);

    /**
     * Processes the given collection of source objects into the given collection of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)}.
     *
     * @param <SourceType>           The source type to process from
     * @param <SourceCollectionType> The type of the source collection
     * @param <TargetCollectionType> The type of the target collection
     * @param <TargetType>           The target type to process to
     * @param sourceType             The source type to process; might <b>not</b> be null.
     * @param source                 The collection of source objects to process; might be null.
     * @param target                 The target collection of objects to process into; might be null
     *                               although in this case null is returned.
     * @param targetType             The type to process all of the source objects to; might <b>not</b>
     *                               be null.
     * @param equalityPredicate      The {@link BiPredicate} to use for checking equality between source
     *                               and target objects, determining the latter to be reused; might
     *                               <b>not</b> be null.
     * @return The given target collection, might be null if the given target
     * collection was null
     */
    default <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType
    processStrictlyIntoAligning(Class<SourceType> sourceType,
                                SourceCollectionType source,
                                TargetCollectionType target,
                                Class<TargetType> targetType,
                                BiPredicate<SourceType, TargetType> equalityPredicate) {
        return processStrictlyIntoAligning(sourceType, source, target, targetType, equalityPredicate, null);
    }

    /**
     * Processes the given collection of source objects into the given collection of
     * target objects by additionally putting already existing target elements into
     * the {@link ProcessingContext}, expecting those target elements to be used and
     * returned instead of creating new ones.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)}.
     *
     * @param <SourceType>           The source type to process from
     * @param <SourceCollectionType> The type of the source collection
     * @param <TargetCollectionType> The type of the target collection
     * @param <TargetType>           The target type to process to
     * @param sourceType             The source type to process; might <b>not</b> be null.
     * @param source                 The collection of source objects to process; might be null.
     * @param target                 The target collection of objects to process into; might be null
     *                               although in this case null is returned.
     * @param targetType             The type to process all of the source objects to; might <b>not</b>
     *                               be null.
     * @param equalityPredicate      The {@link BiPredicate} to use for checking equality between source
     *                               and target objects, determining the latter to be reused; might
     *                               <b>not</b> be null.
     * @param context                Predefined {@link ProcessingContext}; might be null.
     * @return The given target collection, might be null if the given target
     * collection was null
     */
    <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType
    processStrictlyIntoAligning(Class<SourceType> sourceType,
                                SourceCollectionType source,
                                TargetCollectionType target,
                                Class<TargetType> targetType,
                                BiPredicate<SourceType, TargetType> equalityPredicate,
                                ProcessingContext context);

    /**
     * Processes the given list of source objects to a new map of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceTypeKey>   The source type of the key elements
     * @param <SourceTypeValue> The source type of the value elements
     * @param <TargetTypeKey>   The target type of the key elements
     * @param <TargetTypeValue> The target type of the value elements
     * @param source            The map of source objects to process; might be null, although in
     *                          this case null is returned.
     * @param targetTypeKey     The type to process all of the source key objects to; might
     *                          <b>not</b> be null.
     * @param targetTypeValue   The type to process all of the source value objects to; might
     *                          <b>not</b> be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processMap(
            Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
            Class<TargetTypeValue> targetTypeValue) {
        return source == null ? null : processInto(source, new HashMap<>(), targetTypeKey, targetTypeValue, null);
    }

    /**
     * Processes the given list of source objects to a new map of target objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceTypeKey>   The source type of the key elements
     * @param <SourceTypeValue> The source type of the value elements
     * @param <TargetTypeKey>   The target type of the key elements
     * @param <TargetTypeValue> The target type of the value elements
     * @param source            The map of source objects to process; might be null, although in
     *                          this case null is returned.
     * @param targetTypeKey     The type to process all of the source key objects to; might
     *                          <b>not</b> be null.
     * @param targetTypeValue   The type to process all of the source value objects to; might
     *                          <b>not</b> be null.
     * @param context           Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processMap(
            Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
            Class<TargetTypeValue> targetTypeValue, ProcessingContext context) {
        return source == null ? null : processInto(source, new HashMap<>(), targetTypeKey, targetTypeValue, context);
    }

    // ############################################################################################################
    // ################################################### MAP ####################################################
    // ############################################################################################################

    /**
     * Processes the given map of source objects into the given collection of target
     * objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceTypeKey>   The source type of the key elements
     * @param <SourceTypeValue> The source type of the value elements
     * @param <TargetTypeKey>   The target type of the key elements
     * @param <TargetTypeValue> The target type of the value elements
     * @param source            The map of source objects to process; might be null, although in
     *                          this case null is returned.
     * @param target            The target map of objects to process into; might be null although
     *                          in this case null is returned.
     * @param targetTypeKey     The type to process all of the source key objects to; might
     *                          <b>not</b> be null.
     * @param targetTypeValue   The type to process all of the source value objects to; might
     *                          <b>not</b> be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processInto(
            Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
            Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
        return processInto(source, target, targetTypeKey, targetTypeValue, null);
    }

    /**
     * Processes the given map of source objects into the given collection of target
     * objects.
     * <p>
     * Uses {@link #process(Object, Class)}.
     *
     * @param <SourceTypeKey>   The source type of the key elements
     * @param <SourceTypeValue> The source type of the value elements
     * @param <TargetTypeKey>   The target type of the key elements
     * @param <TargetTypeValue> The target type of the value elements
     * @param source            The map of source objects to process; might be null, although in
     *                          this case null is returned.
     * @param target            The target map of objects to process into; might be null although
     *                          in this case null is returned.
     * @param targetTypeKey     The type to process all of the source key objects to; might
     *                          <b>not</b> be null.
     * @param targetTypeValue   The type to process all of the source value objects to; might
     *                          <b>not</b> be null.
     * @param context           Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processInto(
            Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
            Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue, ProcessingContext context);

    /**
     * Processes the given map of source objects to a new map of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceTypeKey>   The source type of the key elements
     * @param <SourceTypeValue> The source type of the value elements
     * @param <TargetTypeKey>   The target type of the key elements
     * @param <TargetTypeValue> The target type of the value elements
     * @param sourceTypeKey     The source key type to process; might <b>not</b> be null.
     * @param sourceTypeValue   The source value type to process; might <b>not</b> be null.
     * @param source            The map of source objects to process; might be null, although in
     *                          this case null is returned.
     * @param targetTypeKey     The type to process all of the source key objects to; might
     *                          <b>not</b> be null.
     * @param targetTypeValue   The type to process all of the source value objects to; might
     *                          <b>not</b> be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processMapStrictly(
            Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
            Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
            Class<TargetTypeValue> targetTypeValue) {
        return source == null ? null
                : processStrictlyInto(sourceTypeKey, sourceTypeValue, source, new HashMap<>(), targetTypeKey,
                targetTypeValue, null);
    }

    /**
     * Processes the given map of source objects to a new map of target objects
     * ensuring that a {@link Processor} of the given source/target type is called
     * even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceTypeKey>   The source type of the key elements
     * @param <SourceTypeValue> The source type of the value elements
     * @param <TargetTypeKey>   The target type of the key elements
     * @param <TargetTypeValue> The target type of the value elements
     * @param sourceTypeKey     The source key type to process; might <b>not</b> be null.
     * @param sourceTypeValue   The source value type to process; might <b>not</b> be null.
     * @param source            The map of source objects to process; might be null, although in
     *                          this case null is returned.
     * @param targetTypeKey     The type to process all of the source key objects to; might
     *                          <b>not</b> be null.
     * @param targetTypeValue   The type to process all of the source value objects to; might
     *                          <b>not</b> be null.
     * @param context           Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processMapStrictly(
            Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
            Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
            Class<TargetTypeValue> targetTypeValue, ProcessingContext context) {
        return source == null ? null
                : processStrictlyInto(sourceTypeKey, sourceTypeValue, source, new HashMap<>(), targetTypeKey,
                targetTypeValue, context);
    }

    /**
     * Processes the given map of source objects into the given collection of target
     * objects ensuring that a {@link Processor} of the given source/target type is
     * called even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceTypeKey>   The source type of the key elements
     * @param <SourceTypeValue> The source type of the value elements
     * @param <TargetTypeKey>   The target type of the key elements
     * @param <TargetTypeValue> The target type of the value elements
     * @param sourceTypeKey     The source key type to process; might <b>not</b> be null.
     * @param sourceTypeValue   The source value type to process; might <b>not</b> be null.
     * @param source            The map of source objects to process; might be null, although in
     *                          this case null is returned.
     * @param target            The target map of objects to process into; might be null although
     *                          in this case null is returned.
     * @param targetTypeKey     The type to process all of the source key objects to; might
     *                          <b>not</b> be null.
     * @param targetTypeValue   The type to process all of the source value objects to; might
     *                          <b>not</b> be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    default <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processStrictlyInto(
            Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
            Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
            Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
        return processStrictlyInto(sourceTypeKey, sourceTypeValue, source, target, targetTypeKey, targetTypeValue,
                null);
    }

    /**
     * Processes the given map of source objects into the given collection of target
     * objects ensuring that a {@link Processor} of the given source/target type is
     * called even if the source element is null.
     * <p>
     * Uses {@link #processStrictly(Class, Object, Class)};
     *
     * @param <SourceTypeKey>   The source type of the key elements
     * @param <SourceTypeValue> The source type of the value elements
     * @param <TargetTypeKey>   The target type of the key elements
     * @param <TargetTypeValue> The target type of the value elements
     * @param sourceTypeKey     The source key type to process; might <b>not</b> be null.
     * @param sourceTypeValue   The source value type to process; might <b>not</b> be null.
     * @param source            The map of source objects to process; might be null, although in
     *                          this case null is returned.
     * @param target            The target map of objects to process into; might be null although
     *                          in this case null is returned.
     * @param targetTypeKey     The type to process all of the source key objects to; might
     *                          <b>not</b> be null.
     * @param targetTypeValue   The type to process all of the source value objects to; might
     *                          <b>not</b> be null.
     * @param context           Predefined {@link ProcessingContext}; might be null.
     * @return The processed target objects, might be null if the given source
     * object set was null
     */
    <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processStrictlyInto(
            Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
            Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
            Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue, ProcessingContext context);

    /**
     * Processes the given enumerated source object using its name.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The source object to process; might be null, although in this case
     *                     null is returned.
     * @param targetType   The enumerated target type to process to; might <b>not</b> be
     *                     null.
     * @return The processed target object, always null if the source is null, never
     * null otherwise
     */
    default <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processNamed(
            SourceType source, Class<TargetType> targetType) {
        return processNamed(source, targetType, null);
    }

    /**
     * Processes the given enumerated source object using its name.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The source object to process; might be null, although in this case
     *                     null is returned.
     * @param targetType   The enumerated target type to process to; might <b>not</b> be
     *                     null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target object, always null if the source is null, never
     * null otherwise
     */
    <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processNamed(
            SourceType source, Class<TargetType> targetType, ProcessingContext context);

    // ############################################################################################################
    // ################################################# SPECIAL ##################################################
    // ############################################################################################################

    /**
     * Processes the given enumerated source object using its ordinal.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The source object to process; might be null, although in this case
     *                     null is returned.
     * @param targetType   The enumerated target type to process to; might <b>not</b> be
     *                     null.
     * @return The processed target object, always null if the source is null, never
     * null otherwise
     */
    default <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processOrdinal(
            SourceType source, Class<TargetType> targetType) {
        return processOrdinal(source, targetType, null);
    }

    /**
     * Processes the given enumerated source object using its ordinal.
     *
     * @param <SourceType> The source type to process from
     * @param <TargetType> The target type to process to
     * @param source       The source object to process; might be null, although in this case
     *                     null is returned.
     * @param targetType   The enumerated target type to process to; might <b>not</b> be
     *                     null.
     * @param context      Predefined {@link ProcessingContext}; might be null.
     * @return The processed target object, always null if the source is null, never
     * null otherwise
     */
    <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processOrdinal(
            SourceType source, Class<TargetType> targetType, ProcessingContext context);

    interface Processor<SourceType, TargetType> {

        TargetType process(SourceType source, ProcessingDelegate context) throws Exception;
    }

    interface BiProcessor<SourceType, TargetType> extends Processor<SourceType, TargetType> {

        SourceType reverse(TargetType target, ProcessingDelegate context) throws Exception;
    }
}
