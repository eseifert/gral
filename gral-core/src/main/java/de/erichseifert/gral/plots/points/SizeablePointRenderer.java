/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
import de.erichseifert.gral.util.MathUtils;


/**
 * Class that provides <code>Drawable</code>s, which are sized accordingly to
 * the data.
 */
public class SizeablePointRenderer extends DefaultPointRenderer {
	/** Key for specifying the {@link java.lang.Integer} which specifies the
	index of the column that is used for point sizes. */
	public static final Key COLUMN = new Key("sizeablePoint.column"); //$NON-NLS-1$

	/**
	 * Initializes a new object.
	 */
	public SizeablePointRenderer() {
		setSettingDefault(COLUMN, 2);
	}

	@Override
	public Shape getPointPath(Row row) {
		Shape shape = getSetting(SHAPE);
		int sizeColumn = this.<Number>getSetting(COLUMN).intValue();
		if (sizeColumn >= row.size()) {
			return shape;
		}
		double size = row.get(sizeColumn).doubleValue();
		if (size < 0.0 || !MathUtils.isCalculatable(size)) {
			return null;
		}
		if (size != 1.0) {
			AffineTransform tx = AffineTransform.getScaleInstance(size, size);
			shape = tx.createTransformedShape(shape);
		}
		return shape;
	}
}
