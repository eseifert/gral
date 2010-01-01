/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.plots.DataPoint2D;
import openjchart.util.GraphicsUtils;

public class DefaultLineRenderer2D extends AbstractLineRenderer2D {

	public DefaultLineRenderer2D() {
	}

	@Override
	public Drawable getLine(final DataPoint2D... points) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				DataPoint2D pointPrv = null;
				for (DataPoint2D pointCur : points) {
					if (pointPrv != null) {
						Point2D pos1 = pointPrv.getPosition();
						Point2D pos2 = pointCur.getPosition();
						Line2D line = new Line2D.Double(pos1, pos2);
						Shape lineShape = punchShapes(line, pointPrv, pointCur);
		
						// Draw line
						Paint paint = getSetting(LineRenderer2D.KEY_LINE_COLOR);
						GraphicsUtils.fillPaintedShape(g2d, lineShape, paint, null);
					}
					pointPrv = pointCur;
				}
			}
		};
		return d;
	}

}
