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
 * <p>Unit tests, mirroring the package structure of the main sources. Each
 * package has a {@code *Tests} suite class, and
 * {@link de.erichseifert.gral.AllTests} aggregates all of them.</p>
 *
 * <p>{@link de.erichseifert.gral.TestUtils} holds the shared helpers and, with
 * them, the local conventions: rendering is checked by drawing into a test
 * image and asserting that pixels were painted rather than by comparing against
 * reference images, and anything serializable gets a round-trip test. There is
 * no mocking framework; tests use
 * {@link de.erichseifert.gral.data.DummyData} and small local subclasses
 * instead.</p>
 */
package de.erichseifert.gral;
