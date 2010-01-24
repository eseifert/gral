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

package openjchart.tests.plots.lines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import openjchart.Drawable;
import openjchart.DrawableConstants.Orientation;
import openjchart.plots.DataPoint2D;
import openjchart.plots.lines.DiscreteLineRenderer2D;
import openjchart.plots.lines.LineRenderer2D;

import org.junit.Test;

public class DiscreteLineRendererTest {

	@Test
	public void testLine() {
		// Get line
		LineRenderer2D r = new DiscreteLineRenderer2D();
		DataPoint2D[] points = {
			new DataPoint2D(new Point2D.Double(0.0, 0.0), null, null),
			new DataPoint2D(new Point2D.Double(1.0, 1.0), null, null)
		};

		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		for (Orientation dir : Orientation.values()) {
			r.setSetting(DiscreteLineRenderer2D.KEY_ASCENT_DIRECTION, dir);
			Drawable line = r.getLine(points);
			assertNotNull(line);
			line.draw(g2d);
		}
	}

	@Test
	public void testSettings() {
		// Get
		LineRenderer2D r = new DiscreteLineRenderer2D();
		assertEquals(Color.BLACK, r.getSetting(LineRenderer2D.KEY_COLOR));
		// Set
		r.setSetting(LineRenderer2D.KEY_COLOR, Color.RED);
		assertEquals(Color.RED, r.getSetting(LineRenderer2D.KEY_COLOR));
		// Remove
		r.removeSetting(LineRenderer2D.KEY_COLOR);
		assertEquals(Color.BLACK, r.getSetting(LineRenderer2D.KEY_COLOR));
	}

}
