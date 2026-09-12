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
package de.erichseifert.gral.plots.colors;

import java.awt.Paint;

/**
 * <p>Maps a number to a {@code java.awt.Paint}. This is what lets the
 * appearance of an element depend on its value: the color of a point, the fill
 * of a bar, the cell of a raster plot.</p>
 *
 * <pre>
 * HeatMap colors = new HeatMap();
 * colors.setRange(0.0, 100.0);
 * pointRenderer.setColor(colors);
 * </pre>
 *
 * <p>Implementations extend one of the two abstract subclasses rather than this
 * interface directly: {@link ContinuousColorMapper} for gradients over a
 * {@code double}, and {@link IndexedColorMapper} for a palette addressed by an
 * {@code int}.</p>
 *
 * <p>A mapper normally covers a limited range of input values, and its
 * {@link Mode} decides what happens outside it. In {@link Mode#OMIT} mode
 * {@link #get(Number)} returns {@code null}, and the element is not drawn at
 * all &mdash; callers have to be prepared for that.</p>
 */
public interface ColorMapper {
	/** How values outside of the mapping range are handled. */
	enum Mode {
		/**	Return {@code null}, so that the element is not drawn. */
		OMIT,
		/**	Clamp to the nearest end of the range. */
		REPEAT,
		/**	Wrap around, so that the range repeats periodically. */
		CIRCULAR
	}

	/**
	 * Returns the paint for the specified value.
	 * @param value Numeric value.
	 * @return Paint for that value, or {@code null} if the value lies outside
	 *         the mapping range and the mode is {@link Mode#OMIT}.
	 */
	Paint get(Number value);

	/**
	 * Returns how values outside of the mapping range will be handled.
	 * @return Handling of values outside of the mapping range.
	 */
	Mode getMode();
}
