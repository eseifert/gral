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

import de.erichseifert.gral.util.MathUtils;

/**
 * <p>Base class for color mappers addressed by a whole number, i.e. palettes
 * of distinct colors rather than gradients. This is what gives consecutive
 * data series or pie slices their different colors.</p>
 *
 * <p>A subclass implements {@link #get(int)}. Input arriving through
 * {@link #get(Number)} is truncated towards zero, so a mapper of this kind can
 * be used wherever a {@link ColorMapper} is expected.</p>
 *
 * @see ContinuousColorMapper
 */
public abstract class IndexedColorMapper
		extends AbstractColorMapper<Integer> {
	/**
	 * Returns the Paint object according to the specified index.
	 * @param value Numeric index.
	 * @return Paint object.
	 */
	public abstract Paint get(int value);

	/**
	 * Returns the Paint object according to the specified index. The specified
	 * value will be handled like an integer index.
	 * @param index Numeric index object.
	 * @return Paint object.
	 */
	public Paint get(Number index) {
		return get(index.intValue());
	}

	@Override
	protected Integer applyMode(Integer index, Integer rangeMin, Integer rangeMax) {
		if (index >= rangeMin && index <= rangeMax) {
			return index;
		}
		Mode mode = getMode();
		if (mode == Mode.REPEAT) {
			return MathUtils.limit(index, rangeMin, rangeMax);
		} else if (mode == Mode.CIRCULAR) {
			int range = rangeMax - rangeMin + 1;
			int i = index%range;
			if (i < 0) {
				i += range;
			}
			return i + rangeMin;
		}
		return null;
	}
}
