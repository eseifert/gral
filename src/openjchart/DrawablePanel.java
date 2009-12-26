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

package openjchart;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;

import javax.swing.JPanel;

/**
 * A class that represents an adapter between the components of this library and swing.
 * It displays a single Drawable in a JPanel.
 */
public class DrawablePanel extends JPanel {
	private final Drawable drawable;

	/**
	 * Creates a new DrawablePanel with the specified Drawable.
	 * @param drawable Drawable to be displayer
	 */
	public DrawablePanel(Drawable drawable) {
		this.drawable = drawable;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawable.draw((Graphics2D)g);
	}

	@Override
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		drawable.setBounds(bounds);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		drawable.setBounds(x, y, width, height);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dims = super.getPreferredSize();
		Dimension2D dimsPlot = drawable.getPreferredSize();
		dims.setSize(dimsPlot);
		return dims;
	}

	@Override
	public Dimension getMinimumSize() {
		return super.getPreferredSize();
	}
}
