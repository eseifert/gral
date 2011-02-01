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
package de.erichseifert.gral;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.SettingsListener;


/**
 * Abstract class that represents a canvas on which plot data will be drawn.
 * It serves as base for specialized implementations for different plot types.
 * Derived classes have to implement how the actual drawing is done.
 */
public abstract class PlotArea extends AbstractDrawable
		implements SettingsListener {
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the background of the plot area. */
	public static final Key BACKGROUND =
		new Key("plotarea.background"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to
	paint the border of the plot area. */
	public static final Key BORDER =
		new Key("plotarea.border"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	fill the border of the plot area. */
	public static final Key COLOR =
		new Key("plotarea.color"); //$NON-NLS-1$
	/** Key for specifying an {@link de.erichseifert.gral.util.Insets2D}
	instance that defines the clipping of the plotted data relative to
	the plot area. Positive inset values result in clipping inside the plot
	area, negative values result in clipping outside the plot area.
	Specifying a <code>null</code> values will turn off clipping. */
	public static final Key CLIPPING =
		new Key("plotarea.clipping"); //$NON-NLS-1$

	/**
	 * Creates a new <code>PlotArea2D</code> object with default
	 * background color and border.
	 */
	public PlotArea() {
		addSettingsListener(this);
		setSettingDefault(BACKGROUND, Color.WHITE);
		setSettingDefault(BORDER, new BasicStroke(1f));
		setSettingDefault(COLOR, Color.BLACK);
		setSettingDefault(CLIPPING, new Insets2D.Double(0.0));
	}

	/**
	 * Draws the background of this Legend with the specified Graphics2D
	 * object.
	 * @param context Environment used for drawing.
	 */
	protected void drawBackground(DrawingContext context) {
		// FIXME duplicate code! See de.erichseifert.gral.Legend
		Paint paint = getSetting(BACKGROUND);
		if (paint != null) {
			GraphicsUtils.fillPaintedShape(context.getGraphics(),
					getBounds(), paint, null);
		}
	}

	/**
	 * Draws the border of this Legend with the specified Graphics2D
	 * object.
	 * @param context Environment used for drawing.
	 */
	protected void drawBorder(DrawingContext context) {
		// FIXME duplicate code! See de.erichseifert.gral.Legend
		Stroke stroke = getSetting(BORDER);
		if (stroke != null) {
			Paint paint = getSetting(COLOR);
			GraphicsUtils.drawPaintedShape(context.getGraphics(),
					getBounds(), paint, null, stroke);
		}
	}

	/**
	 * Draws the data using the specified Graphics2D object.
	 * @param context Environment used for drawing.
	 */
	protected abstract void drawPlot(DrawingContext context);

	@Override
	public void settingChanged(SettingChangeEvent event) {
	}
}
