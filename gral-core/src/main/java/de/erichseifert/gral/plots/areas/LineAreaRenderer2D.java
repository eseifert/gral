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
package de.erichseifert.gral.plots.areas;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;

/**
 * Default two-dimensional implementation of the {@code AreaRenderer} interface
 * that draws lines from data points to the main axis.
 */
public class LineAreaRenderer2D extends AbstractAreaRenderer {
	/** Version id for serialization. */
	private static final long serialVersionUID = -8396097579938931392L;

	/** Stroke that is used to draw the lines from the data points to the
	 * axis. */
	private Stroke stroke;

	/**
	 * Standard constructor that initializes a new instance.
	 */
	public LineAreaRenderer2D() {
		stroke = new BasicStroke(1f);
	}

	/**
	 * Returns the graphical representation to be drawn for the specified data
	 * points.
	 * @param points Points that define the shape of the area.
	 * @param shape Geometric shape of the area.
	 * @return Representation of the area.
	 */
	public Drawable getArea(final List<DataPoint> points, final Shape shape) {
		return new AbstractDrawable() {
			/** Version id for serialization. */
			private static final long serialVersionUID = 5492321759151727458L;

			/**
			 * Draws the {@code Drawable} with the specified drawing context.
			 * @param context Environment used for drawing
			 */
			public void draw(DrawingContext context) {
				Paint paint = LineAreaRenderer2D.this.getColor();
				GraphicsUtils.fillPaintedShape(context.getGraphics(),
					shape, paint, null);
			}
		};
	}

	/**
	 * Returns the shape used for rendering the area of a data points.
	 * @param points Data points.
	 * @return Geometric shape for the area of the specified data points.
	 */
	public Shape getAreaShape(List<DataPoint> points) {
		if (points.isEmpty() || points.get(0) == null) {
			return null;
		}

		Axis axisY = points.get(0).data.axes.get(1);
		AxisRenderer axisRendererY = points.get(0).data.axisRenderers.get(1);

		double axisYMin = axisY.getMin().doubleValue();
		double axisYMax = axisY.getMax().doubleValue();
		double axisYOrigin = MathUtils.limit(0.0, axisYMin, axisYMax);
		double posYOrigin = 0.0;
		if (axisRendererY != null) {
			posYOrigin = axisRendererY.getPosition(
					axisY, axisYOrigin, true, false).get(PointND.Y);
		}
		Path2D shape = new Path2D.Double();
		double x = 0.0;
		double y = 0.0;
		for (DataPoint p : points) {
			Point2D pos = p.position.getPoint2D();
			x = pos.getX();
			y = pos.getY();
			shape.moveTo(x, y);
			shape.lineTo(x, posYOrigin);
		}

		Stroke stroke = getStroke();
		return stroke.createStrokedShape(shape);
	}

	/**
	 * Returns the stroke that is used to draw the lines from the
	 * data points to the axis.
	 * @return Stroke for line drawing.
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * Set the stroke that is used to draw the lines from the
	 * data points to the axis.
	 * @param stroke Stroke for line drawing.
	 */
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}
}
