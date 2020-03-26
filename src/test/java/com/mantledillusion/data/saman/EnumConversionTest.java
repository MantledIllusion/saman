package com.mantledillusion.data.saman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mantledillusion.data.saman.exception.ProcessingException;

import org.junit.jupiter.api.Assertions;

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
	
	@BeforeEach
	public void before() {
		this.service = new DefaultProcessingService(ProcessorRegistry.of());
	}
	
	@Test
	public void testConversionByName() {
		Assertions.assertSame(TargetNameEnum.A, this.service.processNamed(SourceEnum.A, TargetNameEnum.class));
		Assertions.assertSame(TargetNameEnum.A, this.service.processNamed(SourceEnum.A, TargetNameEnum.class)); // TO MAKE SURE CACHED CONVERTER IS USED
	}
	
	@Test
	public void testNullConversionByName() {
		Assertions.assertNull(this.service.processNamed(null, TargetNameEnum.class));
	}
	
	@Test
	public void testConversionByNameWithoutType() {
		Assertions.assertThrows(ProcessingException.class, () -> this.service.processNamed(SourceEnum.A, null));
	}
	
	@Test
	public void testConversionByWrongName() {
		Assertions.assertThrows(ProcessingException.class, () -> this.service.processNamed(SourceEnum.A, TargetWrongNameEnum.class));
	}
	
	@Test
	public void testConversionByOrdinal() {
		Assertions.assertSame(TargetOrdinalEnum.SECOND, this.service.processOrdinal(SourceEnum.B, TargetOrdinalEnum.class));
		Assertions.assertSame(TargetOrdinalEnum.SECOND, this.service.processOrdinal(SourceEnum.B, TargetOrdinalEnum.class)); // TO MAKE SURE CACHED CONVERTER IS USED
	}
	
	@Test
	public void testNullConversionByOrdinal() {
		Assertions.assertNull(this.service.processOrdinal(null, TargetNameEnum.class));
	}
	
	@Test
	public void testConversionByOrdinalWithoutType() {
		Assertions.assertThrows(ProcessingException.class, () -> this.service.processOrdinal(SourceEnum.A, null));
	}

	@Test
	public void testConversionByWrongOrdinal() {
		Assertions.assertThrows(ProcessingException.class, () -> this.service.processOrdinal(SourceEnum.B, TargetWrongOrdinalEnum.class));
	}
}
