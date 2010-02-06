package openjchart.io.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import openjchart.io.AbstractWriterFactory;

public class DataWriterFactory extends AbstractWriterFactory<DataWriter> {
	private static DataWriterFactory instance;

	private DataWriterFactory() {
		super("datawriters.properties");
	}

	public static DataWriterFactory getInstance() {
		if (instance == null) {
			instance = new DataWriterFactory();
		}
		return instance;
	}

	@Override
	public DataWriter getWriter(String mimeType) {
		DataWriter writer = null;
		Class<? extends DataWriter> clazz = writers.get(mimeType);
		//WriterCapabilities capabilities = getCapabilities(mimeType);
		try {
			Constructor<? extends DataWriter> constructor = clazz.getDeclaredConstructor(String.class);
			writer = constructor.newInstance(mimeType);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (writer == null) {
			throw new IllegalArgumentException("Unsupported MIME-Type: "+mimeType);
		}

		return writer;
	}
}
