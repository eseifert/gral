package openjchart.plots.io;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class that provides DrawableWriters for different formats.
 * @see DrawableWriter
 */
public class DrawableWriterFactory {
	private static DrawableWriterFactory instance;
	private final Map<WriterCapabilities, Class<? extends DrawableWriter>> writers = new HashMap<WriterCapabilities, Class<? extends DrawableWriter>>();

	private DrawableWriterFactory() {
		// FIXME: try to get a proper solution via java reflection
		new BitmapWriter(null, null);
		for (WriterCapabilities c : BitmapWriter.CAPABILITIES) {
			writers.put(c, BitmapWriter.class);
		}
		new VectorWriter(null, null);
		for (WriterCapabilities c : VectorWriter.CAPABILITIES) {
			writers.put(c, VectorWriter.class);
		}
	}

	/**
	 * Returns an array of Strings containing all supported output formats.
	 * @return Supported formats.
	 */
	public String[] getSupportedFormats() {
		String[] formats = new String[writers.size()];
		Iterator<WriterCapabilities> iter = writers.keySet().iterator();
		for (int i = 0; iter.hasNext(); i++) {
			WriterCapabilities c = iter.next();
			formats[i] = c.getFormat();
		}

		return formats;
	}

	/**
	 * Returns true if the specified format is supported.
	 * @param format Output format.
	 * @return True if supported.
	 */
	public boolean isFormatSupported(String format) {
		for (WriterCapabilities c : writers.keySet()) {
			if (c.getFormat().equals(format)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns an instance of this DrawableWriterFactory.
	 * @return Instance.
	 */
	public static DrawableWriterFactory getInstance() {
		if (instance == null) {
			instance = new DrawableWriterFactory();
		}
		return instance;
	}

	/**
	 * Returns a DrawableWriter for the specified format.
	 * @param format Output format.
	 * @return DrawableWriter.
	 */
	public DrawableWriter getDrawableWriter(OutputStream destination, String format) {
		// FIXME: use MIME-Type instead of format
		DrawableWriter writer = null;
		for (Map.Entry<WriterCapabilities, Class<? extends DrawableWriter>> entry : writers.entrySet()) {
			if (entry.getKey().getFormat().equals(format)) {
				Class<? extends DrawableWriter> clazz = entry.getValue();
				try {
					Constructor<? extends DrawableWriter> constructor = clazz.getDeclaredConstructor(OutputStream.class, String.class);
					writer = constructor.newInstance(destination, format);
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
			}
		}

		if (writer == null) {
			throw new IllegalArgumentException("Unsupported format: "+format);
		}

		return writer;
	}
}
