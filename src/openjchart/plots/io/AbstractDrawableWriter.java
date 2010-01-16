package openjchart.plots.io;

import java.io.OutputStream;

/**
 * Abstract class that implements the basic functions of a DrawableWriter.
 * Functionality includes the implementation of getters.
 */
public abstract class AbstractDrawableWriter implements DrawableWriter {
	protected static WriterCapabilities[] CAPABILITIES;

	private final OutputStream destination;
	private final String format;

	/**
	 * Creates a new AbstractDrawableWriter object with the specified
	 * destination and format.
	 * @param destination Output destination.
	 * @param format Output format.
	 */
	protected AbstractDrawableWriter(OutputStream destination, String format) {
		this.destination = destination;
		this.format = format;
	}

	@Override
	public OutputStream getDestination() {
		return destination;
	}

	@Override
	public String getFormat() {
		return format;
	}
}
