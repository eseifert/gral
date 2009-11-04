package openjchart.data;

/**
 * Interface providing functions to listen to data changes of DataSources.
 * @see DataSource
 */
public interface DataListener {
	/**
	 * Method that is invoked by objects that provide support for DataListeners.
	 * @param data data that has changed
	 */
	void dataChanged(DataSource data);
}
