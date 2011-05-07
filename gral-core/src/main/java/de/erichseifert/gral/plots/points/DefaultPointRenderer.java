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


/**
 * Class that creates <code>Drawable</code>s for a row of data.
 */
public class DefaultPointRenderer extends AbstractPointRenderer {
	/**
	 * Returns the graphical representation to be drawn for the specified data
	 * value.
	 * @param axis that is used to project the point.
	 * @param axisRenderer Renderer for the axis.
	 * @param row Data row containing the point.
	 * @return Component that can be used to draw the point
	 */
	public Drawable getPoint(final Axis axis,
			final AxisRenderer axisRenderer, final Row row) {
		Drawable drawable = new AbstractDrawable() {
			public void draw(DrawingContext context) {
				Paint paint = DefaultPointRenderer.this.getSetting(COLOR);
				Shape point = getPointPath(row);
				GraphicsUtils.fillPaintedShape(
					context.getGraphics(), point, paint, null);
				PointRenderer renderer = DefaultPointRenderer.this;

				if (renderer.<Boolean>getSetting(VALUE_DISPLAYED)) {
					drawValue(context, point, row.get(1).doubleValue());
				}
				if (renderer.<Boolean>getSetting(ERROR_DISPLAYED)) {
					int columnIndex = row.size() - 1;
					drawError(context, point, row.get(1).doubleValue(),
							row.get(columnIndex - 1).doubleValue(),
							row.get(columnIndex).doubleValue(),
							axis, axisRenderer);
				}
			}
		};

		return drawable;
	}

	/**
	 * Returns a <code>Shape</code> instance that can be used
	 * for further calculations.
	 * @param row Data row containing the point.
	 * @return Outline that describes the point's shape.
	 */
	public Shape getPointPath(Row row) {
		Shape shape = getSetting(SHAPE);
		return shape;
	}
}
