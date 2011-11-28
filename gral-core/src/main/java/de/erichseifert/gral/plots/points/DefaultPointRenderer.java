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

import java.awt.Paint;
import java.awt.Shape;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;


/**
 * Class that creates {@code Drawable}s for a row of data.
 */
public class DefaultPointRenderer extends AbstractPointRenderer {
	/**
	 * Returns the graphical representation to be drawn for the specified data
	 * value.
	 * @param axis that is used to project the point.
	 * @param axisRenderer Renderer for the axis.
	 * @param row Data row containing the point.
	 * @param col Index of the column that will be projected on the axis.
	 * @return Component that can be used to draw the point
	 */
	public Drawable getPoint(final Axis axis,
			final AxisRenderer axisRenderer, final Row row, final int col) {
		Drawable drawable = new AbstractDrawable() {
			public void draw(DrawingContext context) {
				PointRenderer renderer = DefaultPointRenderer.this;
				Paint paint = renderer.getSetting(COLOR);
				Shape point = getPointPath(row);
				GraphicsUtils.fillPaintedShape(
					context.getGraphics(), point, paint, null);

				if (renderer.<Boolean>getSetting(VALUE_DISPLAYED)) {
					int colValue = renderer.<Integer>getSetting(VALUE_COLUMN);
					Comparable<?> value = row.get(colValue);
					drawValue(context, point, value);
				}

				if (renderer.<Boolean>getSetting(ERROR_DISPLAYED)) {
					int colErrorTop = renderer.<Integer>getSetting(ERROR_COLUMN_TOP);
					int colErrorBottom = renderer.<Integer>getSetting(ERROR_COLUMN_BOTTOM);
					if (colErrorTop >= 0 && colErrorTop < row.size() && row.isColumnNumeric(colErrorTop) &&
							colErrorBottom >= 0 && colErrorBottom < row.size() && row.isColumnNumeric(colErrorBottom)) {
						Number value = (Number) row.get(col);
						Number errorTop = (Number) row.get(colErrorTop);
						Number errorBottom = (Number) row.get(colErrorBottom);
						if (MathUtils.isCalculatable(errorTop) &&
								MathUtils.isCalculatable(errorBottom)) {
							drawError(context, point, value.doubleValue(),
								errorTop.doubleValue(), errorBottom.doubleValue(),
								axis, axisRenderer);
						}
					}
				}
			}
		};

		return drawable;
	}

	/**
	 * Returns a {@code Shape} instance that can be used
	 * for further calculations.
	 * @param row Data row containing the point.
	 * @return Outline that describes the point's shape.
	 */
	public Shape getPointPath(Row row) {
		Shape shape = getSetting(SHAPE);
		return shape;
	}
}
