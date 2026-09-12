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
package de.erichseifert.gral.data.filters;

import java.util.Iterator;

/**
 * <p>A {@link Filter} that replaces each value by the sum of it and all values
 * before it, i.e. a running total.</p>
 *
 * <pre>
 * for (double sum : new Accumulation&lt;&gt;(Arrays.asList(3.0, 8.0, 5.0))) {
 *     // 3.0, 11.0, 16.0
 * }
 * </pre>
 *
 * <p>In contrast to the window-based filters this one produces exactly as many
 * values as it consumes, and it computes them lazily while the result is
 * iterated. Values are summed as {@code double}, so the usual rounding of
 * floating-point addition applies to long sequences.</p>
 *
 * @param <T> Type of the values being accumulated.
 */
public class Accumulation<T extends Number & Comparable<T>> implements Filter<T> {
	/** Values to be accumulated. */
	private final Iterable<T> data;

	private static class AccumulationIterator<U extends Number> implements Iterator<Double> {
		private final Iterator<U> wrappedIterator;
		private double accumulatedValue;

		public AccumulationIterator(Iterator<U> wrappedIterator) {
			this.wrappedIterator = wrappedIterator;
			accumulatedValue = 0.0;
		}

		@Override
		public boolean hasNext() {
			return wrappedIterator.hasNext();
		}

		@Override
		public Double next() {
			accumulatedValue += wrappedIterator.next().doubleValue();
			return accumulatedValue;
		}

		@Override
		public void remove() {
			wrappedIterator.remove();
		}
	}

	/**
	 * Initializes a new filter over the specified values. The source is not
	 * copied, so each traversal of this filter traverses the source again.
	 * @param data Values to be accumulated.
	 */
	public Accumulation(Iterable<T> data) {
		this.data = data;
	}

	@Override
	public Iterator<Double> iterator() {
		return new AccumulationIterator<>(data.iterator());
	}
}
