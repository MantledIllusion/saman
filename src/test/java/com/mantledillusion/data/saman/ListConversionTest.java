package com.mantledillusion.data.saman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

import static org.junit.Assert.*;

public class ListConversionTest extends AbstractConversionTest {

	@Test
	public void testConvertList() {
		List<TargetPojo> targetList = this.service.processList(Arrays.asList(SOURCE_A, SOURCE_B), TargetPojo.class);
		assertEquals(targetList.size(), 2);
		assertEquals(targetList.get(0).id, TEST_ID_A);
		assertEquals(targetList.get(1).id, TEST_ID_B);
	}

	@Test
	public void testConvertNullList() {
		assertNull(this.service.processList(null, TargetPojo.class));
		assertNull(this.service.processListStrictly(SourcePojo.class, null, TargetPojo.class));
	}

	@Test
	public void testConvertListNulls() {
		assertNull(this.service.processList(Arrays.asList(SOURCE_A, null, SOURCE_B), TargetPojo.class).get(1));
		assertEquals(TARGET_NULL, this.service.processListStrictly(SourcePojo.class, Arrays.asList(SOURCE_A, null, SOURCE_B), TargetPojo.class).get(1));
	}
	
	@Test
	public void testConvertIntoList() {
		List<TargetPojo> target = new ArrayList<>();
		
		this.service.processInto(Arrays.asList(SOURCE_A), target, TargetPojo.class);
		assertEquals(1, target.size());
		assertEquals(TARGET_A, target.get(0));
		
		this.service.processInto(Arrays.asList((SourcePojo) null), target, TargetPojo.class);
		assertEquals(2, target.size());
		assertNull(target.get(1));
		
		this.service.processStrictlyInto(SourcePojo.class, Arrays.asList((SourcePojo) null), target, TargetPojo.class);
		assertEquals(3, target.size());
		assertEquals(TARGET_NULL, target.get(2));
	}
}
