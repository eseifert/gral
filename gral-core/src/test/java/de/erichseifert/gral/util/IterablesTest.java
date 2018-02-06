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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import org.junit.Test;

import org.hamcrest.CoreMatchers;

public class IterablesTest {
	@Test
	public void testConcatenateReturnsIterableContainingAllElementsOfTheSpecifiedIterables() {
		Iterable<Object> someIterable = Arrays.<Object>asList(1, 2, 3);
		Iterable<Object> emptyIterable = Arrays.asList();
		Iterable<Object> anotherIterable = Arrays.<Object>asList(3, 2, 1);

		Iterable<Object> concatenatedIterable = Iterables.concatenate(someIterable, emptyIterable, anotherIterable);

		assertThat(concatenatedIterable, CoreMatchers.<Object>hasItems(1, 2, 3, 3, 2, 1));
	}

	@Test
	public void testTakeReturnsTheFirstNElementsOfAnIterable() {
		Iterable<Integer> someIterable = Arrays.asList(1, 2, 3, 4, 5);

		Iterable<Integer> firstElements = Iterables.take(someIterable, 3);

		assertThat(firstElements, hasItems(1, 2, 3));
	}

	@Test
	public void testTakeReturnsNoMoreThanNElements() {
		int elementsToTake = 3;
		Iterable<Integer> someIterable = Arrays.asList(1, 2, 3, 4, 5);

		Iterable<Integer> firstElements = Iterables.take(someIterable, elementsToTake);

		int takenElements = 0;
		for (Integer element : firstElements) {
			takenElements++;
		}
		assertThat(takenElements, is(elementsToTake));
	}
}
