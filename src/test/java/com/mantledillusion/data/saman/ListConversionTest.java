package com.mantledillusion.data.saman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.mantledillusion.data.saman.obj.SourcePojo;
import com.mantledillusion.data.saman.obj.TargetPojo;

public class ListConversionTest extends AbstractConversionTest {

	@Test
	public void testConvertList() {
		List<TargetPojo> targetList = this.service.processList(Arrays.asList(SOURCE_A, SOURCE_B), TargetPojo.class);
		Assertions.assertEquals(targetList.size(), 2);
		Assertions.assertEquals(targetList.get(0).id, TEST_ID_A);
		Assertions.assertEquals(targetList.get(1).id, TEST_ID_B);
	}

	@Test
	public void testConvertNullList() {
		Assertions.assertNull(this.service.processList(null, TargetPojo.class));
		Assertions.assertNull(this.service.processListStrictly(SourcePojo.class, null, TargetPojo.class));
	}

	@Test
	public void testConvertListNulls() {
		Assertions.assertNull(this.service.processList(Arrays.asList(SOURCE_A, null, SOURCE_B), TargetPojo.class).get(1));
		Assertions.assertEquals(TARGET_NULL, this.service.processListStrictly(SourcePojo.class, Arrays.asList(SOURCE_A, null, SOURCE_B), TargetPojo.class).get(1));
	}
	
	@Test
	public void testConvertIntoList() {
		List<TargetPojo> target = new ArrayList<>();
		
		this.service.processInto(Arrays.asList(SOURCE_A), target, TargetPojo.class);
		Assertions.assertEquals(1, target.size());
		Assertions.assertEquals(TARGET_A, target.get(0));
		
		this.service.processInto(Arrays.asList((SourcePojo) null), target, TargetPojo.class);
		Assertions.assertEquals(2, target.size());
		Assertions.assertNull(target.get(1));
		
		this.service.processStrictlyInto(SourcePojo.class, Arrays.asList((SourcePojo) null), target, TargetPojo.class);
		Assertions.assertEquals(3, target.size());
		Assertions.assertEquals(TARGET_NULL, target.get(2));
	}

	@Test
	public void testAlignIntoList() {
		List<TargetPojo> target = new ArrayList<>();

		this.service.processIntoAligning(Arrays.asList(SOURCE_A), target, TargetPojo.class, (s, t) -> Objects.equals(s.id, t.id));
		Assertions.assertEquals(1, target.size());
		TargetPojo targetA = target.get(0);
		Assertions.assertEquals(TARGET_A, targetA);

		this.service.processIntoAligning(Arrays.asList(SOURCE_A), target, TargetPojo.class, (s, t) -> Objects.equals(s.id, t.id));
		Assertions.assertEquals(1, target.size());
		Assertions.assertEquals(TARGET_A, target.get(0));
		Assertions.assertSame(targetA, target.get(0));

		this.service.processIntoAligning(Arrays.asList(SOURCE_B), target, TargetPojo.class, (s, t) -> Objects.equals(s.id, t.id));
		Assertions.assertEquals(1, target.size());
		Assertions.assertEquals(TARGET_B, target.get(0));
	}
}
