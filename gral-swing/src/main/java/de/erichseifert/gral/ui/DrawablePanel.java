/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;

import javax.swing.JPanel;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;

/**
 * <p>A {@code JPanel} that displays a single {@link Drawable}. This is the
 * bridge between GRAL and Swing: the panel keeps the bounds of the drawable in
 * step with its own size and paints it on every repaint.</p>
 *
 * <pre>
 * JFrame frame = new JFrame();
 * frame.getContentPane().add(new DrawablePanel(plot));
 * frame.setSize(800, 600);
 * frame.setVisible(true);
 * </pre>
 *
 * <p>The panel is non-opaque and its preferred size is that of the drawable, so
 * {@code pack()} sizes a window to the plot. The drawable is fixed for the
 * lifetime of the panel; to display a different one, create a new panel.</p>
 *
 * <p>Use {@link InteractivePanel} instead if zooming, panning, printing and
 * export are wanted.</p>
 */
public class DrawablePanel extends JPanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1036506991203257170L;

	/** Drawable that should be displayed. */
	private final Drawable drawable;

	/** Defines whether this panel uses antialiasing. */
	private boolean antialiased;

	/**
	 * Initializes a new panel showing the specified drawable. Antialiasing is
	 * enabled by default.
	 * @param drawable {@code Drawable} to be displayed
	 */
	public DrawablePanel(Drawable drawable) {
		this.drawable = drawable;
		setOpaque(false);
		antialiased = true;
	}

	/**
	 * Returns the {@code Drawable} instance that is displayed by this
	 * panel.
	 * @return {@code Drawable} instance
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (isVisible()) {
			Graphics2D graphics = (Graphics2D) g;
			if (isAntialiased()) {
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}

			getDrawable().draw(new DrawingContext(graphics));
		}
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

	/**
	 * Returns whether antialiasing is applied.
	 * @return {@code true} if the panel uses antialiasing, {@code false} otherwise.
	 */
	public boolean isAntialiased() {
		return antialiased;
	}

	/**
	 * Sets whether antialiasing should be applied.
	 * @param antialiased {@code true} if the panel should use antialiasing, {@code false} otherwise.
	 */
	public void setAntialiased(boolean antialiased) {
		this.antialiased = antialiased;
	}
}
