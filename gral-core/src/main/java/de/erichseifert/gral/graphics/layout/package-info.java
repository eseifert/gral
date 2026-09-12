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
 * <p>Layout managers that position the children of a
 * {@link de.erichseifert.gral.graphics.Container}.</p>
 *
 * <p>These are GRAL's own layouts rather than AWT's, because a
 * {@link de.erichseifert.gral.graphics.Drawable} is not a
 * {@code java.awt.Component}. A {@link de.erichseifert.gral.graphics.layout.Layout}
 * does two things: report the preferred size of a container, and assign bounds
 * to each of its children. Where a child goes is decided by the constraint
 * object that was passed when the child was added; what kind of object that is
 * depends on the layout.</p>
 *
 * <ul>
 *   <li>{@link de.erichseifert.gral.graphics.layout.EdgeLayout} &ndash; like
 *   {@code java.awt.BorderLayout}, but with corners as well. The constraint is
 *   a {@link de.erichseifert.gral.graphics.Location}; the component at
 *   {@code CENTER} takes the space the others leave over. This is the layout a
 *   plot uses for its plot area, title, legend and axes.</li>
 *   <li>{@link de.erichseifert.gral.graphics.layout.OuterEdgeLayout} &ndash;
 *   the same idea, but the components are placed <em>outside</em> the
 *   container's bounds. This is how a legend is put beside a plot without
 *   shrinking it.</li>
 *   <li>{@link de.erichseifert.gral.graphics.layout.StackedLayout} &ndash;
 *   arranges components in a horizontal or vertical row. Its
 *   {@link de.erichseifert.gral.graphics.layout.StackedLayout.Constraints}
 *   control alignment and whether a component is stretched across the other
 *   axis.</li>
 *   <li>{@link de.erichseifert.gral.graphics.layout.TableLayout} &ndash; a grid
 *   with a fixed number of columns, like {@code java.awt.GridLayout} except
 *   that rows and columns keep their own sizes.</li>
 * </ul>
 *
 * <pre>
 * DrawableContainer container = new DrawableContainer(new EdgeLayout(5.0, 5.0));
 * container.add(plot, Location.CENTER);
 * container.add(caption, Location.SOUTH);
 * container.setBounds(0.0, 0.0, 800.0, 600.0);
 * container.draw(context);
 * </pre>
 *
 * <p>All layouts honor the {@link de.erichseifert.gral.graphics.Insets2D} of
 * the container and the horizontal and vertical gaps configured on the layout
 * itself.</p>
 */
package de.erichseifert.gral.graphics.layout;
