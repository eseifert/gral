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
package de.erichseifert.gral.io.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.util.Messages;


/**
 * <p>Class that creates a <code>DataSource</code> from file contents which are
 * separated by a certain delimiter character. The delimiter is chosen based on
 * the file type but can also be set manually. By default the comma character
 * will be used as a delimiter for separating columns.</p>
 * <p><code>CSVReader</code>s instances should be obtained by the
 * {@link DataReaderFactory} rather than being created manually:</p>
 * <pre>
 * DataReaderFactory factory = DataReaderFactory.getInstance();
 * DataReader reader = factory.get("text/csv");
 * reader.read(new FileInputStream(filename), Integer.class, Double.class);
 * </pre>
 * @see <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>
 */
public class CSVReader extends AbstractDataReader {
	static {
		addCapabilities(new IOCapabilities(
			"CSV", //$NON-NLS-1$
			Messages.getString("DataIO.csvDescription"), //$NON-NLS-1$
			"text/csv", //$NON-NLS-1$
			new String[] {"csv", "txt"} //$NON-NLS-1$ //$NON-NLS-2$
		));

		addCapabilities(new IOCapabilities(
			"TSV", //$NON-NLS-1$
			Messages.getString("DataIO.tsvDescription"), //$NON-NLS-1$
			"text/tab-separated-values", //$NON-NLS-1$
			new String[] {
				"tsv", "tab", "txt"} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		));
	}

	/**
	 * Creates a new instance with the specified MIME type. The delimiter is
	 * set depending on the MIME type parameter. By default a comma is used as
	 * a delimiter.
	 * @param mimeType MIME type of the file format to be read.
	 */
	public CSVReader(String mimeType) {
		super(mimeType);
		if ("text/tab-separated-values".equals(mimeType)) {
			setDefault("separator", "\t"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			setDefault("separator", ","); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public DataSource read(InputStream input, Class<? extends Number>... types)
			throws IOException {
		Map<Class<? extends Number>, Method> parseMethods =
			new HashMap<Class<? extends Number>, Method>();
		for (Class<? extends Number> type : types) {
			if (parseMethods.containsKey(type)) {
				continue;
			}
			Method parseMethod = getParseMethod(type);
			if (parseMethod != null) {
				parseMethods.put(type, parseMethod);
			}
		}

		String separatorPattern = getSetting("separator"); //$NON-NLS-1$
		DataTable data = new DataTable(types);
		BufferedReader reader =
			new BufferedReader(new InputStreamReader(input));
		String line = null;
		for (int lineNo = 0; (line = reader.readLine()) != null; lineNo++) {
			String[] cols = line.split(separatorPattern);
			if (cols.length < types.length) {
				throw new IllegalArgumentException(MessageFormat.format(
					"Column count in file does not match: got {0,number,integer}, but expected {1,number,integer}.", //$NON-NLS-1$
					cols.length, types.length));
			}
			Number[] row = new Number[types.length];
			for (int i = 0; i < types.length; i++) {
				Method parseMethod = parseMethods.get(types[i]);
				try {
					row[i] = (Number) parseMethod.invoke(null, cols[i]);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(MessageFormat.format(
						"Could not invoke method for parsing data type {0} in column {1,number,integer}.", //$NON-NLS-1$
						types[i].getSimpleName(), i));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(MessageFormat.format(
						"Could not access method for parsing data type {0} in column {1,number,integer}.", //$NON-NLS-1$
						types[i].getSimpleName(), i));
				} catch (InvocationTargetException e) {
					throw new IOException(MessageFormat.format(
						"Type mismatch in line {0,number,integer}, column {1,number,integer}: got \"{2}\", but expected {3} value.", //$NON-NLS-1$
						i, lineNo, cols[i], types[i].getSimpleName()));
				}
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * Returns a Method that returns a parsed value in the specified type.
	 * @param c Desired type.
	 * @return Method that parses a data type.
	 */
	private static Method getParseMethod(Class<?> c) {
		Method parse = null;
		for (Method m : c.getMethods()) {
			boolean isStatic = m.toString().indexOf("static") >= 0; //$NON-NLS-1$
			if (!isStatic) {
				continue;
			}
			Class<?>[] types = m.getParameterTypes();
			boolean hasStringParameter =
				(types.length == 1) && String.class.equals(types[0]);
			if (!hasStringParameter) {
				continue;
			}
			boolean parseName = m.getName().startsWith("parse"); //$NON-NLS-1$
			if (!parseName) {
				continue;
			}
			parse = m;
		}
		return parse;
	}

}
