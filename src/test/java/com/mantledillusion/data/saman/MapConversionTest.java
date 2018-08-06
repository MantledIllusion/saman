package com.mantledillusion.data.saman;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

import static org.junit.Assert.*;

public class MapConversionTest extends AbstractConversionTest {

	@Test
	public void testConvertMap() {
		Map<String, SourcePojo> sourceMap = new HashMap<>();
		sourceMap.put(TEST_ID_A, SOURCE_A);
		sourceMap.put(TEST_ID_B, SOURCE_B);
		Map<String, TargetPojo> targetMap = this.service.convertMap(sourceMap, String.class, TargetPojo.class);
		assertEquals(2, targetMap.size());
		assertEquals(TARGET_A, targetMap.get(TEST_ID_A));
		assertEquals(TARGET_B, targetMap.get(TEST_ID_B));
	}

	@Test
	public void testConvertNullMap() {
		assertNull(this.service.convertMap(null, String.class, TargetPojo.class));
		assertNull(this.service.convertMapStrictly(String.class, SourcePojo.class, null, String.class, TargetPojo.class));
	}

	@Test
	public void testConvertMapNulls() {
		Map<String, SourcePojo> sourceMap = new HashMap<>();
		sourceMap.put(TEST_ID_A, SOURCE_A);
		sourceMap.put(null, null);
		sourceMap.put(TEST_ID_B, SOURCE_B);
		assertEquals(null, this.service.convertMap(sourceMap, String.class, TargetPojo.class).get(null));
		assertEquals(TARGET_NULL, this.service.convertMapStrictly(String.class, SourcePojo.class, sourceMap, String.class, TargetPojo.class).get(null));
	}
	
	@Test
	public void testConvertIntoMap() {
		Map<String, TargetPojo> target = new HashMap<>();
		
		Map<String, SourcePojo> source1 = new HashMap<>();
		source1.put(TEST_ID_A, SOURCE_A);
		this.service.convertInto(source1, target, String.class, TargetPojo.class);
		assertEquals(1, target.size());
		assertEquals(TARGET_A, target.get(TEST_ID_A));

		Map<String, SourcePojo> source2 = new HashMap<>();
		source2.put(null, null);
		this.service.convertInto(source2, target, String.class, TargetPojo.class);
		assertEquals(2, target.size());
		assertNull(target.get(null));

		Map<String, SourcePojo> source3 = new HashMap<>();
		source3.put(null, null);
		this.service.convertStrictlyInto(String.class, SourcePojo.class, source3, target, String.class, TargetPojo.class);
		assertEquals(2, target.size());
		assertEquals(TARGET_NULL, target.get(null));
	}
}
