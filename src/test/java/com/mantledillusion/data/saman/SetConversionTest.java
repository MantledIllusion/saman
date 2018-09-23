package com.mantledillusion.data.saman;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

import static org.junit.Assert.*;

public class SetConversionTest extends AbstractConversionTest {

	@Test
	public void testConvertSet() {
		Set<TargetPojo> targetSet = this.service.processSet(new HashSet<>(Arrays.asList(SOURCE_A, SOURCE_B)), TargetPojo.class);
		assertEquals(2, targetSet.size());
		assertTrue(targetSet.contains(TARGET_A));
		assertTrue(targetSet.contains(TARGET_B));
	}

	@Test
	public void testConvertNullLSet() {
		assertNull(this.service.processSet(null, TargetPojo.class));
		assertNull(this.service.processSetStrictly(SourcePojo.class, null, TargetPojo.class));
	}

	@Test
	public void testConvertSetNulls() {
		assertTrue(this.service.processSet(new HashSet<>(Arrays.asList(SOURCE_A, null, SOURCE_B)), TargetPojo.class).contains(null));
		assertFalse(this.service.processSetStrictly(SourcePojo.class, new HashSet<>(Arrays.asList(SOURCE_A, null, SOURCE_B)), TargetPojo.class).contains(null));
	}
	
	@Test
	public void testConvertIntoSet() {
		Set<TargetPojo> target = new HashSet<>();
		
		this.service.processInto(Arrays.asList(SOURCE_A), target, TargetPojo.class);
		assertEquals(1, target.size());
		assertEquals(TARGET_A, target.iterator().next());
		
		this.service.processInto(Arrays.asList((SourcePojo) null), target, TargetPojo.class);
		assertEquals(2, target.size());
		assertTrue(target.contains(null));
		
		this.service.processStrictlyInto(SourcePojo.class, Arrays.asList((SourcePojo) null), target, TargetPojo.class);
		assertEquals(3, target.size());
		assertTrue(target.contains(TARGET_NULL));
	}
}
