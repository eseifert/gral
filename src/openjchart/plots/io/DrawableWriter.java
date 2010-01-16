package openjchart.plots.io;

import java.io.IOException;
import java.io.OutputStream;

import openjchart.Drawable;

/**
 * Interface providing functions for rendering <code>Drawable</code>
 * instances and writing them to an output stream. As an example: a plot
 * can be saved into a bitmap file.
 */
public interface DrawableWriter {
	/** Use the BMP bitmap format for saving. */
	public static final String FORMAT_BMP = "BMP";
	/** Use the GIF bitmap format for saving. */
	public static final String FORMAT_GIF = "GIF";
	/** Use the PNG bitmap format for saving. */
	public static final String FORMAT_PNG = "PNG";
	/** Use the JFIF/JPEG bitmap format for saving. */
	public static final String FORMAT_JPG = "JPG";
	/** Use the WBMP bitmap format for saving. */
	public static final String FORMAT_WBMP = "WBMP";
	/** Use the EPS vector format for saving. */
	public static final String FORMAT_EPS = "EPS";
	/** Use the SVG vector format for saving. */
	public static final String FORMAT_SVG = "SVG";

	/**
	 * Returns the output stream this class is writing to.
	 * @return OutputStream instance that this class is writing to
	 */
	public OutputStream getDestination();

	/**
	 * Returns the output format of this writer.
	 * @return String representing the MIME-Type.
	 */
	public String getFormat();

	/**
	 * Stores the specified <code>Drawable</code> instance.
	 * @param d <code>Drawable</code> to be written.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	public void write(Drawable d, double width, double height) throws IOException;

	/**
	 * Stores the specified <code>Drawable</code> instance.
	 * @param d <code>Drawable</code> to be written.
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	public void write(Drawable d, double x, double y, double width, double height) throws IOException;
}
