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
package de.erichseifert.gral.plots.points;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.util.DataUtils;
import de.erichseifert.gral.util.MathUtils;


/**
 * Class that provides {@code Drawable}s, which are sized accordingly to
 * the data.
 */
public class SizeablePointRenderer extends DefaultPointRenderer2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = 3276439387457161307L;

	/** Index of the column for the point size. */
	private int column;

	/**
	 * Initializes a new object.
	 */
	public SizeablePointRenderer() {
		column = 2;
	}

	/**
	 * Returns the index of the column which is used for point sizes.
	 * @return index of the column which is used for point sizes.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Sets the index of the column which will be used for point sizes.
	 * @param column Index of the column which will be used for point sizes.
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	@Override
	public Shape getPointShape(PointData data) {
		Shape shape = getShape();

		Row row = data.row;
		int colSize = getColumn();
		if (colSize >= row.size() || colSize < 0 || !row.isColumnNumeric(colSize)) {
			return shape;
		}

		Number value = (Number) row.get(colSize);
		double size = DataUtils.getValueOrDefault(value, Double.NaN);
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
