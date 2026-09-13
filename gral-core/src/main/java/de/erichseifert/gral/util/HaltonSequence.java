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

import java.io.Serializable;
import java.util.Iterator;

/**
 * <p>An endless iterator over the Halton sequence: a quasi-random sequence of
 * values in the interval [0,&nbsp;1) that fills the interval more evenly than
 * independent random numbers would, because every new value avoids the gaps
 * left by the previous ones.</p>
 *
 * <pre>
 * Iterator&lt;Double&gt; sequence = new HaltonSequence(2);
 * // 0.5, 0.25, 0.75, 0.125, 0.625, …
 * </pre>
 *
 * <p>That property is what makes it useful for choosing colors:
 * {@link de.erichseifert.gral.plots.colors.QuasiRandomColors} draws hues from
 * this sequence so that series added one after another stay easy to tell
 * apart.</p>
 *
 * <p>{@link #hasNext()} is always {@code true}; the internal counter wraps
 * around after {@code Long.MAX_VALUE} steps and the sequence starts over.
 * Different bases give different sequences, and a base that is a prime number
 * gives the best spread.</p>
 */
public class HaltonSequence implements Iterator<Double>, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 7466395251522942013L;

	/** Base. */
	private final int base;
	/** Current count. */
	private long c;

	/**
	 * Creates a new sequence to the base of two.
	 */
	public HaltonSequence() {
		this(2);
	}

	/**
	 * Creates a new sequence with the specified base. Prime numbers give the
	 * most even spread.
	 * @param base Base value, greater than one.
	 */
	public HaltonSequence(int base) {
		this.base = base;
	}

    /**
     * Returns whether the iteration has more elements. This means it returns
     * {@code true} if {@code next} would return an element rather
     * than throwing an exception.
     * @return {@code true} if the iterator has more elements.
     */
	public boolean hasNext() {
		return true;
	}

    /**
     * Returns the next element in the iteration.
     * @return the next element in the iteration.
     */
	public Double next() {
		if (++c == Long.MAX_VALUE) {
			c = 0;
		}
		return get(c - 1);
	}

	/**
	 * Returns the element at the specified position of the sequence, counted
	 * from zero. In contrast to {@link #next()} this does not depend on how
	 * often the sequence has been queried before, so the same position always
	 * yields the same value.
	 * @param index Position in the sequence.
	 * @return Element at the specified position.
	 */
	public Double get(long index) {
		long i = index + 1;
		double h = 0.0;
		double step = 1.0 / base;

		while (i > 0) {
			long digit = i % base;
			h += digit * step;
			i = (i - digit) / base;
			step /= base;
		}

		return h;
	}

	/**
	 * Does nothing. A sequence has no elements to remove; this is a no-op
	 * rather than an exception because it predates the throwing default that
	 * {@code Iterator.remove} gained in Java 8.
	 */
	public void remove() {
	}

}
