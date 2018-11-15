package com.mantledillusion.data.saman;

import org.junit.Test;

import com.mantledillusion.data.saman.exception.AmbiguousProcessorException;
import com.mantledillusion.data.saman.exception.ProcessorTypeException;
import com.mantledillusion.data.saman.interfaces.Converter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

public class ProcessorRegistryTest {

	@Test(expected=AmbiguousProcessorException.class)
	public void testAmbiguousConverters() {
		ProcessorRegistry.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				return null;
			}
		}, new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				return null;
			}
		});
	}
	
	@Test(expected=ProcessorTypeException.class)
	public <T> void testUndefinedConverterGenerics() {
		ProcessorRegistry.of(new Converter<SourcePojo, T>() {

			@Override
			public T toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				return null;
			}
		});
	}
}
