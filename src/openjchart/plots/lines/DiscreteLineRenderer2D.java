/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots.lines;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.DrawableConstants.Orientation;
import openjchart.plots.DataPoint2D;
import openjchart.util.GraphicsUtils;

/**
 * Class that connects DataPoints2D with a stair-like line.
 */
public class DiscreteLineRenderer2D extends AbstractLineRenderer2D {
	/** Key for specifying an {@link openjchart.DrawableConstants.Orientation} instance which indicates the primary direction of the "steps". */
	public static final String KEY_ASCENT_DIRECTION = "line.discrete.ascentDirection";
	/** Key for specifying the percentage of the distance between two points, which indicates the "step" of a stair. */
	public static final String KEY_ASCENDING_POINT = "line.discrete.ascendingPoint";

	/**
	 * Creates a new DiscreteLineRenderer2D object with default settings.
	 */
	public DiscreteLineRenderer2D() {
		setSettingDefault(KEY_ASCENT_DIRECTION, Orientation.HORIZONTAL);
		setSettingDefault(KEY_ASCENDING_POINT, 0.5);
	}

	@Override
	public Drawable getLine(final DataPoint2D... points) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Orientation dir = getSetting(KEY_ASCENT_DIRECTION);
				double ascendingPoint = getSetting(KEY_ASCENDING_POINT);

				// Construct shape
				GeneralPath line = new GeneralPath();
				for (DataPoint2D point : points) {
					Point2D pos = point.getPosition();
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
				Shape lineShape = punchShapes(line, points);
				Paint paint = getSetting(LineRenderer2D.KEY_COLOR);
				GraphicsUtils.fillPaintedShape(g2d, lineShape, paint, null);
			}
		};
		return d;
	}

}
