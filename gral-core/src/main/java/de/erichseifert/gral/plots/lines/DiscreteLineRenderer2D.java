/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
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
	 * @return Representation of the line.
	 */
	public Drawable getLine(final Iterable<DataPoint> points) {
		Drawable d = new AbstractDrawable() {
			/** Version id for serialization. */
			private static final long serialVersionUID = -1686744943386843195L;

			public void draw(DrawingContext context) {
				DiscreteLineRenderer2D renderer = DiscreteLineRenderer2D.this;
				Orientation dir = renderer.getSetting(ASCENT_DIRECTION);
				double ascendingPoint = renderer.<Number>getSetting(ASCENDING_POINT).doubleValue();

				// Construct shape
				Path2D line = new Path2D.Double();
				for (DataPoint point : points) {
					Point2D pos = point.position.getPoint2D();
					if (line.getCurrentPoint() == null) {
						line.moveTo(pos.getX(), pos.getY());
					} else {
						Point2D posPrev = line.getCurrentPoint();
						if (dir == Orientation.HORIZONTAL) {
							double ascendingX = posPrev.getX() +
								(pos.getX() - posPrev.getX()) * ascendingPoint;
							line.lineTo(ascendingX,  posPrev.getY());
							line.lineTo(ascendingX,  pos.getY());
						} else {
							double ascendingY = posPrev.getY() +
								(pos.getY() - posPrev.getY()) * ascendingPoint;
							line.lineTo(posPrev.getX(), ascendingY);
							line.lineTo(pos.getX(), ascendingY);
						}
						line.lineTo(pos.getX(), pos.getY());
					}
				}

				// Draw path
				Shape lineShape = punch(line, points);
				Paint paint = DiscreteLineRenderer2D.this
					.getSetting(LineRenderer.COLOR);
				GraphicsUtils.fillPaintedShape(
					context.getGraphics(), lineShape, paint, null);
			}
		};
		return d;
	}

}
