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
package de.erichseifert.gral.plots.legends;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.graphics.Container;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.Orientation;

/**
 * Interface for a legend that display visual examples of the variables used in
 * a plot.
 */
public interface Legend extends Container, Drawable {
	/**
	 * Adds the specified data source in order to display it.
	 * @param source data source to be added.
	 */
	void add(DataSource source);

	/**
	 * Returns whether the specified data source was added to the legend.
	 * @param source Data source
	 * @return {@code true} if legend contains the data source, otherwise {@code false}
	 */
	boolean contains(DataSource source);

	/**
	 * Removes the specified data source.
	 * @param source Data source to be removed.
	 */
	void remove(DataSource source);

	/**
	 * Removes all data sources from the legend.
	 */
	void clear();

	/**
	 * Returns the current font used as a default for sub-components ans for
	 * calculation of relative sizes.
	 * @return Current base font.
	 */
	Font getBaseFont();

	/**
	 * Sets the new font that will be used as a default for sub-components and
	 * for calculation of relative sizes. This method is only used internally
	 * to propagate the base font and shouldn't be used manually.
	 * @param baseFont New base font.
	 */
	void setBaseFont(Font baseFont);

	/**
	 * Returns the paint used to draw the background.
	 * @return Paint used for background drawing.
	 */
	Paint getBackground();

	/**
	 * Sets the paint used to draw the background.
	 * @param background Paint used for background drawing.
	 */
	void setBackground(Paint background);

	/**
	 * Returns the stroke used to draw the border of the legend.
	 * @return Stroke used for border drawing.
	 */
	Stroke getBorderStroke();

	/**
	 * Sets the stroke used to draw the border of the legend.
	 * @param borderStroke Stroke used for border drawing.
	 */
	void setBorderStroke(Stroke borderStroke);

	/**
	 * Returns the font used to display the labels.
	 * @return Font used for labels.
	 */
	Font getFont();

	/**
	 * Sets the font used to display the labels.
	 * @param font Font used for labels.
	 */
	void setFont(Font font);

	/**
	 * Returns the paint used to fill the border of the legend.
	 * @return Paint used for border drawing.
	 */
	Paint getBorderColor();

	/**
	 * Sets the paint used to fill the border of the legend.
	 * @param borderColor Paint used for border drawing.
	 */
	void setBorderColor(Paint borderColor);

	/**
	 * Returns the direction of the legend's items.
	 * @return Item orientation.
	 */
	Orientation getOrientation();

	/**
	 * Sets the direction of the legend's items.
	 * @param orientation Item orientation.
	 */
	void setOrientation(Orientation orientation);

	/**
	 * Returns the size of the legend's symbols.
	 * @return Symbol size relative to the font height.
	 */
	Dimension2D getSymbolSize();

	/**
	 * Sets the size of the legend's symbols.
	 * @param symbolSize Symbol size relative to the font height.
	 */
	void setSymbolSize(Dimension2D symbolSize);

	/**
	 * Returns the horizontal alignment of the legend relative to the plot area.
	 * {@code 0.0} means left, {@code 0.5} means centered, and {@code 1.0} means right.
	 * @return Relative horizontal alignment.
	 */
	double getAlignmentX();

	/**
	 * Sets the horizontal alignment of the legend relative to the plot area.
	 * {@code 0.0} means left, {@code 0.5} means centered, and {@code 1.0} means right.
	 * @param alignmentX Relative horizontal alignment.
	 */
	void setAlignmentX(double alignmentX);

	/**
	 * Returns the vertical alignment of the legend relative to the plot area.
	 * {@code 0.0} means top, {@code 0.5} means centered, and {@code 1.0} means bottom.
	 * @return Relative vertical alignment.
	 */
	double getAlignmentY();

	/**
	 * Sets the vertical alignment of the legend relative to the plot area.
	 * {@code 0.0} means top, {@code 0.5} means centered, and {@code 1.0} means bottom.
	 * @param alignmentY Relative vertical alignment.
	 */
	void setAlignmentY(double alignmentY);

	/**
	 * Returns the horizontal and vertical gap between items.
	 * @return Gap size relative to the font height.
	 */
	Dimension2D getGap();

	/**
	 * Sets the horizontal and vertical gap between items.
	 * @param gap Gap size relative to the font height.
	 */
	void setGap(Dimension2D gap);
}
