package com.mantledillusion.data.saman;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.mantledillusion.data.saman.exception.AmbiguousProcessorException;
import com.mantledillusion.data.saman.exception.ProcessorTypeException;
import com.mantledillusion.data.saman.interfaces.Converter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

public class ProcessorRegistryTest {

	@Test
	public void testAmbiguousConverters() {
		Assertions.assertThrows(AmbiguousProcessorException.class, () -> ProcessorRegistry.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				return null;
			}
		}, new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				return null;
			}
		}));
	}
	
	@Test
	public <T> void testUndefinedConverterGenerics() {
		Assertions.assertThrows(ProcessorTypeException.class, () -> ProcessorRegistry.of(new Converter<SourcePojo, T>() {

			@Override
			public T toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				return null;
			}
		}));
	}
}
