/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
package de.erichseifert.gral.plots.points;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.util.DataUtils;
import de.erichseifert.gral.util.MathUtils;


/**
 * Class that provides {@code Drawable}s, which are sized accordingly to
 * the data.
 */
public class SizeablePointRenderer extends DefaultPointRenderer2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = 3276439387457161307L;

	/** Key for specifying the {@link Integer} which specifies the index of the
	column that is used for point sizes. */
	public static final Key COLUMN =
		new Key("sizeablePoint.column"); //$NON-NLS-1$

	/**
	 * Initializes a new object.
	 */
	public SizeablePointRenderer() {
		setSettingDefault(COLUMN, 2);
	}

	@Override
	public Shape getPointShape(PointData data) {
		Shape shape = this.<Shape>getSetting(SHAPE);

		Row row = data.row;
		int colSize = this.<Integer>getSetting(COLUMN);
		if (colSize >= row.size() || colSize < 0 || !row.isColumnNumeric(colSize)) {
			return shape;
		}

		double size = DataUtils.getValueOrDefault((Number) row.get(colSize), Double.NaN);
		if (!MathUtils.isCalculatable(size) || size <= 0.0) {
			return null;
		}

		if (size != 1.0) {
			AffineTransform tx = AffineTransform.getScaleInstance(size, size);
			shape = tx.createTransformedShape(shape);
		}
		return shape;
	}
}
