package com.mantledillusion.data.saman;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

import org.junit.jupiter.api.Assertions;

public class SetConversionTest extends AbstractConversionTest {

	@Test
	public void testConvertSet() {
		Set<TargetPojo> targetSet = this.service.processSet(new HashSet<>(Arrays.asList(SOURCE_A, SOURCE_B)), TargetPojo.class);
		Assertions.assertEquals(2, targetSet.size());
		Assertions.assertTrue(targetSet.contains(TARGET_A));
		Assertions.assertTrue(targetSet.contains(TARGET_B));
	}

	@Test
	public void testConvertNullLSet() {
		Assertions.assertNull(this.service.processSet(null, TargetPojo.class));
		Assertions.assertNull(this.service.processSetStrictly(SourcePojo.class, null, TargetPojo.class));
	}

	@Test
	public void testConvertSetNulls() {
		Assertions.assertTrue(this.service.processSet(new HashSet<>(Arrays.asList(SOURCE_A, null, SOURCE_B)), TargetPojo.class).contains(null));
		Assertions.assertFalse(this.service.processSetStrictly(SourcePojo.class, new HashSet<>(Arrays.asList(SOURCE_A, null, SOURCE_B)), TargetPojo.class).contains(null));
	}
	
	@Test
	public void testConvertIntoSet() {
		Set<TargetPojo> target = new HashSet<>();
		
		this.service.processInto(Arrays.asList(SOURCE_A), target, TargetPojo.class);
		Assertions.assertEquals(1, target.size());
		Assertions.assertEquals(TARGET_A, target.iterator().next());
		
		this.service.processInto(Arrays.asList((SourcePojo) null), target, TargetPojo.class);
		Assertions.assertEquals(2, target.size());
		Assertions.assertTrue(target.contains(null));
		
		this.service.processStrictlyInto(SourcePojo.class, Arrays.asList((SourcePojo) null), target, TargetPojo.class);
		Assertions.assertEquals(3, target.size());
		Assertions.assertTrue(target.contains(TARGET_NULL));
	}

	@Test
	public void testAlignIntoSet() {
		Set<TargetPojo> target = new HashSet<>();

		this.service.processIntoAligning(Arrays.asList(SOURCE_A), target, TargetPojo.class, (s, t) -> Objects.equals(s.id, t.id));
		Assertions.assertEquals(1, target.size());
		TargetPojo targetA = target.iterator().next();
		Assertions.assertEquals(TARGET_A, targetA);

		this.service.processIntoAligning(Arrays.asList(SOURCE_A), target, TargetPojo.class, (s, t) -> Objects.equals(s.id, t.id));
		Assertions.assertEquals(1, target.size());
		Assertions.assertEquals(TARGET_A, target.iterator().next());
		Assertions.assertSame(targetA, target.iterator().next());

		this.service.processIntoAligning(Arrays.asList(SOURCE_B), target, TargetPojo.class, (s, t) -> Objects.equals(s.id, t.id));
		Assertions.assertEquals(1, target.size());
		Assertions.assertEquals(TARGET_B, target.iterator().next());
	}
}
