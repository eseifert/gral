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

import java.awt.Paint;

/**
 * Basic interface for classes that map numbers to Paint objects. This can be
 * used to generate colors or gradients for various elements in a plot, e.g.
 * points, lines, areas, etc.
 *
 * {@link ContinuousColorMapper} or {@link IndexedColorMapper} should be used
 * as base classes in most cases.
 */
public interface ColorMapper {
	/** Data type to define how values outside of the mapping range will be
	handled. */
	enum Mode {
		/**	Ignore missing values. */
		OMIT,
		/**	Repeat the last value. */
		REPEAT,
		/**	Repeat the data. */
		CIRCULAR
	}

	/**
	 * Returns the Paint object according to the specified value.
	 * @param value Numeric value.
	 * @return Paint object.
	 */
	Paint get(Number value);

	/**
	 * Returns how values outside of the mapping range will be handled.
	 * @return Handling of values outside of the mapping range.
	 */
	Mode getMode();
}
