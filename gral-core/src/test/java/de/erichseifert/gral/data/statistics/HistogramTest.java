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
package de.erichseifert.gral.data.statistics;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

public class HistogramTest {

	@Test
	public void testHasDesiredNumberOfBins() {
		int binCount = 4;

		Histogram histogram = new Histogram(Collections.<Comparable<?>>emptyList(), binCount);

		assertThat(histogram.size(), is(4));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThrowsExceptionWhenBinCountLessThanOne() {
		int binCount = 0;

		Histogram histogram = new Histogram(Collections.<Comparable<?>>emptyList(), binCount);
	}

	@Test
	public void testBucketsContainValueCounts() {
		Iterable<Comparable<?>> data = createHistogramData();
		Histogram histogram = new Histogram(data, 4);
		assertThat(histogram, hasItems(3, 3, 0, 1));
	}

	@Test
	public void testCustomBinsContainValueCounts() {
		Iterable<Comparable<?>> data = createHistogramData();
		Histogram histogram = new Histogram(data, -1.0, 0.5, 2.0, 2.8, 5.0);
		assertThat(histogram, hasItems(0, 3, 3, 1));
	}

	@Test
	public void testGetReturnsBinSize() {
		Iterable<Comparable<?>> data = createHistogramData();
		Histogram histogram = new Histogram(data, 4);

		int binSize = histogram.get(1);

		assertThat(binSize, is(3));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThrowsExceptionWhenBreakCountLessThanTwo() {
		Iterable<Comparable<?>> data = createHistogramData();
		int lessThanTwo = 1;
		Number[] breaks = new Number[lessThanTwo];
		Arrays.fill(breaks, 2);

		new Histogram(data, breaks);
	}

	private static Iterable<Comparable<?>> createHistogramData() {
		List<Comparable<?>> data = new LinkedList<>();
		data.add(1);
		data.add(1);
		data.add(2);
		data.add(2);
		data.add(5);
		data.add(1);
		data.add(2);
		data.add(4);
		return data;
	}
}
