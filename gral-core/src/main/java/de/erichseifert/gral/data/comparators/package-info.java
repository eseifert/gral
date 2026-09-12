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
 * <p>Comparators for sorting the rows of a
 * {@link de.erichseifert.gral.data.DataTable}.</p>
 *
 * <p>A {@link de.erichseifert.gral.data.comparators.DataComparator} compares
 * two {@link de.erichseifert.gral.data.Record}s by the value in one column.
 * {@link de.erichseifert.gral.data.comparators.Ascending} and
 * {@link de.erichseifert.gral.data.comparators.Descending} are the two
 * implementations. Passing several of them to
 * {@link de.erichseifert.gral.data.DataTable#sort(
 * de.erichseifert.gral.data.comparators.DataComparator...)}
 * sorts by the first column and uses the remaining ones to break ties:</p>
 *
 * <pre>
 * // Sort by column 0 ascending, and equal values by column 2 descending.
 * data.sort(new Ascending(0), new Descending(2));
 * </pre>
 *
 * <p>{@code null} values are ordered as if they were larger than any non-null
 * value, in both directions.</p>
 */
package de.erichseifert.gral.data.comparators;
