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

package openjchart;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;

import javax.swing.JPanel;

/**
 * A class that represents an adapter between the components of this library and Swing.
 * It displays a single <code>Drawable</code> in a <code>JPanel</code>.
 */
public class DrawablePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final Drawable drawable;

	/**
	 * Creates a new DrawablePanel with the specified <code>Drawable</code>.
	 * @param drawable <code>Drawable</code> to be displayed
	 */
	public DrawablePanel(Drawable drawable) {
		this.drawable = drawable;
		setOpaque(false);
	}

	public Drawable getDrawable() {
		return drawable;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		getDrawable().draw((Graphics2D)g);
	}

	@Override
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		getDrawable().setBounds(bounds);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		getDrawable().setBounds(x, y, width, height);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dims = super.getPreferredSize();
		Dimension2D dimsPlot = getDrawable().getPreferredSize();
		dims.setSize(dimsPlot);
		return dims;
	}

	@Override
	public Dimension getMinimumSize() {
		return super.getPreferredSize();
	}
}
