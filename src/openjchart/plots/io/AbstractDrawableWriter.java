package openjchart.plots.io;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class that implements the basic functions of a DrawableWriter.
 * Functionality includes the implementation of getters.
 */
public abstract class AbstractDrawableWriter implements DrawableWriter {
	private static final Set<WriterCapabilities> capabilities = new HashSet<WriterCapabilities>();

	private final OutputStream destination;
	private final String mimeType;

	/**
	 * Creates a new AbstractDrawableWriter object with the specified
	 * destination and format.
	 * @param destination Output destination.
	 * @param mimeType MIME-Type.
	 */
	protected AbstractDrawableWriter(OutputStream destination, String mimeType) {
		this.destination = destination;
		this.mimeType = mimeType;
	}

	@Override
	public OutputStream getDestination() {
		return destination;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns a Set with WriterCapabilities for all supported formats.
	 * @return WriterCapabilities.
	 */
	public static Set<WriterCapabilities> getCapabilities() {
		return Collections.unmodifiableSet(capabilities);
	}

	/**
	 * Adds the specified WriterCapabilities to the Set of supported formats.
	 * @param capabilities WriterCapabilities to be added.
	 */
	protected final static void addCapabilities(WriterCapabilities capabilities) {
		AbstractDrawableWriter.capabilities.add(capabilities);
	}
}
