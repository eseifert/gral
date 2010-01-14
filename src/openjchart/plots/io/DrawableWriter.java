package openjchart.plots.io;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import openjchart.Drawable;
import openjchart.util.SVGGraphics2D;

/**
 * Class for rendering <code>Drawable</code> instances and writing them
 * to an output stream. As an example: the class can save a plot to a
 * bitmap file.
 */
public class DrawableWriter {
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
	/** Use the SVG vector format for saving. */
	public static final String FORMAT_SVG = "SVG";

	private final OutputStream dest;
	private final String format;

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
	public void write(Drawable d, double width, double height) throws IOException {
		write(d, 0.0, 0.0, width, height);
	}
	
	public void write(Drawable d, double x, double y, double width, double height) throws IOException {
		Rectangle2D boundsOld = d.getBounds();
		d.setBounds(x, y, width, height);

		if (isVectorFormat(getFormat())) {
			if (FORMAT_SVG.equals(getFormat())) {
				SVGGraphics2D svg = new SVGGraphics2D(x, y, width, height);
				d.draw(svg);
				dest.write(svg.toString().getBytes());
			}
		} else {
			int rasterFormat = BufferedImage.TYPE_INT_ARGB;
			if (FORMAT_GIF.equals(format)) {
				rasterFormat = BufferedImage.TYPE_INT_RGB;
			}
			BufferedImage image = new BufferedImage(
					(int)Math.round(width), (int)Math.round(height), rasterFormat);
			d.draw((Graphics2D) image.getGraphics());
			ImageIO.write(image, format, dest);
		}

		d.setBounds(boundsOld);
	}

	public static boolean isVectorFormat(String format) {
		if (FORMAT_SVG.equals(format)) {
			return true;
		}
		return false;
	}

}
