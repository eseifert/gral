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
 * <p>JavaFX components that display a
 * {@link de.erichseifert.gral.graphics.Drawable}.</p>
 *
 * <p>This is the whole of {@code de.erichseifert.gral:gral-javafx}. Nothing in
 * the core depends on it, and it is the counterpart of
 * {@code de.erichseifert.gral:gral-swing}: the same plot, the same
 * {@link de.erichseifert.gral.navigation.Navigator}, a different toolkit. The
 * painting goes through
 * <a href="https://github.com/jfree/fxgraphics2d">FXGraphics2D</a>, which
 * implements {@code java.awt.Graphics2D} on top of a JavaFX canvas.</p>
 *
 * <p>{@link de.erichseifert.gral.javafx.DrawableCanvas} is a resizable
 * {@code Canvas} that paints one drawable:</p>
 * <pre>
 * StackPane root = new StackPane(new DrawableCanvas(plot));
 * stage.setScene(new Scene(root, 800.0, 600.0));
 * stage.show();
 * </pre>
 *
 * <p>{@link de.erichseifert.gral.javafx.InteractiveCanvas} extends it with
 * dragging to pan and scrolling or double-clicking to zoom.</p>
 *
 * <p>JavaFX itself is not a dependency of this module: it is expected on the
 * class or module path of the application, which decides the version and the
 * platform it runs on.</p>
 */
package de.erichseifert.gral.javafx;
