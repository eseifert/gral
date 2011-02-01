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
package de.erichseifert.gral.plots.areas;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;

/**
 * Default two-dimensional implementation of the <code>AreaRenderer</code>
 * interface.
 */
public class DefaultAreaRenderer2D extends AbstractAreaRenderer {

	@Override
	public Drawable getArea(Axis axis, AxisRenderer axisRenderer,
			Iterable<DataPoint> points) {
		Shape path = getAreaShape(axis, axisRenderer, points);
		final Shape area = punch(path, points);

		return new AbstractDrawable() {
			@Override
			public void draw(DrawingContext context) {
				Paint paint = DefaultAreaRenderer2D.this.getSetting(COLOR);
				GraphicsUtils.fillPaintedShape(context.getGraphics(),
						area, paint, area.getBounds2D());
			}
		};
	}

	/**
	 * Returns the shape used for rendering the area of a data points.
	 * @param axis Axis the points are mapped on.
	 * @param axisRenderer Renderer that is associated with the axis.
	 * @param points Data points.
	 * @return Area of the specified data points
	 */
	private Shape getAreaShape(Axis axis, AxisRenderer axisRenderer,
			Iterable<DataPoint> points) {
		double axisYMin = axis.getMin().doubleValue();
		double axisYMax = axis.getMax().doubleValue();
		double axisYOrigin = MathUtils.limit(0.0, axisYMin, axisYMax);

		PointND<Double> posOrigin = null;
		if (axisRenderer != null) {
			posOrigin = axisRenderer.getPosition(
					axis, axisYOrigin, true, false);
		}

		Path2D path = new Path2D.Double();
		if (posOrigin == null) {
			return path;
		}

		double posYOrigin = posOrigin.get(PointND.Y);
		double x = 0.0;
		double y = 0.0;

		for (DataPoint p: points) {
			Point2D pos = p.getPosition().getPoint2D();
			x = pos.getX();
			y = pos.getY();
			if (path.getCurrentPoint() == null) {
				path.moveTo(x, posYOrigin);
			}
			path.lineTo(x, y);
		}

		if (path.getCurrentPoint() != null) {
			path.lineTo(x, posYOrigin);
			path.closePath();
		}

		return path;
	}
}
