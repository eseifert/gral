package openjchart.data.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import openjchart.data.DataSource;

public class TSVWriter implements DataWriter {
	private final Writer output;

	public TSVWriter(Writer output) {
		this.output = output;
	}

	public void write(DataSource data) throws IOException {
		PrintWriter writer = new PrintWriter(output);
		for (Number[] row : data) {
			for (int col = 0; col < row.length; col++) {
				if (col > 0) {
					writer.print("\t");
				}
				writer.print(row[col]);
			}
			writer.println();
		}
	}

}
