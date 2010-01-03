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

package openjchart.tests;

import static org.junit.Assert.assertEquals;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import openjchart.Drawable;
import openjchart.DrawableContainer;
import openjchart.DrawablePanel;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DrawablePanelTest {
	private static final double DELTA = 1e-15;
	private static Drawable drawable;
	private DrawablePanel panel;

	@BeforeClass
	public static void setUpBeforeClass() {
		drawable = new DrawableContainer();
	}

	@Before
	public void setUp() {
		panel = new DrawablePanel(drawable);
	}

	@Test
	public void testBounds() {
		Rectangle bounds;
		// Get
		bounds = panel.getBounds();
		assertEquals(0.0, bounds.getX(), DELTA);
		assertEquals(0.0, bounds.getY(), DELTA);
		assertEquals(0.0, bounds.getWidth(), DELTA);
		assertEquals(0.0, bounds.getHeight(), DELTA);
		assertEquals(bounds.getX(), panel.getX(), DELTA);
		assertEquals(bounds.getY(), panel.getY(), DELTA);
		assertEquals(bounds.getWidth(), panel.getWidth(), DELTA);
		assertEquals(bounds.getHeight(), panel.getHeight(), DELTA);
		// Set Rectangle object
		panel.setBounds(new Rectangle(1, 2, 10, 20));
		bounds = panel.getBounds();
		assertEquals( 1.0, bounds.getX(), DELTA);
		assertEquals( 2.0, bounds.getY(), DELTA);
		assertEquals(10.0, bounds.getWidth(), DELTA);
		assertEquals(20.0, bounds.getHeight(), DELTA);
		// Set values
		panel.setBounds(3, 4, 30, 40);
		bounds = panel.getBounds();
		assertEquals( 3.0, bounds.getX(), DELTA);
		assertEquals( 4.0, bounds.getY(), DELTA);
		assertEquals(30.0, bounds.getWidth(), DELTA);
		assertEquals(40.0, bounds.getHeight(), DELTA);
	}

	@Test
	public void testDraw() {
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		panel.setBounds(0, 0, image.getWidth(), image.getHeight());
		panel.paint(g2d);
	}

}
