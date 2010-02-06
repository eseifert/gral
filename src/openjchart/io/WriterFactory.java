package openjchart.io;


public interface WriterFactory<T> {

	/**
	 * Returns a DrawableWriter for the specified format.
	 * @param mimeType Output MIME-Type.
	 * @return Writer for the specified MIME-Type.
	 */
	public abstract T getWriter(String mimeType);

	public abstract WriterCapabilities getCapabilities(String mimeType);

	/**
	 * Returns an array of capabilities for all supported output formats.
	 * @return Supported capabilities.
	 */
	public abstract WriterCapabilities[] getCapabilities();

	/**
	 * Returns an array of Strings containing all supported output formats.
	 * @return Supported formats.
	 */
	public abstract String[] getSupportedFormats();

	/**
	 * Returns true if the specified MIME-Type is supported.
	 * @param mimeType MIME-Type.
	 * @return True if supported.
	 */
	public abstract boolean isFormatSupported(String mimeType);

}