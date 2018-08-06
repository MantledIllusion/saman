package com.mantledillusion.data.saman;

import org.junit.Test;

import com.mantledillusion.data.saman.exception.ConversionException;
import com.mantledillusion.data.saman.exception.ConverterException;
import com.mantledillusion.data.saman.exception.NoConverterException;
import com.mantledillusion.data.saman.interfaces.Converter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

import static org.junit.Assert.*;

public class SingleConversionTest extends AbstractConversionTest {
	
	@Test
	public void testConversion() {
		assertEquals(TEST_ID_A, this.service.convert(SOURCE_A, TargetPojo.class).id);
		assertEquals(TEST_ID_A, this.service.convert(TARGET_A, SourcePojo.class).id);
	}
	
	@Test
	public void testNullConversion() {
		assertNull(this.service.convert(null, TargetPojo.class));
		assertNotNull(this.service.convertNull(SourcePojo.class, TargetPojo.class));
		assertNotNull(this.service.convertStrictly(SourcePojo.class, null, TargetPojo.class));
	}
	
	@Test
	public void testSuperTypeConversion() {
		assertEquals(TEST_ID_A, this.service.convert(new SourcePojo(TEST_ID_A) {/* Anonymous Super Type */}, TargetPojo.class).id);
	}

	@Test(expected=ConversionException.class)
	public void testMissingSourceType() {
		this.service.convertStrictly(null, SOURCE_A, TargetPojo.class);
	}

	@Test(expected=ConversionException.class)
	public void testMissingTargetType() {
		this.service.convert(SOURCE_A, null);
	}
	
	@Test(expected=NoConverterException.class)
	public void testMissingConverter() {
		this.service.convert(TEST_ID_A, TargetPojo.class);
	}
	
	@Test(expected=ConverterException.class)
	public void testConversionException() {
		ConversionServiceFactory.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ConversionService service) throws Exception {
				throw new Exception();
			}
		}).convert(SOURCE_A, TargetPojo.class);
	}
}
