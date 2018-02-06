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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Test;

import org.hamcrest.CoreMatchers;

public class ConcatenationIteratorTest {
	@Test
	public void testHasNextReturnsFalseWhenAllInputIteratorAreEmpty() {
		Iterator<Object> emptyIterator = Arrays.asList().iterator();
		Iterator<Object> emptyIterator2 = Arrays.asList().iterator();
		ConcatenationIterator<Object> concatenationIterator = new ConcatenationIterator<>(emptyIterator, emptyIterator2);

		boolean hasNext = concatenationIterator.hasNext();

		assertFalse(hasNext);
	}

	@Test
	public void testHasNextReturnsTrueWhenAnyInputIteratorHasRemainingElements() {
		Iterator<Object> emptyIterator = Arrays.asList().iterator();
		Iterator<Object> filledIterator = Arrays.<Object>asList(1, 2, 3).iterator();
		Iterator<Object> emptyIterator2 = Arrays.asList().iterator();
		ConcatenationIterator<Object> concatenationIterator = new ConcatenationIterator<>(emptyIterator, filledIterator, emptyIterator2);

		boolean hasNext = concatenationIterator.hasNext();

		assertTrue(hasNext);
	}

	@Test
	public void testNextAdvancesTheUnderlyingIterator() {
		Iterator<Object> iterator = Arrays.<Object>asList(1, 2, 3).iterator();
		ConcatenationIterator<Object> concatenatedIterator = new ConcatenationIterator<>(iterator);

		Object firstElement = concatenatedIterator.next();
		Object secondElement = concatenatedIterator.next();

		assertThat(firstElement, is(not(secondElement)));
	}

	@Test
	public void testNextReturnsTheFirstElementOfTheFirstNonEmptyIterator() {
		Iterator<Object> emptyIterator = Arrays.asList().iterator();
		Iterator<Object> nonEmptyIterator = Arrays.<Object>asList(1, 2).iterator();
		Iterator<Object> anotherNonEmptyIterator = Arrays.<Object>asList(3, 4).iterator();
		ConcatenationIterator<Object> concatenatedIterator = new ConcatenationIterator<>(emptyIterator, nonEmptyIterator, anotherNonEmptyIterator);

		Object firstElement = concatenatedIterator.next();

		assertThat(firstElement, CoreMatchers.<Object>is(1));
	}

	@Test(expected = NoSuchElementException.class)
	public void testNextThrowsExceptionWhenAllIteratorsAreEmpty() {
		Iterator<Object> emptyIterator = Arrays.asList().iterator();
		Iterator<Object> emptyIterator2 = Arrays.asList().iterator();
		ConcatenationIterator<Object> concatenatedIterator = new ConcatenationIterator<>(emptyIterator, emptyIterator2);

		concatenatedIterator.next();
	}
}
