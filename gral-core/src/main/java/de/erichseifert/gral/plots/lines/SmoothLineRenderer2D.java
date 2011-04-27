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
 * Class that connects <code>DataPoint</code>s with a smooth line.
 */
public class SmoothLineRenderer2D extends AbstractLineRenderer2D {
	/** Key for specifying a {@link java.lang.Number} value for the smoothness
	of the line. The value must be in range 0 (sharpest) to 1 (smoothest). */
	public static final Key SMOOTHNESS =
		new Key("line.smooth.smoothness"); //$NON-NLS-1$

	/**
	 * Initializes a new <code>SmoothLineRenderer2D</code> instance with
	 * default settings.
	 */
	public SmoothLineRenderer2D() {
		setSettingDefault(SMOOTHNESS, 1.0);
	}

	@Override
	public Drawable getLine(final Iterable<DataPoint> points) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(DrawingContext context) {
				double smoothness = SmoothLineRenderer2D.this.
					<Number>getSetting(SMOOTHNESS).doubleValue();

				// Construct shape
				Path2D line = new Path2D.Double();

				Point2D p0 = null, p1 = null, p2 = null, p3 = null;
				Point2D ctrl1 = new Point2D.Double();
				Point2D ctrl2 = new Point2D.Double();
				for (DataPoint point : points) {
					p3 = point.getPosition().getPoint2D();

					addCurve(line, p0, p1, p2, p3, ctrl1, ctrl2, smoothness);

					p0 = p1;
					p1 = p2;
					p2 = p3;
				}
				addCurve(line, p0, p1, p2, p3, ctrl1, ctrl2, smoothness);

				// Draw path
				Shape lineShape = punch(line, points);
				Paint paint = SmoothLineRenderer2D.this
					.getSetting(LineRenderer.COLOR);
				GraphicsUtils.fillPaintedShape(
					context.getGraphics(), lineShape, paint, null);
			}
		};
		return d;
	}

	/**
	 * Utility method to add a smooth curve segment to a specified line path.
	 * @param line Line path.
	 * @param p0 Previous neighbor.
	 * @param p1 First point.
	 * @param p2 Second point.
	 * @param p3 Next neighbor.
	 * @param ctrl1 First control point.
	 * @param ctrl2 Second control point.
	 * @param smoothness Smoothness factor
	 */
	private static void addCurve(Path2D line, Point2D p0, Point2D p1,
			Point2D p2, Point2D p3, Point2D ctrl1, Point2D ctrl2,
			double smoothness) {
		if (p1 != null) {
			if (line.getCurrentPoint() == null) {
				line.moveTo(p1.getX(), p1.getY());
			}
			if (p2 != null) {
				getControlsPoints(p0, p1, p2, p3, ctrl1, ctrl2, smoothness);
				line.curveTo(
					ctrl1.getX(), ctrl1.getY(),
					ctrl2.getX(), ctrl2.getY(),
					p2.getX(), p2.getY());
			}
		}
	}

	/**
	 * Set the coordinates of two control points <i>ctrl1</i> and <i>ctrl2</i>
	 * which can be used to draw a smooth Bézier curve through two points
	 * <i>p1</i> and <i>p2</i>. To get a smooth curve the two neighboring
	 * points <i>p0</i> and <i>p3</i> are required. However, <i>p0</i> and
	 * <i>p3</i> may also be set to <code>null</code> in case of end points.
	 * @param p0 Previous neighbor.
	 * @param p1 First point.
	 * @param p2 Second point.
	 * @param p3 Next neighbor.
	 * @param ctrl1 First control point.
	 * @param ctrl2 Second control point.
	 * @param smoothness Smoothness factor
	 */
	private static void getControlsPoints(Point2D p0, Point2D p1, Point2D p2,
			Point2D p3, Point2D ctrl1, Point2D ctrl2, double smoothness) {
		if (p0 == null) {
			p0 = p1;
		}
        if (p3 == null) {
        	p3 = p2;
        }

        Point2D c1 = new Point2D.Double(
			(p0.getX() + p1.getX()) / 2.0,
			(p0.getY() + p1.getY()) / 2.0);
		Point2D c2 = new Point2D.Double(
			(p1.getX() + p2.getX()) / 2.0,
			(p1.getY() + p2.getY()) / 2.0);
		Point2D c3 = new Point2D.Double(
			(p2.getX() + p3.getX()) / 2.0,
			(p2.getY() + p3.getY()) / 2.0);

		double len1 = p1.distance(p0);
		double len2 = p2.distance(p1);
		double len3 = p3.distance(p2);

		double k1 = len1 / (len1 + len2);
		double k2 = len2 / (len2 + len3);

		Point2D m1 = new Point2D.Double(
			c1.getX() + (c2.getX() - c1.getX()) * k1,
			c1.getY() + (c2.getY() - c1.getY()) * k1);
		Point2D m2 = new Point2D.Double(
			c2.getX() + (c3.getX() - c2.getX()) * k2,
			c2.getY() + (c3.getY() - c2.getY()) * k2);

		ctrl1.setLocation(
			m1.getX() + (c2.getX() - m1.getX()) * smoothness + p1.getX() - m1.getX(),
			m1.getY() + (c2.getY() - m1.getY()) * smoothness + p1.getY() - m1.getY()
		);
		ctrl2.setLocation(
			m2.getX() + (c2.getX() - m2.getX()) * smoothness + p2.getX() - m2.getX(),
			m2.getY() + (c2.getY() - m2.getY()) * smoothness + p2.getY() - m2.getY()
		);
	}

}
