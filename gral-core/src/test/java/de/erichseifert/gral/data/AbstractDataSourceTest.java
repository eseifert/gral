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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.data.statistics.Statistics;

public class AbstractDataSourceTest {
	protected class StubAbstractDataSource extends AbstractDataSource {
		private int colCount;
		private int rowCount;

		public StubAbstractDataSource() {
		}

		public StubAbstractDataSource(int colCount, int rowCount) {
			this.colCount = colCount;
			this.rowCount = rowCount;
			Class<? extends Comparable<?>>[] columnTypes = new Class[colCount];
			Arrays.fill(columnTypes, Comparable.class);
			setColumnTypes(columnTypes);
		}

		public StubAbstractDataSource(String name) {
			super(name);
		}

		@Override
		public Comparable<?> get(int col, int row) {
			return null;
		}

		@Override
		public int getRowCount() {
			return rowCount;
		}

		@Override
		public int getColumnCount() {
			return colCount;
		}
	}

	private AbstractDataSource source;

	@Before
	public void setUp() {
		source = new StubAbstractDataSource();
	}

	@Test
	public void testGetName() {
		assertEquals(null, source.getName());

		source = new StubAbstractDataSource("name");
		assertEquals("name", source.getName());
	}

	@Test
	public void testColumnStatisticsContainsColumnForEachColumnInDataSource() {
		int columnCount = 3;
		source = new StubAbstractDataSource(columnCount, 0);
		DataSource columnStatistics = source.getColumnStatistics(Statistics.N);
		assertThat(columnStatistics.getColumnCount(), is(source.getColumnCount()));
	}

	@Test
	public void testColumnStatisticsForMultiColumnDataSourceContainsSingleRow() {
		int columnCount = 5;
		source = new StubAbstractDataSource(columnCount, 0);
		DataSource columnStatistics = source.getColumnStatistics(Statistics.N);
		assertThat(columnStatistics.getRowCount(), is(1));
	}

	@Test
	public void testColumnStatisticsForEmptyDataSourceContainsNoRow() {
		DataSource columnStatistics = source.getColumnStatistics(Statistics.N);
		assertThat(columnStatistics.getRowCount(), is(0));
	}

	@Test
	public void testRowStatisticsForEmptyDataSourceContainsNoColumn() {
		DataSource rowStatistics = source.getRowStatistics(Statistics.N);
		assertThat(rowStatistics.getColumnCount(), is(0));
	}

	@Test
	public void testRowStatisticsForDataSourceContainsSingleColumn() {
		source = new StubAbstractDataSource(5, 3);
		DataSource rowStatistics = source.getRowStatistics(Statistics.N);
		assertThat(rowStatistics.getColumnCount(), is(1));
	}

	@Test
	public void testRowStatisticsForDataSourceContainsRowForEachRowInDataSource() {
		int rowCount = 3;
		source = new StubAbstractDataSource(5, rowCount);
		DataSource rowStatistics = source.getRowStatistics(Statistics.N);
		assertThat(rowStatistics.getRowCount(), is(rowCount));
	}
}