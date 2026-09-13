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

/**
 * <p>The drawing primitives that every visible part of a plot is built
 * from.</p>
 *
 * <p>{@link de.erichseifert.gral.graphics.Drawable} is a lightweight component:
 * it has bounds, a preferred size, and a
 * {@link de.erichseifert.gral.graphics.Drawable#draw(de.erichseifert.gral.graphics.DrawingContext)
 * draw} method. It deliberately does <em>not</em> extend
 * {@code java.awt.Component}, which is what allows the same object to be
 * painted into a Swing panel, an image, a PDF, or a printer page.
 * {@link de.erichseifert.gral.graphics.AbstractDrawable} implements the bounds
 * handling, so subclasses only have to supply {@code draw} and usually
 * {@code getPreferredSize}.</p>
 *
 * <pre>
 * Drawable cross = new AbstractDrawable() {
 *     public void draw(DrawingContext context) {
 *         Graphics2D g = context.getGraphics();
 *         g.draw(new Line2D.Double(getX(), getY(),
 *             getX() + getWidth(), getY() + getHeight()));
 *     }
 * };
 * cross.setBounds(0.0, 0.0, 100.0, 50.0);
 * cross.draw(new DrawingContext(graphics2D));
 * </pre>
 *
 * <p>{@link de.erichseifert.gral.graphics.DrawingContext} carries the
 * {@code Graphics2D} to paint on together with two hints: a
 * {@link de.erichseifert.gral.graphics.DrawingContext.Target} that says whether
 * the output is a pixel raster or a vector document, and a
 * {@link de.erichseifert.gral.graphics.DrawingContext.Quality} level. Renderers
 * check the target to decide, for example, whether a gradient can be emitted as
 * vector data or has to be rasterized first.</p>
 *
 * <p>{@link de.erichseifert.gral.graphics.DrawableContainer} holds child
 * drawables and positions them with a
 * {@link de.erichseifert.gral.graphics.layout.Layout}; this is the base class of
 * {@link de.erichseifert.gral.plots.AbstractPlot}.
 * {@link de.erichseifert.gral.graphics.Label} draws a rotatable, alignable
 * piece of text and is used for plot titles, axis labels and legend entries.
 * {@link de.erichseifert.gral.graphics.Insets2D},
 * {@link de.erichseifert.gral.graphics.Dimension2D},
 * {@link de.erichseifert.gral.graphics.Location} and
 * {@link de.erichseifert.gral.graphics.Orientation} are the small geometry
 * types that the AWT lacks in {@code double} precision or at all.</p>
 *
 * @see de.erichseifert.gral.graphics.layout
 */
package de.erichseifert.gral.graphics;
