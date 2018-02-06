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

import java.io.Serializable;
import java.util.Iterator;

/**
 * Class that calculates the values of the Halton sequence.
 */
public class HaltonSequence implements Iterator<Double>, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 7466395251522942013L;

	/** Base. */
	private final int base;
	/** Current count. */
	private long c;

	/**
	 * Creates a new HaltonSequence object to the base of two.
	 */
	public HaltonSequence() {
		this(2);
	}

	/**
	 * Creates a new instance with the specified base.
	 * @param base Base value.
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
		long i, digit;
		double h, step;

		if (++c == Long.MAX_VALUE) {
			c = 0;
		}

		i = c;
		h = 0.0;
		step = 1.0 / base;

		while (i > 0) {
			digit = i % base;
		    h += digit * step;
		    i = (i - digit) / base;
		    step /= base;
		}

		return h;
	}

	/**
	 * Stub method to fulfill {@code Iterator} interface.
	 */
	public void remove() {
	}

}
