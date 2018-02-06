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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import org.junit.Test;

import org.hamcrest.CoreMatchers;

public class WindowIteratorTest {
	@Test
	public void testNextReturnsIterables() {
		int windowSize = 3;
		Iterable<Object> iterable = Arrays.<Object>asList(0, 1, 2, 3, 4, 5);
		WindowIterator<Object> windowIterator = new WindowIterator<>(iterable.iterator(), windowSize);

		assertThat(windowIterator.next(), CoreMatchers.<Object>hasItems(0, 1, 2));
		assertThat(windowIterator.next(), CoreMatchers.<Object>hasItems(1, 2, 3));
		assertThat(windowIterator.next(), CoreMatchers.<Object>hasItems(2, 3, 4));
		assertThat(windowIterator.next(), CoreMatchers.<Object>hasItems(3, 4, 5));
	}

	@Test
	public void testHasNextReturnsFalseWhenEndOfInputIsReached() {
		int windowSize = 3;
		Iterable<Object> iterable = Arrays.<Object>asList(0, 1, 2);
		WindowIterator<Object> windowIterator = new WindowIterator<>(iterable.iterator(), windowSize);
		windowIterator.next();

		boolean hasNext = windowIterator.hasNext();

		assertThat(hasNext, is(false));
	}

	@Test
	public void testHasNextReturnsTrueWhenInputHasRemainingItems() {
		int windowSize = 3;
		Iterable<Object> iterable = Arrays.<Object>asList(0, 1, 2, 3);
		WindowIterator<Object> windowIterator = new WindowIterator<>(iterable.iterator(), windowSize);

		boolean hasNext = windowIterator.hasNext();

		assertThat(hasNext, is(true));
	}
}
