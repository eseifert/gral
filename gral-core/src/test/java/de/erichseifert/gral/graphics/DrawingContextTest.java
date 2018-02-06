/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
package de.erichseifert.gral.graphics;

import static org.junit.Assert.assertEquals;

import java.awt.Graphics2D;
import java.awt.Image;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.DrawingContext.Quality;
import de.erichseifert.gral.graphics.DrawingContext.Target;


public class DrawingContextTest {
	private Image testImage;
	private Graphics2D graphics;
	private DrawingContext context;

	@Before
	public void setUp() {
		testImage = TestUtils.createTestImage();
		graphics = (Graphics2D) testImage.getGraphics();
		context = new DrawingContext(graphics);
	}

	@Test
	public void testCreateDefault() {
		assertEquals(graphics, context.getGraphics());
		assertEquals(Quality.NORMAL, context.getQuality());
		assertEquals(Target.BITMAP, context.getTarget());
	}

	@Test
	public void testCreateParams() {
		DrawingContext context = new DrawingContext(graphics, Quality.QUALITY, Target.VECTOR);
		assertEquals(graphics, context.getGraphics());
		assertEquals(Quality.QUALITY, context.getQuality());
		assertEquals(Target.VECTOR, context.getTarget());
	}
}
