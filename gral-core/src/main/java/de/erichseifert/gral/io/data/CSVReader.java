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
package de.erichseifert.gral.io.data;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.util.Messages;
import de.erichseifert.gral.util.StatefulTokenizer;
import de.erichseifert.gral.util.StatefulTokenizer.Token;


/**
 * <p>Class that creates a {@code DataSource} from file contents which are
 * separated by a certain delimiter character. The delimiter is chosen based on
 * the file type but can also be set manually. By default the comma character
 * will be used as a delimiter for separating columns.</p>
 * <p>{@code CSVReader} instances should be obtained by the
 * {@link DataReaderFactory} rather than being created manually:</p>
 * <pre>
 * DataReaderFactory factory = DataReaderFactory.getInstance();
 * DataReader reader = factory.get("text/csv");
 * reader.read(new FileInputStream(filename), Integer.class, Double.class);
 * </pre>
 * @see <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>
 */
public class CSVReader extends AbstractDataReader {
	/** Key for specifying a {@link Character} value that defines the
	delimiting character used to separate columns. */
	public static final String SEPARATOR_CHAR = "separator"; //$NON-NLS-1$

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
	 * Token types for analyzing CSV or TSV input.
	 */
	private enum CSVTokenType {
		/** Type for text tokens containing empty content. */
		EMPTY_SPACE,
		/** Type for text tokens containing value content. */
		TEXT,
		/** Type for quotes that may wrap value content. */
		QUOTE,
		/** Type for row separators. */
		ROW_SEPARATOR,
		/** Type for column separators. */
		COLUMN_SEPARATOR,
	}

	/**
	 *
	 */
	private static final class CSVTokenizer extends StatefulTokenizer {
		/**
		 * Initializes a new tokenizer instance with a grammar to analyze CSV
		 * or TSV content. The character that separates columns must be
		 * provided.
		 * @param separator Column separator character.
		 */
		public CSVTokenizer(char separator) {
			addJoinedType(CSVTokenType.TEXT);
			addIgnoredType(CSVTokenType.QUOTE);

			// Basic Set of rules for analyzing CSV content
			putRules(
				new Rule("\n|\r\n|\r", CSVTokenType.ROW_SEPARATOR),
				new Rule(Pattern.quote(String.valueOf(separator)),
						CSVTokenType.COLUMN_SEPARATOR),
				new Rule("\"", CSVTokenType.QUOTE, "quoted"),
				new Rule("[ \t]+", CSVTokenType.EMPTY_SPACE),
				new Rule(".", CSVTokenType.TEXT)
			);
			// Set of rules that is valid inside quoted content
			putRules("quoted",
				new Rule("(\")\"", CSVTokenType.TEXT),
				new Rule("\"", CSVTokenType.QUOTE, "#pop"),
				new Rule(".", CSVTokenType.TEXT)
			);
		}
	}

	/**
	 * Creates a new instance with the specified MIME type. The delimiter is
	 * set depending on the MIME type parameter. By default a comma is used as
	 * a delimiter.
	 * @param mimeType MIME type of the file format to be read.
	 */
	public CSVReader(String mimeType) {
		super(mimeType);
		if ("text/tab-separated-values".equals(mimeType)) { //$NON-NLS-1$
			setDefault(SEPARATOR_CHAR, '\t');
		} else {
			setDefault(SEPARATOR_CHAR, ',');
		}
	}

