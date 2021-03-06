package com.mantledillusion.data.saman;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.mantledillusion.data.saman.interfaces.Converter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

public class ProcessingContextTest {
	
	private static final String ID = "_id";

	@Test
	public void testAvailableInContext() {
		ProcessingService service = new DefaultProcessingService(ProcessorRegistry.of(new Converter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate context) throws Exception {
				return new TargetPojo(context.get(String.class));
			}
		}));
		
		TargetPojo target = service.process(new SourcePojo(null), TargetPojo.class, ProcessingContext.of(ID));
		Assertions.assertEquals(ID, target.id);
	}
}
