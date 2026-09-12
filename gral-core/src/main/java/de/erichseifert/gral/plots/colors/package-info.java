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
 * <p>Mappings from numbers to {@code java.awt.Paint}, used wherever the
 * appearance of an element should depend on a value.</p>
 *
 * <p>{@link de.erichseifert.gral.plots.colors.ColorMapper} is the common
 * interface; in practice one of the two abstract subclasses is extended:</p>
 * <ul>
 *   <li>{@link de.erichseifert.gral.plots.colors.ContinuousColorMapper} maps a
 *   {@code double} and is the right base for gradients. Implementations
 *   include {@link de.erichseifert.gral.plots.colors.Grayscale},
 *   {@link de.erichseifert.gral.plots.colors.RainbowColors},
 *   {@link de.erichseifert.gral.plots.colors.HeatMap},
 *   {@link de.erichseifert.gral.plots.colors.LinearGradient} and the constant
 *   {@link de.erichseifert.gral.plots.colors.SingleColor}.</li>
 *   <li>{@link de.erichseifert.gral.plots.colors.IndexedColorMapper} maps an
 *   {@code int} and is the right base for palettes of distinct colors, for
 *   example {@link de.erichseifert.gral.plots.colors.IndexedColors},
 *   {@link de.erichseifert.gral.plots.colors.RandomColors} and
 *   {@link de.erichseifert.gral.plots.colors.QuasiRandomColors}, which spreads
 *   hues with a Halton sequence so that neighboring colors stay
 *   distinguishable.</li>
 * </ul>
 *
 * <p>A continuous mapper normally works on a fixed input range &mdash;
 * {@link de.erichseifert.gral.plots.colors.ScaledContinuousColorMapper}
 * rescales values into it &mdash; and a
 * {@link de.erichseifert.gral.plots.colors.ColorMapper.Mode} decides what
 * happens to values outside that range: {@code OMIT} returns {@code null} and
 * the element is not drawn, {@code REPEAT} clamps to the nearest end, and
 * {@code CIRCULAR} wraps around.</p>
 *
 * <pre>
 * HeatMap colors = new HeatMap();
 * colors.setRange(0.0, 100.0);
 * colors.setMode(ColorMapper.Mode.CIRCULAR);
 * pointRenderer.setColor(colors);
 * </pre>
 *
 * <p>The default mode is {@code REPEAT}. Note that the mode is only settable on
 * mappers that re-declare {@code setMode} as public, which the shipped ones
 * do.</p>
 */
package de.erichseifert.gral.plots.colors;
