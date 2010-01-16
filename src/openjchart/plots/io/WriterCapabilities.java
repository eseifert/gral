package openjchart.plots.io;

public class WriterCapabilities {
	private final String format;
	private final String name;
	private final String mimeType;
	private final String[] extensions;

	public WriterCapabilities(String format, String name, String mimeType, String... extensions) {
		this.format = format;
		this.name = name;
		this.mimeType = mimeType;
		// TODO: Check that there is at least one filename extension
		this.extensions = extensions;
	}

	public String getFormat() {
		return format;
	}

	public String getName() {
		return name;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String[] getExtensions() {
		return extensions;
	}
}
