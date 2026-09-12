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
 * <p>Writing a {@link de.erichseifert.gral.graphics.Drawable} &mdash; usually a
 * plot &mdash; to an image or document file.</p>
 *
 * <pre>
 * DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/png");
 * try (OutputStream out = new FileOutputStream("plot.png")) {
 *     writer.write(plot, out, 800.0, 600.0);
 * }
 * </pre>
 *
 * <p>The writer sets the bounds of the drawable to the requested size for the
 * duration of the call and restores them afterwards, so the same plot object
 * can be displayed on screen and exported at a different size without
 * interference. No window and no display are involved, which makes this the
 * path to use on a headless machine.</p>
 *
 * <p>{@link de.erichseifert.gral.io.plots.BitmapWriter} renders into a
 * {@code BufferedImage} and encodes it with {@code javax.imageio}; it handles
 * {@code image/png} (with transparency), {@code image/jpeg}, {@code image/bmp},
 * {@code image/gif} and {@code image/vnd.wap.wbmp}.</p>
 *
 * <p>{@link de.erichseifert.gral.io.plots.VectorWriter} produces
 * {@code application/pdf}, {@code application/postscript} (EPS) and
 * {@code image/svg+xml}. It needs the VectorGraphics2D library at run time and
 * talks to it purely by reflection: GRAL compiles without it, and a missing
 * library surfaces as an exception when a vector format is written, not at
 * class-loading time. Either the original {@code de.erichseifert.vectorgraphics2d}
 * or the Eclipse SWTChart fork {@code org.eclipse.swtchart.vectorgraphics2d}
 * will do.</p>
 */
package de.erichseifert.gral.io.plots;
