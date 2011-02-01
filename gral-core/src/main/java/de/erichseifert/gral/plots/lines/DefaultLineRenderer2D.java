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
package de.erichseifert.gral.plots.lines;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.util.GraphicsUtils;


/**
 * Class that connects two dimensional data points with a straight line.
 */
public class DefaultLineRenderer2D extends AbstractLineRenderer2D {
	/** Number of line segments which will be reserved to avoid unnecessary
	copying of array data. */
	private static final int INITIAL_LINE_CAPACITY = 10000;

	/**
	 * Initializes a new <code>DefaultLineRenderer2D</code> instance.
	 */
	public DefaultLineRenderer2D() {
	}

	@Override
	public Drawable getLine(final Iterable<DataPoint> points) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(DrawingContext context) {
				// Construct shape
				Path2D line = new Path2D.Double(
					Path2D.WIND_NON_ZERO, INITIAL_LINE_CAPACITY);
				for (DataPoint point : points) {
					Point2D pos = point.getPosition().getPoint2D();
					if (line.getCurrentPoint() == null) {
						line.moveTo(pos.getX(), pos.getY());
					} else {
						line.lineTo(pos.getX(), pos.getY());
					}
				}

				// Draw line
				Shape lineShape = punch(line, points);
				Paint paint = DefaultLineRenderer2D.this
					.getSetting(LineRenderer.COLOR);
				GraphicsUtils.fillPaintedShape(
					context.getGraphics(), lineShape, paint, null);
			}
		};
		return d;
	}

}
