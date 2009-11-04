package openjchart.data;

import java.util.HashMap;

/**
 * Class that represents a view on a DataSource.
 * This is achieved by simply mapping strings to column indexes.
 * @see DataSource
 */
public class DataSeries extends HashMap<String, Integer> {
	public static final String X = "x";
	public static final String Y = "y";
	public static final String Z = "z";
	public static final String SIZE = "size";

	/**
	 * Basic constructor.
	 */
	public DataSeries() {
	}
}
