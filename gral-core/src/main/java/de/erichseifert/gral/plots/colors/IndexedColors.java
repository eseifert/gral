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

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.erichseifert.gral.util.MathUtils;

/**
 * Maps index values to a specified color palette.
 */
public class IndexedColors extends IndexedColorMapper {
	/** Version id for serialization. */
	private static final long serialVersionUID = -8072979842165455075L;

	/** Color palette that will be used for mapping. **/
	private final List<Color> colors;

	/**
	 * Creates a new instance with at least one color.
	 * @param color1 First color.
	 * @param colors Additional colors.
	 */
	public IndexedColors(Color color1, Color... colors) {
		this.colors = new ArrayList<>();
		this.colors.add(color1);
		this.colors.addAll(Arrays.asList(colors));
	}

	/**
	 * Returns the Paint object associated to the specified index value.
	 * @param index Numeric index.
	 * @return Paint object.
	 */
	@Override
	public Paint get(int index) {
		Integer i = applyMode(index, 0, colors.size() - 1);
		if (!MathUtils.isCalculatable(i)) {
			return null;
		}
		return colors.get(i);
	}

	/**
	 * Returns the colors that are used for mapping.
	 * @return A list of colors in the order they are used as the color palette.
	 */
	public List<Color> getColors() {
		return Collections.unmodifiableList(colors);
	}

	@Override
	public void setMode(
			de.erichseifert.gral.plots.colors.ColorMapper.Mode mode) {
		super.setMode(mode);
	}
}
