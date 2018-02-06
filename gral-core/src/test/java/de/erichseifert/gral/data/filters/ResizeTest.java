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
package de.erichseifert.gral.data.filters;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;

public class ResizeTest {
	private static final double DELTA = 1e-15;

	private static DataTable dataEmpty;
	private static DataTable dataHorizontal;
	private static DataTable dataVertical;
	private static DataTable dataDiagonal;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		dataEmpty = new DataTable(Double.class, Double.class, Double.class);
		dataEmpty.add(0.0, 0.0, 0.0); // 0
		dataEmpty.add(0.0, 0.0, 0.0); // 1
		dataEmpty.add(0.0, 0.0, 0.0); // 2

		dataHorizontal = new DataTable(Double.class, Double.class, Double.class);
		dataHorizontal.add(0.0, 0.0, 0.0); // 0
		dataHorizontal.add(1.0, 1.0, 1.0); // 1
		dataHorizontal.add(0.0, 0.0, 0.0); // 2

		dataVertical = new DataTable(Double.class, Double.class, Double.class);
		dataVertical.add(0.0, 1.0, 0.0); // 0
		dataVertical.add(0.0, 1.0, 0.0); // 1
		dataVertical.add(0.0, 1.0, 0.0); // 2

		dataDiagonal = new DataTable(Double.class, Double.class, Double.class);
		dataDiagonal.add(1.0, 0.0, 0.0); // 0
		dataDiagonal.add(0.0, 1.0, 0.0); // 1
		dataDiagonal.add(0.0, 0.0, 1.0); // 2
	}

	private void assertFiltered(DataSource data, int cols, int rows, double[] expected) {
		Resize filter = new Resize(data, cols, rows);
		assertEquals((cols > 0) ? cols : data.getColumnCount(), filter.getColumnCount());
		assertEquals((rows > 0) ? rows : data.getRowCount(), filter.getRowCount());
		for (int i = 0; i < expected.length; i++) {
			int col = i % filter.getColumnCount();
			int row = i / filter.getColumnCount();
			assertEquals(expected[i], ((Number) filter.get(col, row)).doubleValue(), DELTA);
		}
	}

	private void assertIdentity(DataSource data) {
		assertFiltered(data, data.getColumnCount(), data.getRowCount(), new double[] {
			((Number) data.get(0, 0)).doubleValue(), ((Number) data.get(1, 0)).doubleValue(), ((Number) data.get(2, 0)).doubleValue(),
			((Number) data.get(0, 1)).doubleValue(), ((Number) data.get(1, 1)).doubleValue(), ((Number) data.get(2, 1)).doubleValue(),
			((Number) data.get(0, 2)).doubleValue(), ((Number) data.get(1, 2)).doubleValue(), ((Number) data.get(2, 2)).doubleValue(),
		});
	}

	@Test
	public void testIdentity() {
		assertIdentity(dataEmpty);
		assertIdentity(dataHorizontal);
		assertIdentity(dataVertical);
		assertIdentity(dataDiagonal);
	}

	@Test
	public void testHorizontal() {
		assertFiltered(dataEmpty, 1, 0, new double[] {
			0.0/3.0,
			0.0/3.0,
			0.0/3.0
		});
		assertFiltered(dataHorizontal, 1, 0, new double[] {
			0.0/3.0,
			3.0/3.0,
			0.0/3.0
		});
		assertFiltered(dataVertical, 1, 0, new double[] {
			1.0/3.0,
			1.0/3.0,
			1.0/3.0
		});
		assertFiltered(dataDiagonal, 1, 0, new double[] {
			1.0/3.0,
			1.0/3.0,
			1.0/3.0
		});
	}

	@Test
	public void testVertical() {
		assertFiltered(dataEmpty, 0, 1, new double[] {
			0.0/3.0, 0.0/3.0, 0.0/3.0
		});
		assertFiltered(dataHorizontal, 0, 1, new double[] {
			1.0/3.0, 1.0/3.0, 1.0/3.0
		});
		assertFiltered(dataVertical, 0, 1, new double[] {
			0.0/3.0, 3.0/3.0, 0.0/3.0
		});
		assertFiltered(dataDiagonal, 0, 1, new double[] {
			1.0/3.0, 1.0/3.0, 1.0/3.0
		});
	}

	@Test
	public void testResize() {
		assertFiltered(dataEmpty, 0, 1, new double[] {
				0.0/9.0
			});
			assertFiltered(dataHorizontal, 1, 1, new double[] {
				3.0/9.0
			});
			assertFiltered(dataVertical, 1, 1, new double[] {
				3.0/9.0
			});
			assertFiltered(dataDiagonal, 1, 1, new double[] {
				3.0/9.0
			});
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Filter2D original = new Resize(dataDiagonal, 2, 2);
		Filter2D deserialized = TestUtils.serializeAndDeserialize(original);

    	// Test metadata
    	assertEquals(original.getMode(), deserialized.getMode());
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
