package openjchart.data.io;

import java.io.IOException;

import openjchart.data.DataSource;

public interface DataWriter {
	void write(DataSource data) throws IOException;
}
