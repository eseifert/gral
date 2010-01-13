package openjchart.plots.io;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import openjchart.Drawable;

public class DrawableWriter {
	/** Saves the file as PNG. */
	public static final String FORMAT_PNG = "PNG";
	/** Saves the file as JPG. */
	public static final String FORMAT_JPG = "JPG";
	/** Saves the file as GIF. */
	public static final String FORMAT_GIF = "GIF";
	/** Saves the file as BMP. */
	public static final String FORMAT_BMP = "BMP";
	/** Saves the file as WBMP. */
	public static final String FORMAT_WBMP = "WBMP";

	private final File dest;
	private final String format;

	/**
	 * Creates a new DrawableWriter object with the specified output File
	 * format.
	 * @param dest Destination file.
	 * @param format File format.
	 */
	public DrawableWriter(File dest, String format) {
		// FIXME: Specifiy MIME-Type instead of format
		// TODO: Option to set transparency
		// TODO: Possibility to choose a background color
		this.dest = dest;
		this.format = format;
	}

	/**
	 * Stores the specified Drawable.
	 * @param d Drawable to be written.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException
	 */
	public void write(Drawable d, int width, int height) throws IOException {
		Rectangle2D boundsOld = d.getBounds();
		d.setBounds(0, 0, width, height);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		d.draw((Graphics2D) image.getGraphics());
		d.setBounds(boundsOld);
		ImageIO.write(image, format, dest);
	}

}
