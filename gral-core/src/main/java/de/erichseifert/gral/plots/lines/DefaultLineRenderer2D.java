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
package de.erichseifert.gral.plots.lines;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.util.GraphicsUtils;


/**
 * Class that connects two dimensional data points with a straight line.
 */
public class DefaultLineRenderer2D extends AbstractLineRenderer2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = -1728830281555843667L;
	/** Number of line segments which will be reserved to avoid unnecessary
	copying of array data. */
	private static final int INITIAL_LINE_CAPACITY = 10000;

	/**
	 * Initializes a new {@code DefaultLineRenderer2D} instance.
	 */
	public DefaultLineRenderer2D() {
	}

	/**
	 * Returns a graphical representation for the line defined by
	 * {@code e points}.
	 * @param points Points used for creating the line.
	 * @param shape Geometric shape for this line.
	 * @return Representation of the line.
	 */
	public Drawable getLine(final List<DataPoint> points, final Shape shape) {
		return new AbstractDrawable() {
			/** Version id for serialization. */
			private static final long serialVersionUID1 = 7995515716470892483L;

			/**
			 * Draws the {@code Drawable} with the specified drawing context.
			 * @param context Environment used for drawing
			 */
			public void draw(DrawingContext context) {
				// Draw line
				Paint paint = DefaultLineRenderer2D.this.getColor();
				GraphicsUtils.fillPaintedShape(
					context.getGraphics(), shape, paint, null);
			}
		};
	}

	/**
	 * Returns the geometric shape for this line.
	 * @param points Points used for creating the line.
	 * @return Geometric shape for this line.
	 */
	public Shape getLineShape(List<DataPoint> points) {
		// Construct shape
		Path2D shape = new Path2D.Double(
			Path2D.WIND_NON_ZERO, INITIAL_LINE_CAPACITY);
		for (DataPoint point : points) {
			Point2D pos = point.position.getPoint2D();
			if (shape.getCurrentPoint() == null) {
				shape.moveTo(pos.getX(), pos.getY());
			} else {
				shape.lineTo(pos.getX(), pos.getY());
			}
		}
		return stroke(shape);
	}
}
