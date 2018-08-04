package com.mantledillusion.data.saman;

import org.junit.Before;
import org.junit.Test;

import com.mantledillusion.data.saman.exception.ConversionException;
import com.mantledillusion.data.saman.exception.NoConverterException;
import com.mantledillusion.data.saman.interfaces.BiConverter;
import com.mantledillusion.data.saman.interfaces.Converter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

import static org.junit.Assert.*;

public class ConversionTest {

	private static final String TEST_ID = "id";
	
	private static final SourcePojo SOURCE = new SourcePojo(TEST_ID);
	private static final TargetPojo TARGET = new TargetPojo(TEST_ID);
	
	private ConversionService service;
	
	@Before
	public void before() {
		this.service = ConversionServiceFactory.of(new BiConverter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ConversionService service) throws Exception {
				return new TargetPojo(source.id);
			}

			@Override
			public SourcePojo toSource(TargetPojo target, ConversionService service) throws Exception {
				return new SourcePojo(target.id);
			}
		});
	}
	
	@Test
	public void testConversion() {
		assertEquals(TEST_ID, this.service.convert(SOURCE, TargetPojo.class).id);
		assertEquals(TEST_ID, this.service.convert(TARGET, SourcePojo.class).id);
	}
	
	@Test
	public void testSuperTypeConversion() {
		assertEquals(TEST_ID, this.service.convert(new SourcePojo(TEST_ID) {/* Anonymous Super Type */}, TargetPojo.class).id);
	}
	
	@Test(expected=NoConverterException.class)
	public void testMissingConverter() {
		this.service.convert(TEST_ID, TargetPojo.class);
	}
	
	@Test(expected=ConversionException.class)
	public void testConversionException() {
		ConversionServiceFactory.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ConversionService service) throws Exception {
				throw new Exception();
			}
		}).convert(SOURCE, TargetPojo.class);
	}
}
