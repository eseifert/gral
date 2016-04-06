/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2016 Erich Seifert <dev[at]erichseifert.de>,
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

import de.erichseifert.gral.data.statistics.Statistics;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AbstractDataSourceTest {
	protected class StubAbstractDataSource extends AbstractDataSource {
		public StubAbstractDataSource() {
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
			return 0;
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
	public void testStatisticsNotNull() {
		DataSource rowStatistics = source.getStatistics(Row.class, Statistics.N);
		assertThat(rowStatistics, notNullValue());

		DataSource columnStatistics = source.getStatistics(Column.class, Statistics.N);
		assertThat(columnStatistics, notNullValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThrowsExceptionWhenOrientationUnknown() {
		source.getStatistics(DataAccessor.class, Statistics.N);
	}
}