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

import java.util.Iterator;

public class Accumulation<T extends Number & Comparable<T>> implements Filter<T> {
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

	public Accumulation(Iterable<T> data) {
		this.data = data;
	}

	@Override
	public Iterator<Double> iterator() {
		return new AccumulationIterator<>(data.iterator());
	}
}
