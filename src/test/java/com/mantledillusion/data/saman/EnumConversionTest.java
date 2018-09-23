package com.mantledillusion.data.saman;

import org.junit.Before;
import org.junit.Test;

import com.mantledillusion.data.saman.exception.ProcessingException;

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

	private ProcessingService service;
	
	@Before
	public void before() {
		this.service = ProcessingServiceFactory.of();
	}
	
	@Test
	public void testConversionByName() {
		assertSame(TargetNameEnum.A, this.service.processNamed(SourceEnum.A, TargetNameEnum.class));
		assertSame(TargetNameEnum.A, this.service.processNamed(SourceEnum.A, TargetNameEnum.class)); // TO MAKE SURE CACHED CONVERTER IS USED
	}
	
	@Test
	public void testNullConversionByName() {
		assertNull(this.service.processNamed(null, TargetNameEnum.class));
	}
	
	@Test(expected=ProcessingException.class)
	public void testConversionByNameWithoutType() {
		this.service.processNamed(SourceEnum.A, null);
	}
	
	@Test(expected=ProcessingException.class)
	public void testConversionByWrongName() {
		this.service.processNamed(SourceEnum.A, TargetWrongNameEnum.class);
	}
	
	@Test
	public void testConversionByOrdinal() {
		assertSame(TargetOrdinalEnum.SECOND, this.service.processOrdinal(SourceEnum.B, TargetOrdinalEnum.class));
		assertSame(TargetOrdinalEnum.SECOND, this.service.processOrdinal(SourceEnum.B, TargetOrdinalEnum.class)); // TO MAKE SURE CACHED CONVERTER IS USED
	}
	
	@Test
	public void testNullConversionByOrdinal() {
		assertNull(this.service.processOrdinal(null, TargetNameEnum.class));
	}
	
	@Test(expected=ProcessingException.class)
	public void testConversionByOrdinalWithoutType() {
		this.service.processOrdinal(SourceEnum.A, null);
	}

	@Test(expected=ProcessingException.class)
	public void testConversionByWrongOrdinal() {
		this.service.processOrdinal(SourceEnum.B, TargetWrongOrdinalEnum.class);
	}
}
