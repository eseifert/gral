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
 * <p>Swing components that display a
 * {@link de.erichseifert.gral.graphics.Drawable}.</p>
 *
 * <p>This is the only package of the library that depends on
 * {@code javax.swing}, and nothing else in the library depends on this package
 * &mdash; plots can be created, drawn and exported without ever loading it.</p>
 *
 * <p>{@link de.erichseifert.gral.ui.DrawablePanel} is a {@code JPanel} that
 * paints one drawable and keeps its bounds in step with the panel size:</p>
 * <pre>
 * JFrame frame = new JFrame();
 * frame.getContentPane().add(new DrawablePanel(plot));
 * frame.setSize(800, 600);
 * frame.setVisible(true);
 * </pre>
 *
 * <p>{@link de.erichseifert.gral.ui.InteractivePanel} extends it with the
 * behavior expected of a plot on screen: dragging pans, the mouse wheel and a
 * double click zoom, and a right click opens a context menu offering to reset
 * the view, to print, and to export to any format registered with
 * {@link de.erichseifert.gral.io.plots.DrawableWriterFactory}. Panning and
 * zooming are only available for drawables that are
 * {@link de.erichseifert.gral.navigation.Navigable}, which plots are.</p>
 *
 * <p>{@link de.erichseifert.gral.ui.ExportDialog},
 * {@link de.erichseifert.gral.ui.ExportChooser} and
 * {@link de.erichseifert.gral.ui.DrawableWriterFilter} are the dialogs behind
 * that export action and can also be used on their own.</p>
 */
package de.erichseifert.gral.ui;
