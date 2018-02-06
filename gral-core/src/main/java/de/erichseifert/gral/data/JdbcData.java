/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Data source for database tables accessed through a JDBC connection.
 */
public class JdbcData extends AbstractDataSource {
	/** Version id for serialization. */
	private static final long serialVersionUID = 5196527358266585129L;

	/** The JDBC connection. */
	private final Connection connection;
	/** The name of the table containing the data. */
	private final String table;

	/** Flag that tells whether this object uses buffering. */
	private boolean buffered;
	/** Buffered number of rows. Only valid when the object is buffered. */
	private int bufferedRowCount;
	/** Buffered result of the JDBC data query. Only valid when the object is
	buffered. */
	private ResultSet bufferedQuery;
	/** Last row accessed by the {@link #get(int, int)} method.
	Only valid when the object is buffered. */
	private int bufferedQueryRow;

	/**
	 * Initializes a new instance to query the data from a specified table
	 * using a specified JDBC connection. It is assumed the table columns
	 * are constant during the connection.
	 * @param connection JDBC connection object.
	 * @param table Properly quoted name of the table.
	 * @param buffered Turns on buffering of JDBC queries.
	 */
	@SuppressWarnings("unchecked")
	public JdbcData(Connection connection, String table, boolean buffered) {
		this.connection = connection;
		this.table = table;
		setBuffered(buffered);

		try {
			setColumnTypes(getJdbcColumnTypes());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes a new buffered instance to query the data from a specified
	 * table using a specified JDBC connection.
	 * @param connection JDBC connection object.
	 * @param table Properly quoted name of the table.
	 */
	public JdbcData(Connection connection, String table) {
		this(connection, table, true);
	}

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	public Comparable<?> get(int col, int row) {
		try {
			ResultSet result = bufferedQuery;
			if (!isBuffered() || result == null) {
				PreparedStatement stmt = connection.prepareStatement(
						"SELECT * FROM " + table + "", //$NON-NLS-1$ //$NON-NLS-2$
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				result = stmt.executeQuery();

				if (isBuffered()) {
					bufferedQuery = result;
				}
			}
			if (!isBuffered() || row != bufferedQueryRow) {
				result.absolute(row + 1);
				bufferedQueryRow = row;
			}
			return jdbcToJavaValue(result, col);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getColumnCount() {
		if (getColumnTypes() != null) {
			return getColumnTypes().length;
		}
		return 0;
	}

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	public int getRowCount() {
		int rowCount = bufferedRowCount;
		if (!isBuffered() || rowCount < 0) {
			try {
				PreparedStatement stmt = connection.prepareStatement(
					"SELECT COUNT(*) FROM " + table, //$NON-NLS-1$
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
				ResultSet result = stmt.executeQuery();
				if (result.first()) {
					rowCount = result.getInt(1);
					bufferedRowCount = rowCount;
				} else {
					rowCount = 0;
				}
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
				rowCount = 0;
			}
		}
		return rowCount;
	}

	/**
	 * Fetches the column types as Java {@code Class} objects from the
	 * JDBC table.
	 * @return Column types as Java {@code Class} objects
	 * @throws SQLException if an error occurs during access to JDBC table.
	 */
	@SuppressWarnings("unchecked")
	private Class<? extends Comparable<?>>[] getJdbcColumnTypes()
			throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(
			"SELECT * FROM " + table + " WHERE 1 = 0"); //$NON-NLS-1$ //$NON-NLS-2$
		ResultSetMetaData metadata = stmt.getMetaData();
		int colCount = metadata.getColumnCount();
		Class<?>[] types = new Class<?>[colCount];
		for (int colIndex = 0; colIndex < colCount; colIndex++) {
			int sqlType = metadata.getColumnType(colIndex + 1);
			Class<? extends Comparable<?>> type = null;
			switch (sqlType) {
			case Types.TINYINT:
				type = Byte.class;
				break;
			case Types.SMALLINT:
				type = Short.class;
				break;
			case Types.INTEGER:
				type = Integer.class;
				break;
			case Types.BIGINT:
				type = Long.class;
				break;
			case Types.REAL:
				type = Float.class;
				break;
			case Types.FLOAT:
			case Types.DOUBLE:
				type = Double.class;
				break;
			case Types.DATE:
				type = Date.class;
				break;
			case Types.TIME:
				type = Time.class;
				break;
			case Types.TIMESTAMP:
				type = Timestamp.class;
				break;
			case Types.CHAR:
			case Types.NCHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
				type = String.class;
			default:
				break;
			}
			types[colIndex] = type;
		}
		return (Class<? extends Comparable<?>>[]) types;
	}

	/**
	 * Converts a value of a JDBC {@code ResultSet} to a Java compatible
	 * data value. If the data type is unknown {@code null} will be
	 * returned.
	 * @param row ResultSet object.
	 * @param col Column.
	 * @return Converted value.
	 * @throws SQLException if an error occurs during conversion or accessing
	 *         the result set.
	 */
	private Comparable<?> jdbcToJavaValue(ResultSet row, int col) throws SQLException {
		// TODO Add getColumn(int).getType() method to Column
		// AbstractDataSource.getColumnTypes() makes a defensive copy.
		Class<? extends Comparable<?>> colType = getColumnTypes()[col];
		int sqlCol = col + 1;
		if (Byte.class.equals(colType)) {
			return row.getByte(sqlCol);
		} else if (Short.class.equals(colType)) {
			return row.getShort(sqlCol);
		} else if (Integer.class.equals(colType)) {
			return row.getInt(sqlCol);
		} else if (Long.class.equals(colType)) {
			return row.getLong(sqlCol);
		} else if (Float.class.equals(colType)) {
			return row.getFloat(sqlCol);
		} else if (Double.class.equals(colType)) {
			return row.getDouble(sqlCol);
		} else if (Date.class.equals(colType)) {
			return row.getDate(sqlCol);
		} else if (Time.class.equals(colType)) {
			return row.getTime(sqlCol);
		} else if (Timestamp.class.equals(colType)) {
			return row.getTimestamp(sqlCol);
		} else if (String.class.equals(colType)) {
			return row.getString(sqlCol);
		} else {
			return null;
		}
	}

	/**
	 * Returns whether this data source is buffered.
	 * @return {@code true} when this object uses buffering,
	 *         {@code false} otherwise
	 */
	public boolean isBuffered() {
		return buffered;
	}

	/**
	 * Determines whether this data source should buffer intermediate results.
	 * This implies that the data doesn't change during access.
	 * @param buffered {@code true} when this object should use buffering,
	 *                 {@code false} otherwise
	 */
	public void setBuffered(boolean buffered) {
		this.buffered = buffered;
		this.bufferedRowCount = -1;
		this.bufferedQuery = null;
		this.bufferedQueryRow = -1;
	}

	/**
	 * Custom serialization method.
	 * @param out Output stream.
	 * @throws ClassNotFoundException if a deserialized class does not exist.
	 * @throws IOException if there is an error while writing data to the
	 *         output stream.
	 */
	private void writeObject(ObjectOutputStream out)
			throws ClassNotFoundException, IOException {
		throw new UnsupportedOperationException("JDBC data sources cannot be serialized.");
	}
}
