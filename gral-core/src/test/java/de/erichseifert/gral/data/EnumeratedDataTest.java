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

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.statistics.Statistics;

public class EnumeratedDataTest {
	private static final double DELTA = TestUtils.DELTA;

	private static DataTable table;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class);
		table.add(3, 1); // 0
		table.add(2, 3); // 1
		table.add(1, 2); // 2
	}

	@Test
	public void testCreate() {
		// without parameters
		EnumeratedData withoutParams = new EnumeratedData(table);
		assertEquals(table.getColumnCount() + 1, withoutParams.getColumnCount());
		assertEquals(table.getRowCount(), withoutParams.getRowCount());
		assertEquals(0.0, ((Number) withoutParams.get(0, 0)).doubleValue(), DELTA);
		assertEquals(1.0, ((Number) withoutParams.get(0, 1)).doubleValue(), DELTA);
		assertEquals(2.0, ((Number) withoutParams.get(0, 2)).doubleValue(), DELTA);

		// with parameters
		EnumeratedData withParams = new EnumeratedData(table, -1, 2.0);
		assertEquals(table.getColumnCount() + 1, withParams.getColumnCount());
		assertEquals(table.getRowCount(), withParams.getRowCount());
		assertEquals(-1.0, ((Number) withParams.get(0, 0)).doubleValue(), DELTA);
		assertEquals( 1.0, ((Number) withParams.get(0, 1)).doubleValue(), DELTA);
		assertEquals( 3.0, ((Number) withParams.get(0, 2)).doubleValue(), DELTA);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		DataSource original = new EnumeratedData(table);
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
