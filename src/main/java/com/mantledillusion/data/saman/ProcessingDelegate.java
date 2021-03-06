package com.mantledillusion.data.saman;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

public class ProcessingDelegate extends ProcessingContext implements ProcessingService {

	private final ProcessingService service;

	ProcessingDelegate(ProcessingService service, ProcessingContext base) {
		super(base);
		this.service = service;
	}

	// DELEGATED

	// ############################################################################################################
	// ############################################# SINGLE INSTANCES #############################################
	// ############################################################################################################

	@Override
	public <SourceType, TargetType> TargetType process(SourceType source, Class<TargetType> targetType) {
		return this.service.process(source, targetType, this);
	}

	@Override
	public <SourceType, TargetType> TargetType processNull(Class<SourceType> sourceType, Class<TargetType> targetType) {
		return this.service.processNull(sourceType, targetType, this);
	}

	@Override
	public <SourceType, TargetType> TargetType processStrictly(Class<SourceType> sourceType, SourceType source,
			Class<TargetType> targetType) {
		return this.service.processStrictly(sourceType, source, targetType, this);
	}

	@Override
	public <SourceType, TargetType> TargetType processStrictly(Class<SourceType> sourceType, SourceType source,
			Class<TargetType> targetType, ProcessingContext context) {
		return this.service.processStrictly(sourceType, source, targetType, context);
	}

	// ############################################################################################################
	// ############################################### COLLECTIONS ################################################
	// ############################################################################################################

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processInto(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType) {
		return this.service.processInto(source, target, targetType, this);
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processInto(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType,
			ProcessingContext context) {
		return this.service.processInto(source, target, targetType, context);
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processIntoAligning(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType,
			BiPredicate<SourceType, TargetType> equalityPredicate) {
		return this.service.processIntoAligning(source, target, targetType, equalityPredicate, this);
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processIntoAligning(
			SourceCollectionType source, TargetCollectionType target, Class<TargetType> targetType,
			BiPredicate<SourceType, TargetType> equalityPredicate, ProcessingContext context) {
		return this.service.processIntoAligning(source, target, targetType, equalityPredicate, context);
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processStrictlyInto(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType) {
		return this.service.processStrictlyInto(sourceType, source, target, targetType, this);
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processStrictlyInto(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType, ProcessingContext context) {
		return this.service.processStrictlyInto(sourceType, source, target, targetType, context);
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processStrictlyIntoAligning(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType, BiPredicate<SourceType, TargetType> equalityPredicate) {
		return this.service.processStrictlyIntoAligning(sourceType, source, target, targetType, equalityPredicate, this);
	}

	@Override
	public <SourceType, SourceCollectionType extends Collection<SourceType>, TargetCollectionType extends Collection<TargetType>, TargetType> TargetCollectionType processStrictlyIntoAligning(
			Class<SourceType> sourceType, SourceCollectionType source, TargetCollectionType target,
			Class<TargetType> targetType, BiPredicate<SourceType, TargetType> equalityPredicate,
			ProcessingContext context) {
		return this.service.processStrictlyIntoAligning(sourceType, source, target, targetType, equalityPredicate, context);
	}

	// ############################################################################################################
	// ################################################### MAP ####################################################
	// ############################################################################################################

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processMap(
			Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
			Class<TargetTypeValue> targetTypeValue) {
		return this.service.processMap(source, targetTypeKey, targetTypeValue, this);
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processInto(
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
		return this.service.processInto(source, target, targetTypeKey, targetTypeValue, this);
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processInto(
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue, ProcessingContext context) {
		return this.service.processInto(source, target, targetTypeKey, targetTypeValue, context);
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processMapStrictly(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Class<TargetTypeKey> targetTypeKey,
			Class<TargetTypeValue> targetTypeValue) {
		return this.service.processMapStrictly(sourceTypeKey, sourceTypeValue, source, targetTypeKey, targetTypeValue,
				this);
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processStrictlyInto(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue) {
		return this.service.processStrictlyInto(sourceTypeKey, sourceTypeValue, source, target, targetTypeKey,
				targetTypeValue, this);
	}

	@Override
	public <SourceTypeKey, SourceTypeValue, TargetTypeKey, TargetTypeValue> Map<TargetTypeKey, TargetTypeValue> processStrictlyInto(
			Class<SourceTypeKey> sourceTypeKey, Class<SourceTypeValue> sourceTypeValue,
			Map<SourceTypeKey, SourceTypeValue> source, Map<TargetTypeKey, TargetTypeValue> target,
			Class<TargetTypeKey> targetTypeKey, Class<TargetTypeValue> targetTypeValue, ProcessingContext context) {
		return this.service.processStrictlyInto(sourceTypeKey, sourceTypeValue, source, target, targetTypeKey,
				targetTypeValue, context);
	}

	// ############################################################################################################
	// ################################################# SPECIAL ##################################################
	// ############################################################################################################

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processNamed(
			SourceType source, Class<TargetType> targetType) {
		return this.service.processNamed(source, targetType, this);
	}

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processNamed(
			SourceType source, Class<TargetType> targetType, ProcessingContext context) {
		return this.service.processNamed(source, targetType, context);
	}

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processOrdinal(
			SourceType source, Class<TargetType> targetType) {
		return this.service.processOrdinal(source, targetType, this);
	}

	@Override
	public <SourceType extends Enum<SourceType>, TargetType extends Enum<TargetType>> TargetType processOrdinal(
			SourceType source, Class<TargetType> targetType, ProcessingContext context) {
		return this.service.processOrdinal(source, targetType, context);
	}
}
