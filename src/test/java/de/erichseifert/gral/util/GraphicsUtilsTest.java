/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


import org.junit.Test;

import de.erichseifert.gral.util.GraphicsUtils;

public class GraphicsUtilsTest {
	//private static final double DELTA = 1e-5;

	@Test
	public void testGetLayout() {
		TextLayout layout = GraphicsUtils.getLayout("M", Font.decode(null));
		assertNotNull(layout);
		Rectangle2D bounds = layout.getBounds();
		assertTrue(bounds.getWidth() > 0.0);
		assertTrue(bounds.getHeight() > 0.0);
	}

	@Test
	public void testPaintedShape() {
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		Shape shape = new Rectangle2D.Double(10.0, 10.0, 300.0, 220.0);
		Paint paint = Color.red;

		GraphicsUtils.fillPaintedShape(g2d, shape, paint, null);
		Rectangle2D paintBounds = shape.getBounds2D();
		GraphicsUtils.fillPaintedShape(g2d, shape, paint, paintBounds);

		GraphicsUtils.drawPaintedShape(g2d, shape, paint, paintBounds, null);
		BasicStroke stroke = new BasicStroke(2f);
		GraphicsUtils.drawPaintedShape(g2d, shape, paint, paintBounds, stroke);
	}

}
