package com.mantledillusion.data.saman;

import org.junit.Test;

import com.mantledillusion.data.saman.exception.ProcessingException;
import com.mantledillusion.data.saman.exception.ProcessorException;
import com.mantledillusion.data.saman.exception.NoProcessorException;
import com.mantledillusion.data.saman.interfaces.Converter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

import static org.junit.Assert.*;

public class SingleConversionTest extends AbstractConversionTest {
	
	@Test
	public void testConversion() {
		assertEquals(TEST_ID_A, this.service.process(SOURCE_A, TargetPojo.class).id);
		assertEquals(TEST_ID_A, this.service.process(TARGET_A, SourcePojo.class).id);
	}
	
	@Test
	public void testNullConversion() {
		assertNull(this.service.process(null, TargetPojo.class));
		assertNotNull(this.service.processNull(SourcePojo.class, TargetPojo.class));
		assertNotNull(this.service.processStrictly(SourcePojo.class, null, TargetPojo.class));
	}
	
	@Test
	public void testSuperTypeConversion() {
		assertEquals(TEST_ID_A, this.service.process(new SourcePojo(TEST_ID_A) {/* Anonymous Super Type */}, TargetPojo.class).id);
	}

	@Test(expected=ProcessingException.class)
	public void testMissingSourceType() {
		this.service.processStrictly(null, SOURCE_A, TargetPojo.class);
	}

	@Test(expected=ProcessingException.class)
	public void testMissingTargetType() {
		this.service.process(SOURCE_A, null);
	}
	
	@Test(expected=NoProcessorException.class)
	public void testMissingConverter() {
		this.service.process(TEST_ID_A, TargetPojo.class);
	}
	
	@Test(expected=ProcessorException.class)
	public void testCheckedProcessorException() {
		new DefaultProcessingService(ProcessorRegistry.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				throw new Exception();
			}
		})).process(SOURCE_A, TargetPojo.class);
	}
	
	@Test(expected=ProcessorException.class)
	public void testWrappedRuntimeProcessorException() {
		new DefaultProcessingService(ProcessorRegistry.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				throw new RuntimeException();
			}
		})).process(SOURCE_A, TargetPojo.class);
	}
	
	@Test(expected=RuntimeException.class)
	public void testUnwrappedRuntimeProcessorException() {
		DefaultProcessingService service = new DefaultProcessingService(ProcessorRegistry.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				throw new RuntimeException();
			}
		}));
		service.setWrapRuntimeExceptions(false);
		service.process(SOURCE_A, TargetPojo.class);
	}
}
