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
package de.erichseifert.gral.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;

import javax.swing.JPanel;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;

/**
 * A class that represents an adapter between the components of this library
 * and Swing. It displays a single <code>Drawable</code> in a
 * <code>JPanel</code>.
 */
public class DrawablePanel extends JPanel {
	/** Unique id for serialization. */
	private static final long serialVersionUID = 1L;
	/** Drawable that should be displayed. */
	private final Drawable drawable;

	/**
	 * Creates a new DrawablePanel with the specified <code>Drawable</code>.
	 * @param drawable <code>Drawable</code> to be displayed
	 */
	public DrawablePanel(Drawable drawable) {
		this.drawable = drawable;
		setOpaque(false);
	}

	/**
	 * Returns the <code>Drawable</code> instance that is displayed by this
	 * panel.
	 * @return <code>Drawable</code> instance
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g;
		getDrawable().draw(new DrawingContext(graphics));
	}

	@Override
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		getDrawable().setBounds(bounds);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		getDrawable().setBounds(0.0, 0.0, width, height);
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
