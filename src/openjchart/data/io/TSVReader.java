package openjchart.data.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import openjchart.data.DataSource;
import openjchart.data.DataTable;

public class TSVReader implements DataReader {
	private final Class<? extends Number>[] types;
	private final Map<Class<? extends Number>, Method> parseMethods;
	private final Reader input;

	public TSVReader(Reader input, Class<? extends Number>... types) {
		parseMethods = new HashMap<Class<? extends Number>, Method>();
		this.input = input;
		this.types = types;
		for (Class<? extends Number> type : types) {
			if (parseMethods.containsKey(type)) {
				continue;
			}
			Method parseMethod = getParseMethod(type);
			if (parseMethod != null) {
				parseMethods.put(type, parseMethod);
			}
		}
	}

	public DataSource read() throws IOException, ParseException {
		DataTable data = new DataTable(types);
		BufferedReader reader = new BufferedReader(input);
		String line = null;
		for (int lineNo = 0; (line = reader.readLine()) != null; lineNo++) {
			String[] cols = line.split("\t");
			if (cols.length < types.length) {
				throw new IllegalArgumentException("Column count in file doesn't match; got "+cols.length+", but expected "+types.length+".");
			}
			Number[] row = new Number[types.length];
			for (int i = 0; i < types.length; i++) {
				Method parseMethod = parseMethods.get(types[i]);
				try {
					row[i] = (Number)parseMethod.invoke(null, cols[i]);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
					throw new ParseException("Type mismatch in column "+i+": got \""+cols[i]+"\", but expected "+types[i].getSimpleName()+" value.", -1);
				}
			}
			data.add(row);
		}
		return data;
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
