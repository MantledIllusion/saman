package com.mantledillusion.data.saman;

import com.mantledillusion.data.saman.interfaces.BiConverter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;
import org.junit.jupiter.api.BeforeEach;

public class AbstractConversionTest {

	protected static final String TEST_ID_A = "idA";
	protected static final String TEST_ID_B = "idB";
	
	protected static final SourcePojo SOURCE_A = new SourcePojo(TEST_ID_A);
	protected static final SourcePojo SOURCE_B = new SourcePojo(TEST_ID_B);
	protected static final TargetPojo TARGET_A = new TargetPojo(TEST_ID_A);
	protected static final TargetPojo TARGET_B = new TargetPojo(TEST_ID_B);
	protected static final TargetPojo TARGET_NULL = new TargetPojo(null);

	protected ProcessingService service;
	
	@BeforeEach
	public void before() {
		this.service = new DefaultProcessingService(ProcessorRegistry.of(new BiConverter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingDelegate context) throws Exception {
				return context.get(TargetPojo.class, new TargetPojo(source == null ? null : source.id));
			}

			@Override
			public SourcePojo toSource(TargetPojo target, ProcessingDelegate context) throws Exception {
				return context.get(SourcePojo.class, new SourcePojo(target == null ? null : target.id));
			}
		}));
	}
}