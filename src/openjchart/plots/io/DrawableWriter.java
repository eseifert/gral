package openjchart.plots.io;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import openjchart.Drawable;

/**
 * Class for rendering <code>Drawable</code> instances and writing them
 * to an output stream. As an example: the class can save a plot to a
 * bitmap file.
 */
public class DrawableWriter {
	/** Use the BMP format for saving. */
	public static final String FORMAT_BMP = "BMP";
	/** Use the GIF format for saving. */
	public static final String FORMAT_GIF = "GIF";
	/** Use the PNG format for saving. */
	public static final String FORMAT_PNG = "PNG";
	/** Use the JFIF/JPEG format for saving. */
	public static final String FORMAT_JPG = "JPG";
	/** Use the WBMP format for saving. */
	public static final String FORMAT_WBMP = "WBMP";

	private final OutputStream dest;
	private final String format;
	private final int rasterFormat;

	/**
	 * Creates a new <code>DrawableWriter</code> object with the specified output File
	 * format.
	 * @param dest Destination stream.
	 * @param format File format.
	 */
	public DrawableWriter(OutputStream dest, String format) {
		// FIXME: Specifiy MIME-Type instead of format
		// TODO: Option to set transparency
		// TODO: Possibility to choose a background color
		this.dest = dest;
		this.format = format;
		rasterFormat = FORMAT_GIF.equals(format) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
	}

	/**
	 * Returns the output stream this class is writing to.
	 * @return OutputStream instance that this class is writing to
	 */
	public OutputStream getDestination() {
		return dest;
	}

	/**
	 * Returns the output format of this writer.
	 * @return String describing the output format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Stores the specified <code>Drawable</code> instance.
	 * @param d <code>Drawable</code> to be written.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	public void write(Drawable d, int width, int height) throws IOException {
		Rectangle2D boundsOld = d.getBounds();
		d.setBounds(0, 0, width, height);
		BufferedImage image = new BufferedImage(width, height, rasterFormat);
		d.draw((Graphics2D) image.getGraphics());
		d.setBounds(boundsOld);
		ImageIO.write(image, format, dest);
	}

}
