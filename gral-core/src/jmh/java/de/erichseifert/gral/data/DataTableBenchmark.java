package de.erichseifert.gral.data;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class DataTableBenchmark {
	private DataTable table;

	@Setup(Level.Trial)
	public void createTable() {
		table = new DataTable(6, Double.class);
	}

	@TearDown(Level.Iteration)
	public void clearTable() {
		table.clear();
	}

	@Benchmark
	public void addRecord() {
		table.add(0.0, 1.0, 2.0, 3.0, 4.0, 5.0);
	}
}
