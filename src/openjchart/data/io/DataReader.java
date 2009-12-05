package openjchart.data.io;

import java.io.IOException;
import java.text.ParseException;

import openjchart.data.DataSource;

public interface DataReader {
	DataSource read() throws IOException, ParseException;
}
