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
package de.erichseifert.gral.data.statistics;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import org.junit.Test;

public class HistogramTest {

	@Test
	public void testHasDesiredNumberOfBuckets() {
		int bucketCount = 4;

		Histogram histogram = new Histogram(Collections.<Comparable<?>>emptyList(), bucketCount);

		assertThat(histogram.size(), is(4));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThrowsExceptionWhenBreakCountLessThanOne() {
		int bucketCount = 0;

		Histogram histogram = new Histogram(Collections.<Comparable<?>>emptyList(), bucketCount);
	}
}
