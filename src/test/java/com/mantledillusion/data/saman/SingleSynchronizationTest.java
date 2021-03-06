package com.mantledillusion.data.saman;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mantledillusion.data.saman.interfaces.BiSynchronizer;

import org.junit.jupiter.api.Assertions;

public class SingleSynchronizationTest {
	
	private static class SourcePojo extends com.mantledillusion.data.saman.obj.SourcePojo {
		
		public String value;

		public SourcePojo(String id) {
			super(id);
		}
	}
	
	private static class TargetPojo extends com.mantledillusion.data.saman.obj.TargetPojo {
		
		public String value;

		public TargetPojo(String id) {
			super(id);
		}
	}
	
	private static final String VALUE = "value";
	
	private ProcessingService service;
	private Map<String, SourcePojo> mockService;
	private Map<String, TargetPojo> mockDb;
	
	@BeforeEach
	public void before() {
		this.service = new DefaultProcessingService(ProcessorRegistry.of(new BiSynchronizer<SourcePojo, TargetPojo>() {

			@Override
			public TargetPojo fetchTarget(SourcePojo source, ProcessingDelegate service) throws Exception {
				if (mockDb.containsKey(source.id)) {
					return mockDb.get(source.id);
				} else {
					return new TargetPojo(source.id);
				}
			}

			@Override
			public void toTarget(SourcePojo source, TargetPojo target, ProcessingDelegate service) throws Exception {
				target.value = source.value;
			}
			
			@Override
			public TargetPojo persistTarget(TargetPojo target, SourcePojo source, ProcessingDelegate service) throws Exception {
				if (target.id == null) {
					target.id = RandomStringUtils.random(5);
				}
				mockDb.put(target.id, target);
				return target;
			}

			@Override
			public SourcePojo fetchSource(TargetPojo target, ProcessingDelegate service) throws Exception {
				if (mockService.containsKey(target.id)) {
					return mockService.get(target.id);
				} else {
					return new SourcePojo(target.id);
				}
			}

			@Override
			public void toSource(TargetPojo target, SourcePojo source, ProcessingDelegate service) throws Exception {
				source.value = target.value;
			}
			
			@Override
			public SourcePojo persistSource(SourcePojo source, TargetPojo target, ProcessingDelegate service) throws Exception {
				if (source.id == null) {
					source.id = RandomStringUtils.random(5);
				}
				mockService.put(source.id, source);
				return source;
			}
		}));
		this.mockService = new HashMap<>();
		this.mockDb = new HashMap<>();
	}
	
	@Test
	public void testSynchronization() {
		SourcePojo source = new SourcePojo(null);
		source.value = VALUE;
		
		TargetPojo target = this.service.process(source, TargetPojo.class);
		Assertions.assertEquals(1, this.mockDb.size());
		Assertions.assertEquals(VALUE, target.value);
		
		SourcePojo convertedSource = this.service.process(target, SourcePojo.class);
		Assertions.assertEquals(1, this.mockService.size());
		Assertions.assertEquals(VALUE, convertedSource.value);
		Assertions.assertEquals(target.id, convertedSource.id);
	}
}
