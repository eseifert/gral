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
import de.erichseifert.gral.util.Orientation;


/**
 * Class that connects <code>DataPoint</code>s with a stair-like line.
 */
public class DiscreteLineRenderer2D extends AbstractLineRenderer2D {
	/** Key for specifying an instance of
	{@link de.erichseifert.gral.util.Orientation} which indicates
	the primary direction of the "steps". */
	public static final Key ASCENT_DIRECTION =
		new Key("line.discrete.ascentDirection"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the relative
	distance between two points, i.e. the "step" of a stair. */
	public static final Key ASCENDING_POINT =
		new Key("line.discrete.ascendingPoint"); //$NON-NLS-1$

	/**
	 * Initializes a new <code>DiscreteLineRenderer2D</code> instance with
	 * default settings.
	 */
	public DiscreteLineRenderer2D() {
		setSettingDefault(ASCENT_DIRECTION, Orientation.HORIZONTAL);
		setSettingDefault(ASCENDING_POINT, 0.5);
	}

	@Override
	public Drawable getLine(final Iterable<DataPoint> points) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(DrawingContext context) {
				Orientation dir = DiscreteLineRenderer2D.this
					.getSetting(ASCENT_DIRECTION);
				double ascendingPoint = DiscreteLineRenderer2D.this
					.<Number>getSetting(ASCENDING_POINT).doubleValue();

				// Construct shape
				Path2D line = new Path2D.Double();
				for (DataPoint point : points) {
					Point2D pos = point.getPosition().getPoint2D();
					if (line.getCurrentPoint() == null) {
						line.moveTo(pos.getX(), pos.getY());
					} else {
						Point2D posPrev = line.getCurrentPoint();
						if (Orientation.HORIZONTAL.equals(dir)) {
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
