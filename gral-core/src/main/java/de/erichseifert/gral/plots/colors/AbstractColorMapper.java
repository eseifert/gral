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
package de.erichseifert.gral.plots.colors;

import java.io.Serializable;


/**
 * Interface that maps numbers to Paint objects. This can be used to generate
 * colors or gradients for various elements in a plot, e.g. lines, areas, etc.
 *
 * @param <T> Data type of input values.
 */
public abstract class AbstractColorMapper<T extends Number>
		implements ColorMapper, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 8456831369409589441L;

	/** Handling of values that are outside the mapping range. */
	private Mode mode;

	/**
	 * Initializes a new instance with default values.
	 */
	public AbstractColorMapper() {
		mode = Mode.REPEAT;
	}

	/**
	 * Returns how values outside of the mapping range will be handled.
	 * @return Handling of values outside of the mapping range.
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Sets how values outside of the mapping range will be handled.
	 * @param mode Handling of values outside of the mapping range.
	 */
	protected void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * Transforms a value outside of the mapping range. If the value is inside
	 * the range, no transformation will be applied.
	 * @param value Value to be handled.
	 * @param rangeMin Lower bounds of range
	 * @param rangeMax Upper bounds of range
	 * @return Transformed value.
	 */
	protected abstract T applyMode(T value, T rangeMin, T rangeMax);
}
