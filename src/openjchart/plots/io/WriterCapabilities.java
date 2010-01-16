package openjchart.plots.io;

/**
 * Class that stores information on a DrawableWriter.
 */
public class WriterCapabilities {
	private final String format;
	private final String name;
	private final String mimeType;
	private final String[] extensions;

	/**
	 * Creates a new WriterCapabilities object with the specified format,
	 * name, MIME-Type and filename extensions.
	 * @param format Format.
	 * @param name Name.
	 * @param mimeType MIME-Type
	 * @param extensions Extensions.
	 */
	protected WriterCapabilities(String format, String name, String mimeType, String... extensions) {
		this.format = format;
		this.name = name;
		this.mimeType = mimeType;
		// TODO: Check that there is at least one filename extension
		this.extensions = extensions;
	}

	/**
	 * Returns the format.
	 * @return Format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Returns the name of the format.
	 * @return Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the MIME-Type of the format.
	 * @return Format.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns an array with Strings containing all possible filename
	 * extensions.
	 * @return Filename Extensions.
	 */
	public String[] getExtensions() {
		return extensions;
	}
}
