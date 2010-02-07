/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.shapes;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.util.GraphicsUtils;


/**
 * Class that creates Drawables for a row of data.
 */
public class DefaultShapeRenderer extends AbstractShapeRenderer {

	@Override
	public Drawable getShape(final Row row) {
		Drawable drawable = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Paint paint = getSetting(KEY_COLOR);
				Shape shape = getShapePath(row);
				GraphicsUtils.fillPaintedShape(g2d, shape, paint, null);

				if (getSetting(KEY_VALUE_DISPLAYED)) {
					drawValue(g2d, shape, row.get(1).doubleValue());
				}
			}
		};

		return drawable;
	}

	@Override
	public Shape getShapePath(Row row) {
		Shape shape = getSetting(KEY_SHAPE);
		return shape;
	}
}
