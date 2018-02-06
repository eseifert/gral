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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class MedianFilterTest {
	@Test
	public void testReturnsMedianValueWithinFilterWindow() {
		int windowsSize = 3;
		Iterable<Integer> data = Arrays.asList(3, 8, 5, 6, 4, 9);

		MedianFilter<Integer> medianFilter = new MedianFilter<>(data, windowsSize);

		assertThat(medianFilter, hasItems(5.0, 6.0, 5.0, 6.0));
	}

	@Test
	public void testIteratorIsEmptyWhenFilterWindowLargerThanDataToBeFiltered() {
		List<Integer> data = Arrays.asList(3, 8, 5, 6, 4, 9);
		int windowSize = data.size() + 1;
		MedianFilter<Integer> medianFilter = new MedianFilter<>(data, windowSize);

		boolean hasNext = medianFilter.iterator().hasNext();

		assertThat(hasNext, is(false));
	}
}
