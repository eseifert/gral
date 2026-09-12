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
 * <p>Reading and writing {@link de.erichseifert.gral.data.DataSource}s.</p>
 *
 * <p>Instances are obtained from
 * {@link de.erichseifert.gral.io.data.DataReaderFactory} and
 * {@link de.erichseifert.gral.io.data.DataWriterFactory} rather than
 * constructed directly, since that is what makes the format pluggable:</p>
 *
 * <pre>
 * DataReader reader = DataReaderFactory.getInstance().get("text/csv");
 * DataSource data;
 * try (InputStream in = new FileInputStream("values.csv")) {
 *     // The column types have to be stated: a text file does not carry them.
 *     data = reader.read(in, Double.class, Double.class);
 * }
 *
 * DataWriter writer = DataWriterFactory.getInstance().get("text/csv");
 * try (OutputStream out = new FileOutputStream("copy.csv")) {
 *     writer.write(data, out);
 * }
 * </pre>
 *
 * <p>Supported formats:</p>
 * <ul>
 *   <li>{@link de.erichseifert.gral.io.data.CSVReader} and
 *   {@link de.erichseifert.gral.io.data.CSVWriter} for
 *   {@code text/csv} and {@code text/tab-separated-values}. Values that contain
 *   the separator, a quote or a line break are quoted on writing and unquoted
 *   on reading.</li>
 *   <li>{@link de.erichseifert.gral.io.data.ImageReader} and
 *   {@link de.erichseifert.gral.io.data.ImageWriter} for the bitmap types
 *   {@code image/png}, {@code image/jpeg}, {@code image/bmp},
 *   {@code image/gif} and {@code image/vnd.wap.wbmp}. One pixel becomes one
 *   cell, so an image turns into a table that
 *   {@link de.erichseifert.gral.plots.RasterPlot} can display.</li>
 *   <li>{@link de.erichseifert.gral.io.data.AudioReader} for {@code audio/wav},
 *   which yields one column of sample values.</li>
 * </ul>
 *
 * <p>Readers and writers are configured through named settings, for example the
 * column separator of a CSV file; see
 * {@link de.erichseifert.gral.io.data.AbstractDataReader#setSetting(String, Object)}.
 * Writing plots rather than data is done by
 * {@link de.erichseifert.gral.io.plots} instead.</p>
 */
package de.erichseifert.gral.io.data;
