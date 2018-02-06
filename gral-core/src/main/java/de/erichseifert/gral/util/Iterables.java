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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Iterables {
	private static class ConcatenationIterable<T> implements Iterable<T> {
		private final Iterable<Iterable<T>> inputIterables;

		public ConcatenationIterable(Iterable<Iterable<T>> inputIterables) {
			this.inputIterables = inputIterables;
		}

		@Override
		public Iterator<T> iterator() {
			List<Iterator<T>> iterators = new LinkedList<>();
			for (Iterable<T> iterable : inputIterables) {
				iterators.add(iterable.iterator());
			}
			@SuppressWarnings("unchecked")
			Iterator<T> iterator = new ConcatenationIterator<T>(iterators.toArray(new Iterator[0]));
			return iterator;
		}
	}

	public static <T> Iterable<T> concatenate(Iterable<T>... iterables) {
		return new ConcatenationIterable<>(Arrays.asList(iterables));
	}

	private static class LengthIterator<T> implements Iterator<T> {
		private final Iterator<T> inputIterator;
		private final int maxElementCount;
		private int retrievedElementCount;

		public LengthIterator(Iterator<T> inputIterator, int elementCount) {
			this.inputIterator = inputIterator;
			this.maxElementCount = elementCount;
		}

		@Override
		public boolean hasNext() {
			return retrievedElementCount < maxElementCount && inputIterator.hasNext();
		}

		@Override
		public T next() {
			retrievedElementCount++;
			return inputIterator.next();
		}

		@Override
		public void remove() {
			inputIterator.remove();
		}
	}

	public static <T> Iterable<T> take(final Iterable<T> iterable, final int elementCount) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new LengthIterator<>(iterable.iterator(), elementCount);
			}
		};
	}
}
