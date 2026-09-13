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
 * <p>The JavaFX example browser: the same window as the Swing one of
 * {@code gral-examples}, listing the same examples.</p>
 *
 * <p>That is the point of it. The examples themselves are in
 * {@code de.erichseifert.gral.examples} and name no toolkit; only
 * {@code Browser} and {@code ExampleView} here are JavaFX, and all the latter
 * does is put the drawable of an example into a
 * {@link de.erichseifert.gral.javafx.InteractiveCanvas}.</p>
 */
package de.erichseifert.gral.examples.javafx;
