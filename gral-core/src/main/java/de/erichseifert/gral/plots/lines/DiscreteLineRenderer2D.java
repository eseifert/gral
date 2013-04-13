/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Orientation;


/**
 * Class that connects {@code DataPoint}s with a stair-like line.
 */
public class DiscreteLineRenderer2D extends AbstractLineRenderer2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = 4648286099838467355L;

	/** Key for specifying an instance of
	{@link de.erichseifert.gral.util.Orientation} which indicates the primary
    direction of the "steps". */
	public static final Key ASCENT_DIRECTION =
		new Key("line.discrete.ascentDirection"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the relative distance
	between two points, i.e. the "step" of a stair. */
	public static final Key ASCENDING_POINT =
		new Key("line.discrete.ascendingPoint"); //$NON-NLS-1$

	/**
	 * Initializes a new {@code DiscreteLineRenderer2D} instance with default
	 * settings.
	 */
	public DiscreteLineRenderer2D() {
		setSettingDefault(ASCENT_DIRECTION, Orientation.HORIZONTAL);
		setSettingDefault(ASCENDING_POINT, 0.5);
	}

	/**
	 * Returns a graphical representation for the line defined by
	 * {@code points}.
	 * @param points Points to be used for creating the line.
	 * @param shape Geometric shape for this line.
	 * @return Representation of the line.
	 */
	public Drawable getLine(final List<DataPoint> points, final Shape shape) {
		Drawable d = new AbstractDrawable() {
			/** Version id for serialization. */
			private static final long serialVersionUID = -1686744943386843195L;

			/**
			 * Draws the {@code Drawable} with the specified drawing context.
			 * @param context Environment used for drawing
			 */
			public void draw(DrawingContext context) {
				// Draw path
				Paint paint = DiscreteLineRenderer2D.this
					.getSetting(LineRenderer.COLOR);
				GraphicsUtils.fillPaintedShape(
					context.getGraphics(), shape, paint, null);
			}
		};
		return d;
	}

	/**
	 * Returns the geometric shape for this line.
	 * @param points Points used for creating the line.
	 * @return Geometric shape for this line.
	 */
	public Shape getLineShape(List<DataPoint> points) {
		Orientation dir = getSetting(ASCENT_DIRECTION);
		double ascendingPoint =
			this.<Number>getSetting(ASCENDING_POINT).doubleValue();

		// Construct shape
		Path2D shape = new Path2D.Double();
		for (DataPoint point : points) {
			Point2D pos = point.position.getPoint2D();
			if (shape.getCurrentPoint() == null) {
				shape.moveTo(pos.getX(), pos.getY());
			} else {
				Point2D posPrev = shape.getCurrentPoint();
				if (dir == Orientation.HORIZONTAL) {
					double ascendingX = posPrev.getX() +
						(pos.getX() - posPrev.getX()) * ascendingPoint;
					shape.lineTo(ascendingX,  posPrev.getY());
					shape.lineTo(ascendingX,  pos.getY());
				} else {
					double ascendingY = posPrev.getY() +
						(pos.getY() - posPrev.getY()) * ascendingPoint;
					shape.lineTo(posPrev.getX(), ascendingY);
					shape.lineTo(pos.getX(), ascendingY);
				}
				shape.lineTo(pos.getX(), pos.getY());
			}
		}

		return punch(shape, points);
	}
}
