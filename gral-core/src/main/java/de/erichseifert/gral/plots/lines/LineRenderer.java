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
package de.erichseifert.gral.plots.lines;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.DataPoint;


/**
 * <p>Connects the data points of a series with a line. The plot projects the
 * rows first and hands over the resulting {@link DataPoint}s, so a line
 * renderer works purely in view coordinates and never touches the axes.</p>
 *
 * <p>Rendering happens in two steps: {@link #getLineShape(List)} builds the
 * geometry, and {@link #getLine(List, Shape)} wraps it in a {@link Drawable}
 * that strokes it. Splitting them allows a caller to reuse or modify the shape
 * &mdash; which is what an {@link de.erichseifert.gral.plots.areas.AreaRenderer}
 * does.</p>
 *
 * <pre>
 * DefaultLineRenderer2D line = new DefaultLineRenderer2D();
 * line.setColor(Color.BLACK);
 * line.setStroke(new BasicStroke(2f));
 * // Leave a hole of 2 pixels around each data point so the marks stay visible.
 * line.setGap(2.0);
 * line.setGapRounded(true);
 * plot.setLineRenderers(series, line);
 * </pre>
 *
 * <p>A series has no line at all unless a renderer is set for it. Like the
 * other renderers, one instance serves every point of a series and must not
 * keep per-point state; implementations normally extend
 * {@link AbstractLineRenderer2D}.</p>
 */
public interface LineRenderer {
	/**
	 * Returns the geometric shape for this line.
	 * @param points Points used for creating the line.
	 * @return Geometric shape for this line.
	 */
	Shape getLineShape(List<DataPoint> points);

	/**
	 * Returns a graphical representation for the line defined by
	 * {@code points}.
	 * @param points Points to be used for creating the line.
	 * @param shape Geometric shape for this line.
	 * @return Representation of the line.
	 */
	Drawable getLine(List<DataPoint> points, Shape shape);

	/**
	 * Returns the stroke to be used to define the line shape.
	 * @return Stroke used for drawing.
	 */
	Stroke getStroke();

	/**
	 * Sets the stroke to be used to define the line shape.
	 * @param stroke Stroke used for drawing.
	 */
	void setStroke(Stroke stroke);

	/**
	 * Returns the value for the gap between the line and a point.
	 * If the gap value is equal to or smaller than 0 no gap will be used.
	 * @return Gap size between drawn line and connected points in pixels.
	 */
	double getGap();

	/**
	 * Sets the value for the gap between the line and a point.
	 * If the gap value is equal to or smaller than 0 no gap will be used.
	 * @param gap Gap size between drawn line and connected points in pixels.
	 */
	void setGap(double gap);

	/**
	 * Returns whether the gaps should have rounded corners.
	 * @return {@code true} if the gap corners should be rounded.
	 */
	boolean isGapRounded();

	/**
	 * Sets whether the gaps should have rounded corners.
	 * @param gapRounded {@code true} if the gap corners should be rounded.
	 */
	void setGapRounded(boolean gapRounded);

	/**
	 * Returns the paint to be used to paint the line shape.
	 * @return Paint for line drawing.
	 */
	Paint getColor();

	/**
	 * Sets the paint to be used to paint the line shape.
	 * @param color Paint for line drawing.
	 */
	void setColor(Paint color);
}
