package com.mantledillusion.data.saman;

import org.junit.Before;
import org.junit.Test;

import com.mantledillusion.data.saman.exception.ConversionException;

import static org.junit.Assert.*;

public class EnumConversionTest {
	
	private  enum SourceEnum {

		A,
		B;
	}
	
	private  enum TargetNameEnum {

		A,
		B;
	}
	
	private  enum TargetWrongNameEnum {

		VALUE_A,
		VALUE_B;
	}
	
	private  enum TargetOrdinalEnum {

		FIRST,
		SECOND;
	}
	
	private  enum TargetWrongOrdinalEnum {

		FIRST,
		SECOND,
		THIRD;
	}

	private ConversionService service;
	
	@Before
	public void before() {
		this.service = ConversionServiceFactory.of();
	}
	
	@Test
	public void testConversionByName() {
		assertSame(TargetNameEnum.A, this.service.convertNamed(SourceEnum.A, TargetNameEnum.class));
		assertSame(TargetNameEnum.A, this.service.convertNamed(SourceEnum.A, TargetNameEnum.class)); // TO MAKE SURE CACHED CONVERTER IS USED
	}
	
	@Test
	public void testNullConversionByName() {
		assertNull(this.service.convertNamed(null, TargetNameEnum.class));
	}
	
	@Test(expected=ConversionException.class)
	public void testConversionByNameWithoutType() {
		this.service.convertNamed(SourceEnum.A, null);
	}
	
	@Test(expected=ConversionException.class)
	public void testConversionByWrongName() {
		this.service.convertNamed(SourceEnum.A, TargetWrongNameEnum.class);
	}
	
	@Test
	public void testConversionByOrdinal() {
		assertSame(TargetOrdinalEnum.SECOND, this.service.convertOrdinal(SourceEnum.B, TargetOrdinalEnum.class));
		assertSame(TargetOrdinalEnum.SECOND, this.service.convertOrdinal(SourceEnum.B, TargetOrdinalEnum.class)); // TO MAKE SURE CACHED CONVERTER IS USED
	}
	
	@Test
	public void testNullConversionByOrdinal() {
		assertNull(this.service.convertOrdinal(null, TargetNameEnum.class));
	}
	
	@Test(expected=ConversionException.class)
	public void testConversionByOrdinalWithoutType() {
		this.service.convertOrdinal(SourceEnum.A, null);
	}

	@Test(expected=ConversionException.class)
	public void testConversionByWrongOrdinal() {
		this.service.convertOrdinal(SourceEnum.B, TargetWrongOrdinalEnum.class);
	}
}
