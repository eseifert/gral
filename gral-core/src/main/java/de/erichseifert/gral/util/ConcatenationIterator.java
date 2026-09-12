/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>An iterator that visits the elements of several other iterators, one
 * source after the other.</p>
 *
 * <pre>
 * Iterator&lt;String&gt; all = new ConcatenationIterator&lt;&gt;(
 *     first.iterator(), second.iterator());
 * </pre>
 *
 * <p>{@link #remove()} is a no-op rather than an exception. That is deliberate:
 * it predates Java 8, where {@code Iterator.remove} gained a throwing default,
 * and removing the override would turn a silent no-op into an
 * {@code UnsupportedOperationException} for existing callers.</p>
 *
 * @param <T> Type of the elements.
 */
public class ConcatenationIterator<T> implements Iterator<T> {
	/** Sources to be visited, in order. */
	private final Iterator<T>[] inputIterators;

	/**
	 * Initializes a new iterator over the specified sources. The array is
	 * copied, but the iterators in it are used directly.
	 * @param inputIterators Sources to be visited in the given order.
	 */
	public ConcatenationIterator(Iterator<T>... inputIterators) {
		this.inputIterators = Arrays.copyOf(inputIterators, inputIterators.length);
	}

	@Override
	public boolean hasNext() {
		for (Iterator<T> inputIterator : inputIterators) {
			if (inputIterator.hasNext()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public T next() {
		for (Iterator<T> inputIterator : inputIterators) {
			if (inputIterator.hasNext()) {
				return inputIterator.next();
			}
		}
		throw new NoSuchElementException("No elements left in concatenated iterator.");
	}

	/**
	 * Does nothing. See the class documentation for why this is not an
	 * {@code UnsupportedOperationException}.
	 */
	@Override
	public void remove() {
	}
}
