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

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
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
 * interface that draws lines from data points to the main axis.
 */
public class LineAreaRenderer2D extends AbstractAreaRenderer {
	/** Key for specifying the {@link java.awt.Stroke} instance that is used
	draw the lines from the data points to the axis. */
	public static final Key STROKE = new Key("linearea.stroke"); //$NON-NLS-1$

	/**
	 * Standard constructor that initializes a new instance.
	 */
	public LineAreaRenderer2D() {
		setSettingDefault(STROKE, new BasicStroke(1f));
	}

	@Override
	public Drawable getArea(Axis axis, AxisRenderer axisRenderer,
			Iterable<DataPoint> points) {
		Shape path = getAreaShape(axis, axisRenderer, points);
		Stroke stroke = getSetting(STROKE);
		final Shape area = punch(stroke.createStrokedShape(path), points);

		return new AbstractDrawable() {
			@Override
			public void draw(DrawingContext context) {
				Paint paint = LineAreaRenderer2D.this.getSetting(COLOR);
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
		double posYOrigin = 0.0;
		if (axisRenderer != null) {
			posYOrigin = axisRenderer.getPosition(
					axis, axisYOrigin, true, false).get(PointND.Y);
		}
		Path2D path = new Path2D.Double();
		double x = 0.0;
		double y = 0.0;
		for (DataPoint p : points) {
			Point2D pos = p.getPosition().getPoint2D();
			x = pos.getX();
			y = pos.getY();
			path.moveTo(x, y);
			path.lineTo(x, posYOrigin);
		}

		return path;
	}
}
