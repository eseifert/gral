/**
 * GRAL : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.shapes;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import de.erichseifert.gral.data.Row;


/**
 * Class that provides Drawables, which are sized accordingly to the data.
 */
public class SizeableShapeRenderer extends DefaultShapeRenderer {

	/**
	 * Creates a new SizeableShapeRenderer object.
	 */
	public SizeableShapeRenderer() {
	}

	@Override
	public Shape getShapePath(Row row) {
		Shape shape = getSetting(KEY_SHAPE);
		if (row.getSource().getColumnCount() >= 3) {
			double size = row.get(2).doubleValue();
			AffineTransform tx = AffineTransform.getScaleInstance(size, size);
			shape = tx.createTransformedShape(shape);
		}
		return shape;
	}
}
