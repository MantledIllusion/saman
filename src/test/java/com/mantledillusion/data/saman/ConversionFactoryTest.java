package com.mantledillusion.data.saman;

import org.junit.Test;

import com.mantledillusion.data.saman.exception.AmbiguousConverterException;
import com.mantledillusion.data.saman.exception.ConverterTypeException;
import com.mantledillusion.data.saman.interfaces.Converter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

public class ConversionFactoryTest {

	@Test(expected=AmbiguousConverterException.class)
	public void testAmbiguousConverters() {
		ConversionServiceFactory.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ConversionService service) throws Exception {
				return null;
			}
		}, new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ConversionService service) throws Exception {
				return null;
			}
		});
	}
	
	@Test(expected=ConverterTypeException.class)
	public <T> void testUndefinedConverterGenerics() {
		ConversionServiceFactory.of(new Converter<SourcePojo, T>() {

			@Override
			public T toTarget(SourcePojo source, ConversionService service) throws Exception {
				return null;
			}
		});
	}
}
