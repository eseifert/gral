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
package de.erichseifert.gral.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.statistics.Statistics;

public class DummyDataTest {
	private static final double DELTA = TestUtils.DELTA;

	@Test
	public void testCreate() {
		Comparable<?>[] expected = {42, 1.23, "foobar" };

		for (Comparable<?> value : expected) {
			DummyData integer = new DummyData(2, 3, value);
			assertEquals(2, integer.getColumnCount());
			assertEquals(3, integer.getRowCount());
			assertEquals(integer.getColumnCount(), integer.getColumnTypes().length);
			for (Comparable<?> cell : integer) {
				assertEquals(value, cell);
			}
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		DataSource original = new DummyData(2, 3, "foobar");
		DataSource deserialized = TestUtils.serializeAndDeserialize(original);

    	// Test metadata
    	assertArrayEquals(original.getColumnTypes(), deserialized.getColumnTypes());
    	assertEquals(original.getColumnCount(), deserialized.getColumnCount());
    	assertEquals(original.getRowCount(), deserialized.getRowCount());

		// Test values
    	for (int row = 0; row < original.getRowCount(); row++) {
        	for (int col = 0; col < original.getColumnCount(); col++) {
            	assertEquals(
        			String.format("Wrong data at col=%d, row=%d.", col, row),
        			original.get(col, row), deserialized.get(col, row));
        	}
    	}

    	// Test statistics
    	String[] stats = { Statistics.N, Statistics.SUM, Statistics.MEAN, Statistics.VARIANCE };
    	for (String stat : stats) {
    		assertEquals(
				original.getStatistics().get(stat),
				deserialized.getStatistics().get(stat),
				DELTA);
		}
    }
}
