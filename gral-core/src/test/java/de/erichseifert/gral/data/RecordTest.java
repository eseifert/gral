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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RecordTest {
	@Test
	public void testCreatableFromComparables() {
		new Record(-3.0, 1, "SomeString", null);
	}

	@Test
	public void testAllowsRetrievingValues() {
		Record record = new Record(-3.0, 1, "SomeString", null);

		assertThat(record.<Double>get(0), is(-3.0));
		assertThat(record.<Integer>get(1), is(1));
		assertThat(record.<String>get(2), is("SomeString"));
		assertThat(record.get(3), nullValue());
	}
}
