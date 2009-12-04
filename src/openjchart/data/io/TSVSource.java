package openjchart.data.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;

import openjchart.data.AbstractDataSource;

public class TSVSource extends AbstractDataSource {
	private final ArrayList<Number[]> data;
	private Class<?>[] types;
	private Method[] parseMethods;

	public TSVSource(Reader input, Class<? extends Number>... types) throws IOException, ParseException {
		this.types = new Class[types.length];
		this.parseMethods = new Method[types.length];
		for (int i = 0; i < types.length; i++) {
			this.types[i] = types[i];
			this.parseMethods[i] = getParseMethod(types[i]);
		}
		data = new ArrayList<Number[]>();

		BufferedReader reader = new BufferedReader(input);
		String line = null;
		for (int lineNo = 0; (line = reader.readLine()) != null; lineNo++) {
			String[] cols = line.split("\t");
			if (cols.length < getColumnCount()) {
				throw new IllegalArgumentException("Column count in file doesn't match; got "+cols.length+", but expected "+getColumnCount()+".");
			}
			Number[] row = new Number[getColumnCount()];
			for (int i = 0; i < getColumnCount(); i++) {
				try {
					row[i] = (Number)parseMethods[i].invoke(null, cols[i]);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
					throw new ParseException("Type mismatch in column "+i+": got \""+cols[i]+"\", but expected "+types[i].getSimpleName()+" value.", -1);
				}
			}
			data.add(row);
		}
	}

	@Override
	public Number[] get(int row) {
		return data.get(row);
	}

	@Override
	public Number get(int col, int row) {
		return data.get(row)[col];
	}

	@Override
	public int getColumnCount() {
		return types.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	private static Method getParseMethod(Class<?> c) {
		Method parse = null;
		for (Method m : c.getMethods()) {
			boolean isStatic = m.toString().indexOf("static") >= 0;
			if (!isStatic) {
				continue;
			}
			Class<?>[] types = m.getParameterTypes();
			boolean hasStringParameter = (types.length == 1) && (String.class.equals(types[0]));
			if (!hasStringParameter) {
				continue;
			}
			boolean parseName = m.getName().startsWith("parse");
			if (!parseName) {
				continue;
			}
			parse = m;
		}
		return parse;
	}
}
