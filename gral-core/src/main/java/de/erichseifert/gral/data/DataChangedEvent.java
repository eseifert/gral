package de.erichseifert.gral.data;

import java.util.EventObject;

public class DataChangedEvent extends EventObject {
	/** Version number for serialization. */
	private static final long serialVersionUID = 1L;

	/** Column of the value that has changed. */
	private final int col;
	/** Row of the value that has changed. */
	private final int row;
	/** Value before changes have been applied. */
	private final Number valOld;
	/** Changed value. */
	private final Number valNew;

	/**
	 * Initializes a new event with data source, position of the data value,
	 * and the values.
	 * @param source Data source
	 * @param col Columns of the value
	 * @param row Row of the value
	 * @param valOld Old value
	 * @param valNew New value
	 */
	public DataChangedEvent(DataSource source, int col, int row, Number valOld, Number valNew) {
		super(source);
		this.col = col;
		this.row = row;
		this.valOld = valOld;
		this.valNew = valNew;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	public Number getOld() {
		return valOld;
	}

	public Number getNew() {
		return valNew;
	}
}
