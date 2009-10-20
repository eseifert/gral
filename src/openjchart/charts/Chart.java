package openjchart.charts;

import javax.swing.JComponent;

import openjchart.data.DataMapper;
import openjchart.data.DataTable;

/**
 * Basic interface for all classes that want to present the data of a DataTable.
 * Classes implementing this interface act as a renderer. After some optional
 * configuration, a Chart is capable of displaying any number of DataTables.
 * @author Michael Seifert
 * @see openjchart.data.DataTable
 *
 */
public interface Chart {
	/**
	 * Returns a component that is used to display the data with the specified mapping.
	 * @param data data to be displayed
	 * @param mapper mapping of columns in the data to certain properties
	 * @return display component
	 */
	JComponent getChartRenderer(DataTable data, DataMapper mapper);
}
