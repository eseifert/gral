package openjchart.data;

import java.util.ArrayList;
import java.util.List;


public abstract class DataSubset extends AbstractDataSource implements DataListener {
	private final DataSource original;
	private final List<Integer> accepted;

	public DataSubset(DataSource original) {
		accepted = new ArrayList<Integer>();
		this.original = original;
		this.original.addDataListener(this);
		dataChanged(this.original);
	}

	@Override
	public Number[] get(int row) {
		int rowOrig = accepted.get(row);
		return original.get(rowOrig);
	}

	@Override
	public Number get(int col, int row) {
		int rowOrig = accepted.get(row);
		return original.get(col, rowOrig);
	}

	@Override
	public int getColumnCount() {
		return original.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return accepted.size();
	}

	@Override
	public void dataChanged(DataSource data) {
		accepted.clear();
		int rowIndex = 0;
		for (Number[] row : original) {
			if (accept(row)) {
				accepted.add(rowIndex);
			}
			rowIndex++;
		}
		notifyDataChanged();
	}

	public abstract boolean accept(Number[] row);
}
