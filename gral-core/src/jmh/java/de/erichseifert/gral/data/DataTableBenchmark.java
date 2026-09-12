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
package de.erichseifert.gral.data;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * <p>Measures how long it takes to append a row to a {@link DataTable}. Adding
 * rows is the hot path when a plot is fed from live data, and it does more than
 * a list insertion: the number of values and each of their types are checked
 * against the columns, and one {@link DataChangeEvent} per column is built and
 * dispatched to the listeners.</p>
 *
 * <p>Run with {@code ./gradlew :gral-core:jmh}.</p>
 */
@State(Scope.Benchmark)
public class DataTableBenchmark {
	/** Table that rows are appended to. */
	private DataTable table;

	/**
	 * Creates the table that the benchmark appends to, once per trial.
	 */
	@Setup(Level.Trial)
	public void createTable() {
		table = new DataTable(6, Double.class);
	}

	/**
	 * Empties the table after each iteration, so that measurements are not
	 * distorted by a table that keeps growing.
	 */
	@TearDown(Level.Iteration)
	public void clearTable() {
		table.clear();
	}

	/**
	 * Appends one row of six values to the table.
	 */
	@Benchmark
	public void addRecord() {
		table.add(0.0, 1.0, 2.0, 3.0, 4.0, 5.0);
	}
}
