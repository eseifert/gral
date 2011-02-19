/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;

import de.erichseifert.gral.data.AbstractDataSource;

/**
 * Data source for database tables accessed through a JDBC connection.
 */
public class JdbcData extends AbstractDataSource {
	/** The data types of all columns. */
	private Class<? extends Number>[] types;
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
	 * @param conn JDBC connection object.
	 * @param table Properly quoted name of the table.
	 * @param buffering Turns on buffering of JDBC queries.
	 */
	public JdbcData(Connection connection, String table, boolean buffered) {
		this.connection = connection;
		this.table = table;
		setBuffered(buffered);

		types = null;
		try {
			this.types = getJdbcColumnTypes();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes a new buffered instance to query the data from a specified
	 * table using a specified JDBC connection.
	 * @param conn JDBC connection object.
	 * @param table Properly quoted name of the table.
	 */
	public JdbcData(Connection connection, String table) {
		this(connection, table, true);
	}

	@Override
	public Number get(int col, int row) {
			try {
				ResultSet result = bufferedQuery;
				if (!isBuffered() || result == null) {
					PreparedStatement stmt = connection.prepareStatement(
							"SELECT * FROM " + table + "",
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
		if (types != null)
			return types.length;
		return 0;
	}

	@Override
	public Class<? extends Number>[] getColumnTypes() {
		return Arrays.copyOf(types, types.length);
	}

	@Override
	public int getRowCount() {
		int rowCount = bufferedRowCount;
		if (!isBuffered() || rowCount < 0) {
			try {
				PreparedStatement stmt = connection.prepareStatement(
						"SELECT COUNT(*) FROM " + table,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet result = stmt.executeQuery();
				result.first();
				rowCount = result.getInt(1);
				bufferedRowCount = rowCount;
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
				rowCount = 0;
			}
		}
		return rowCount;
	}

	/**
	 * Fetches the column types as Java <code>Class</code> objects from the
	 * JDBC table.
	 * @return Column types as Java <code>Class</code> objects
	 * @throws SQLException if an error occurs during access to JDBC table.
	 */
	private Class<? extends Number>[] getJdbcColumnTypes()
			throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(
				"SELECT * FROM " + table + " WHERE 1 = 0");
		ResultSetMetaData metadata = stmt.getMetaData();
		int colCount = metadata.getColumnCount();
		Class<?>[] types = new Class<?>[colCount];
		for (int colIndex = 0; colIndex < colCount; colIndex++) {
			int sqlType = metadata.getColumnType(colIndex + 1);
			switch (sqlType) {
			case Types.TINYINT:
				types[colIndex] = Byte.class;
				break;
			case Types.SMALLINT:
				types[colIndex] = Short.class;
				break;
			case Types.INTEGER:
				types[colIndex] = Integer.class;
				break;
			case Types.BIGINT:
				types[colIndex] = Long.class;
				break;
			case Types.REAL:
				types[colIndex] = Float.class;
				break;
			case Types.FLOAT:
			case Types.DOUBLE:
				types[colIndex] = Double.class;
				break;
			case Types.DATE:
				types[colIndex] = Date.class;
				break;
			case Types.TIME:
				types[colIndex] = Time.class;
				break;
			case Types.TIMESTAMP:
				types[colIndex] = Timestamp.class;
				break;
			default:
				break;
			}
		}
		return (Class<? extends Number>[]) types;
	}

	/**
	 * Converts a value of a JDBC <code>ResultSet</code> to a Java compatible
	 * data value. If the data type is unknown <code>null</code> will be
	 * returned.
	 * @param row ResultSet object.
	 * @param col Column.
	 * @return Converted value.
	 * @throws SQLException if an error occurs during conversion or accessing
	 *         the result set.
	 */
	private Number jdbcToJavaValue(ResultSet row, int col) throws SQLException {
		Class<? extends Number> colType = types[col];
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
			return row.getDate(sqlCol).getTime();
		} else if (Time.class.equals(colType)) {
			return row.getTime(sqlCol).getTime();
		} else if (Timestamp.class.equals(colType)) {
			return row.getTimestamp(sqlCol).getTime();
		} else {
			return null;
		}
	}

	/**
	 * Returns whether this data source is buffered.
	 * @return <code>true</code> when this object uses buffering,
	 *         <code>false</code> otherwise
	 */
	public boolean isBuffered() {
		return buffered;
	}

	/**
	 * Determines whether this data source should buffer intermediate results.
	 * This implies that the data doesn't change during access.
	 * @param buffered <code>true</code> when this object should use buffering,
	 *                 <code>false</code> otherwise
	 */
	public void setBuffered(boolean buffered) {
		this.buffered = buffered;
		this.bufferedRowCount = -1;
		this.bufferedQuery = null;
		this.bufferedQueryRow = -1;
	}
}
