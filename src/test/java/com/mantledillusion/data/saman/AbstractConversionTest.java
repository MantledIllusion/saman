package com.mantledillusion.data.saman;

import org.junit.Before;

import com.mantledillusion.data.saman.interfaces.BiConverter;
import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

public class AbstractConversionTest {

	protected static final String TEST_ID_A = "idA";
	protected static final String TEST_ID_B = "idB";
	
	protected static final SourcePojo SOURCE_A = new SourcePojo(TEST_ID_A);
	protected static final SourcePojo SOURCE_B = new SourcePojo(TEST_ID_B);
	protected static final TargetPojo TARGET_A = new TargetPojo(TEST_ID_A);
	protected static final TargetPojo TARGET_B = new TargetPojo(TEST_ID_B);
	protected static final TargetPojo TARGET_NULL = new TargetPojo(null);

	protected ProcessingService service;
	
	@Before
	public void before() {
		this.service = ProcessingServiceFactory.of(new BiConverter<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo toTarget(SourcePojo source, ProcessingService service) throws Exception {
				return new TargetPojo(source == null ? null : source.id);
			}

			@Override
			public SourcePojo toSource(TargetPojo target, ProcessingService service) throws Exception {
				return new SourcePojo(target == null ? null : target.id);
			}
		});
	}
	
}
