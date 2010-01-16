package openjchart.plots.io;

import java.io.OutputStream;

public abstract class AbstractDrawableWriter implements DrawableWriter {
	protected static WriterCapabilities[] CAPABILITIES;

	private final OutputStream destination;
	private final String format;

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
