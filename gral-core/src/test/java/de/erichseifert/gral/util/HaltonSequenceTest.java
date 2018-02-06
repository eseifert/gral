/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class HaltonSequenceTest {
	private static final double DELTA = 1e-15;

	@Test
	public void testHasNext() {
		HaltonSequence seq = new HaltonSequence();
		for (int i = 0; i < 20; i++) {
			assertTrue(seq.hasNext());
		}
	}

	@Test
	public void testBase2() {
		HaltonSequence seq = new HaltonSequence();
		assertEquals(0.5000, seq.next(), DELTA);
		assertEquals(0.2500, seq.next(), DELTA);
		assertEquals(0.7500, seq.next(), DELTA);
		assertEquals(0.1250, seq.next(), DELTA);
		assertEquals(0.6250, seq.next(), DELTA);
		assertEquals(0.3750, seq.next(), DELTA);
		assertEquals(0.8750, seq.next(), DELTA);
		assertEquals(0.0625, seq.next(), DELTA);
		assertEquals(0.5625, seq.next(), DELTA);
		assertEquals(0.3125, seq.next(), DELTA);
	}

	@Test
	public void testBase3() {
		HaltonSequence seq = new HaltonSequence(3);
		assertEquals(0.3333333333333333, seq.next(), DELTA);
		assertEquals(0.6666666666666666, seq.next(), DELTA);
		assertEquals(0.1111111111111111, seq.next(), DELTA);
		assertEquals(0.4444444444444444, seq.next(), DELTA);
		assertEquals(0.7777777777777777, seq.next(), DELTA);
		assertEquals(0.2222222222222222, seq.next(), DELTA);
		assertEquals(0.5555555555555556, seq.next(), DELTA);
		assertEquals(0.8888888888888888, seq.next(), DELTA);
		assertEquals(0.0370370370370370, seq.next(), DELTA);
		assertEquals(0.3703703703703703, seq.next(), DELTA);
	}

	@Test
	public void testBase5() {
		HaltonSequence seq = new HaltonSequence(5);
		assertEquals(0.20, seq.next(), DELTA);
		assertEquals(0.40, seq.next(), DELTA);
		assertEquals(0.60, seq.next(), DELTA);
		assertEquals(0.80, seq.next(), DELTA);
		assertEquals(0.04, seq.next(), DELTA);
		assertEquals(0.24, seq.next(), DELTA);
		assertEquals(0.44, seq.next(), DELTA);
		assertEquals(0.64, seq.next(), DELTA);
		assertEquals(0.84, seq.next(), DELTA);
		assertEquals(0.08, seq.next(), DELTA);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		HaltonSequence original = new HaltonSequence(3);
		HaltonSequence deserialized = TestUtils.serializeAndDeserialize(original);

		for (int i = 0; i < 10; i++) {
			assertEquals(original.next(), deserialized.next(), DELTA);
		}
	}
}
