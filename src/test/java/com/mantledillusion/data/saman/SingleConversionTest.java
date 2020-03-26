package com.mantledillusion.data.saman;

import org.junit.jupiter.api.Test;

import com.mantledillusion.data.saman.exception.ProcessingException;
import com.mantledillusion.data.saman.exception.ProcessorException;
import com.mantledillusion.data.saman.exception.NoProcessorException;
import com.mantledillusion.data.saman.interfaces.Converter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

import org.junit.jupiter.api.Assertions;

public class SingleConversionTest extends AbstractConversionTest {
	
	@Test
	public void testConversion() {
		Assertions.assertEquals(TEST_ID_A, this.service.process(SOURCE_A, TargetPojo.class).id);
		Assertions.assertEquals(TEST_ID_A, this.service.process(TARGET_A, SourcePojo.class).id);
	}
	
	@Test
	public void testNullConversion() {
		Assertions.assertNull(this.service.process(null, TargetPojo.class));
		Assertions.assertNotNull(this.service.processNull(SourcePojo.class, TargetPojo.class));
		Assertions.assertNotNull(this.service.processStrictly(SourcePojo.class, null, TargetPojo.class));
	}
	
	@Test
	public void testSuperTypeConversion() {
		Assertions.assertEquals(TEST_ID_A, this.service.process(new SourcePojo(TEST_ID_A) {/* Anonymous Super Type */}, TargetPojo.class).id);
	}

	@Test
	public void testMissingSourceType() {
		Assertions.assertThrows(ProcessingException.class, () -> this.service.processStrictly(null, SOURCE_A, TargetPojo.class));
	}

	@Test
	public void testMissingTargetType() {
		Assertions.assertThrows(ProcessingException.class, () -> this.service.process(SOURCE_A, null));
	}

	@Test
	public void testMissingConverter() {
		Assertions.assertThrows(NoProcessorException.class, () -> this.service.process(TEST_ID_A, TargetPojo.class));
	}

	@Test
	public void testCheckedProcessorException() {
		Assertions.assertThrows(ProcessorException.class, () -> new DefaultProcessingService(ProcessorRegistry.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				throw new Exception();
			}
		})).process(SOURCE_A, TargetPojo.class));
	}

	@Test
	public void testWrappedRuntimeProcessorException() {
		Assertions.assertThrows(ProcessorException.class, () -> new DefaultProcessingService(ProcessorRegistry.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				throw new RuntimeException();
			}
		})).process(SOURCE_A, TargetPojo.class));
	}
	
	@Test
	public void testUnwrappedRuntimeProcessorException() {
		DefaultProcessingService service = new DefaultProcessingService(ProcessorRegistry.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				throw new RuntimeException();
			}
		}));
		service.setWrapRuntimeExceptions(false);
		Assertions.assertThrows(RuntimeException.class, () -> service.process(SOURCE_A, TargetPojo.class));
	}
}
