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
 * <p>Helpers used throughout the library. Most are static utility classes; they
 * are public because renderers outside the library need them, not because they
 * are a curated API.</p>
 *
 * <ul>
 *   <li>{@link de.erichseifert.gral.util.MathUtils} &ndash; rounding to a
 *   multiple, limiting to a range, floating-point comparison with an epsilon,
 *   binary search, and quantile computation.</li>
 *   <li>{@link de.erichseifert.gral.util.GeometryUtils} &ndash; intersections,
 *   shape subtraction, and iteration over the segments of a
 *   {@code java.awt.Shape}.</li>
 *   <li>{@link de.erichseifert.gral.util.GraphicsUtils} &ndash; deriving
 *   lighter and darker colors, color space conversion, and filling or stroking
 *   a shape with a paint aligned to given bounds.</li>
 *   <li>{@link de.erichseifert.gral.util.DataUtils} &ndash; small conversions
 *   between arrays, lists and maps, and a null-safe way of turning a
 *   {@code Number} into a {@code double}.</li>
 *   <li>{@link de.erichseifert.gral.util.PointND} &ndash; a point with an
 *   arbitrary number of dimensions, used by the axis renderers, which are not
 *   restricted to two.</li>
 *   <li>{@link de.erichseifert.gral.util.HaltonSequence} &ndash; a
 *   quasi-random sequence; it is what gives
 *   {@link de.erichseifert.gral.plots.colors.QuasiRandomColors} its evenly
 *   spread hues.</li>
 *   <li>{@link de.erichseifert.gral.util.StatefulTokenizer} &ndash; the small
 *   state machine behind the CSV parser.</li>
 *   <li>{@link de.erichseifert.gral.util.Messages} &ndash; lookup of
 *   translatable, user-facing strings.</li>
 *   <li>{@link de.erichseifert.gral.util.WindowIterator} and
 *   {@link de.erichseifert.gral.util.ConcatenationIterator} &ndash; the
 *   iterators the newer filters are built from.</li>
 * </ul>
 */
package de.erichseifert.gral.util;
