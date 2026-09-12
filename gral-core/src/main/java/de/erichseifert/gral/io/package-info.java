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
 * <p>The plug-in registry that input and output formats are looked up in.</p>
 *
 * <p>An {@link de.erichseifert.gral.io.IOFactory} maps a MIME type to a reader
 * or writer object. There are three factories, one per kind of object:
 * {@link de.erichseifert.gral.io.data.DataReaderFactory},
 * {@link de.erichseifert.gral.io.data.DataWriterFactory} and
 * {@link de.erichseifert.gral.io.plots.DrawableWriterFactory}.</p>
 *
 * <pre>
 * DataWriterFactory factory = DataWriterFactory.getInstance();
 * if (factory.isFormatSupported("text/csv")) {
 *     DataWriter writer = factory.get("text/csv");
 *     …
 * }
 * </pre>
 *
 * <p>{@link de.erichseifert.gral.io.AbstractIOFactory} builds the mapping by
 * reading every properties file of a given name that is visible on the
 * classpath &mdash; {@code datareaders.properties},
 * {@code datawriters.properties} or {@code drawablewriters.properties} &mdash;
 * and instantiating the named classes by reflection. Each line maps one MIME
 * type to one class name. Supporting a new format therefore means writing the
 * class <em>and</em> adding a line to the matching properties file; because all
 * copies of the file on the classpath are read, a separate JAR can contribute
 * formats without touching GRAL.</p>
 *
 * <p>What a format can do is described by
 * {@link de.erichseifert.gral.io.IOCapabilities} objects. A reader or writer
 * registers them in a static initializer through
 * {@link de.erichseifert.gral.io.IOCapabilitiesStorage}, and the factory
 * retrieves them by reflectively calling the static {@code getCapabilities()}
 * method that {@code IOCapabilitiesStorage} provides &mdash; which is how the
 * list of supported formats can be built without loading a file first.</p>
 */
package de.erichseifert.gral.io;
