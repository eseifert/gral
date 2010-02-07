/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.lines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


import org.junit.Test;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.plots.DataPoint2D;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer2D;

public class DefaultLineRendererTest {

	@Test
	public void testLine() {
		// Get line
		LineRenderer2D r = new DefaultLineRenderer2D();
		DataPoint2D[] points = {
			new DataPoint2D(new Point2D.Double(0.0, 0.0), null, null),
			new DataPoint2D(new Point2D.Double(1.0, 1.0), null, null)
		};
		Drawable line = r.getLine(points);
		assertNotNull(line);

		// Draw line
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		line.draw(g2d);
	}

	@Test
	public void testSettings() {
		// Get
		LineRenderer2D r = new DefaultLineRenderer2D();
		assertEquals(Color.BLACK, r.getSetting(LineRenderer2D.KEY_COLOR));
		// Set
		r.setSetting(LineRenderer2D.KEY_COLOR, Color.RED);
		assertEquals(Color.RED, r.getSetting(LineRenderer2D.KEY_COLOR));
		// Remove
		r.removeSetting(LineRenderer2D.KEY_COLOR);
		assertEquals(Color.BLACK, r.getSetting(LineRenderer2D.KEY_COLOR));
	}

}