	/**
	 * Returns a DataSource that was imported.
	 * @param input Input to be read.
	 * @param types Number types for the columns of the DataSource.
	 * @return DataSource Imported data.
	 * @throws IOException when the file format is not valid or when
	 *         experiencing an error during file operations.
	 */
	public DataSource read(InputStream input, Class<? extends Comparable<?>>... types)
			throws IOException {
		// Read all contents from the input stream
		Scanner scanner = new Scanner(input).useDelimiter("\\Z");
		String content = scanner.next();

		// Tokenize the string
		Character separator = getSetting(SEPARATOR_CHAR);
		CSVTokenizer tokenizer = new CSVTokenizer(separator);
		List<Token> tokens = tokenizer.tokenize(content);

		// Add row token if there was no trailing line break
		Token lastToken = tokens.get(tokens.size() - 1);
		if (lastToken.getType() != CSVTokenType.ROW_SEPARATOR) {
			Token eof = new Token(lastToken.getEnd(), lastToken.getEnd(),
				CSVTokenType.ROW_SEPARATOR, "");
			tokens.add(eof);
		}

		// Find methods for all column data types that can be used to convert
		// the text to the column data type
		Map<Class<? extends Comparable<?>>, Method> parseMethods =
				new HashMap<>();
		for (Class<? extends Comparable<?>> type : types) {
			if (parseMethods.containsKey(type)) {
				continue;
			}
			Method parseMethod = getParseMethod(type);
			if (parseMethod != null) {
				parseMethods.put(type, parseMethod);
			}
		}

		// Process the data and store the data.
		DataTable data = new DataTable(types);
		List<Comparable<?>> row = new LinkedList<>();
		int rowIndex = 0;
		int colIndex = 0;
		StringBuilder cellContent = new StringBuilder();
		for (Token token : tokens) {
			if (token.getType() == CSVTokenType.TEXT ||
					token.getType() == CSVTokenType.EMPTY_SPACE) {
				// Store the token text
				cellContent.append(token.getContent());
			} else if (token.getType() == CSVTokenType.COLUMN_SEPARATOR ||
					token.getType() == CSVTokenType.ROW_SEPARATOR) {
				// Check for a valid number of columns
				if (colIndex >= types.length) {
					throw new IllegalArgumentException(MessageFormat.format(
						"Too many columns in line {0,number,integer}: got {1,number,integer}, but expected {2,number,integer}.", //$NON-NLS-1$
						rowIndex + 1, colIndex + 1, types.length));
				}

				// We need to add the cell to the row in both cases because
				// rows don't have a trailing column token
				Class<? extends Comparable<?>> colType = types[colIndex];
				Method parseMethod = parseMethods.get(colType);
				Comparable<?> cell = null;
				try {
					cell = (Comparable<?>) parseMethod.invoke(
						null, cellContent.toString().trim());

				} catch (IllegalArgumentException e) {
					throw new RuntimeException(MessageFormat.format(
						"Could not invoke method for parsing data type {0} in column {1,number,integer}.", //$NON-NLS-1$
						types[colIndex].getSimpleName(), colIndex));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(MessageFormat.format(
						"Could not access method for parsing data type {0} in column {1,number,integer}.", //$NON-NLS-1$
						types[colIndex].getSimpleName(), colIndex));
				} catch (InvocationTargetException e) {
					if (cellContent.length() > 0) {
						throw new IOException(MessageFormat.format(
							"Type mismatch in line {0,number,integer}, column {1,number,integer}: got \"{2}\", but expected {3} value.", //$NON-NLS-1$
							rowIndex + 1, colIndex + 1, cellContent.toString(), colType.getSimpleName()));
					}
				}
				row.add(cell);
				colIndex++;

				if (token.getType() == CSVTokenType.ROW_SEPARATOR) {
					// Check for a valid number of columns
					if (row.size() < types.length) {
						throw new IllegalArgumentException(MessageFormat.format(
							"Not enough columns in line {0,number,integer}: got {1,number,integer}, but expected {2,number,integer}.", //$NON-NLS-1$
							rowIndex + 1, row.size(), types.length));
					}

					// Add the row to the table
					data.add(row);
					rowIndex++;

					// Start a new row
					row.clear();
					colIndex = 0;
				}
				cellContent = new StringBuilder();
			}
		}

		return data;
	}

	/**
	 * Returns a method that can return a parsed value of the specified type.
	 * @param c Desired type.
	 * @return Method that parses a data type.
	 */
	private static Method getParseMethod(Class<?> c) {
		Method parse = null;

		if (String.class.isAssignableFrom(c)) {
			try {
				parse = String.class.getMethod("valueOf", Object.class);
			} catch (NoSuchMethodException e) {
			}
		} else {
			for (Method m : c.getMethods()) {
				boolean isStatic = m.toString().contains("static"); //$NON-NLS-1$
				if (!isStatic) {
					continue;
				}
				Class<?>[] types = m.getParameterTypes();
				boolean hasStringParameter =
					(types.length == 1) && String.class.equals(types[0]);
				if (!hasStringParameter) {
					continue;
				}
				// Check method name for a pattern like "parseInt*" for Integer or
				// "parseSho*" for Short to avoid collisions
				if (!m.getName().startsWith("parse" + c.getSimpleName().substring(0, 3))) {  //$NON-NLS-1$
					continue;
				}
				parse = m;
			}
		}

		return parse;
	}
}
