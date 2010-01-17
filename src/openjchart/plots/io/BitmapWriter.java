package openjchart.plots.io;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import openjchart.Drawable;

/**
 * Class that stores Drawables as bitmap graphics.
 * Supported formats:
 * <ul>
 * <li>BMP</li>
 * <li>GIF</li>
 * <li>JPEG</li>
 * <li>PNG</li>
 * <li>WBMP</li>
 * </ul>
 */
public class BitmapWriter extends AbstractDrawableWriter {
	static {
		WriterCapabilities BMP_CAPABILITIES = new WriterCapabilities(
			"BMP",
			"Windows Bitmap",
			TYPE_BMP,
			"bmp", "dib"
		);
		addCapabilities(BMP_CAPABILITIES);

		WriterCapabilities GIF_CAPABILITIES = new WriterCapabilities(
			"GIF",
			"Graphics Interchange Format",
			TYPE_GIF,
			"gif"
		);
		addCapabilities(GIF_CAPABILITIES);

		WriterCapabilities JPG_CAPABILITIES = new WriterCapabilities(
			"JPG",
			"JPEG",
			TYPE_JPEG,
			"jpg", "jpeg", "jpe", "jif", "jfif", "jfi"
		);
		addCapabilities(JPG_CAPABILITIES);

		WriterCapabilities PNG_CAPABILITIES = new WriterCapabilities(
			"PNG",
			"Portable Network Grahpics",
			TYPE_PNG,
			"png"
		);
		addCapabilities(PNG_CAPABILITIES);

		WriterCapabilities WBMP_CAPABILITIES = new WriterCapabilities(
			"WBMP",
			"Wireless Application Protocol Bitmap Format",
			TYPE_WBMP,
			"wbmp"
		);
		addCapabilities(WBMP_CAPABILITIES);
	}

	/**
	 * Creates a new BitmapWriter object with the specified destination and
	 * format.
	 * @param destination Output destination.
	 * @param format Output format.
	 */
	protected BitmapWriter(OutputStream destination, String format) {
		super(destination, format);
		// TODO: Option to set transparency
		// TODO: Possibility to choose a background color
	}

	@Override
	public void write(Drawable d, double width, double height) throws IOException {
		write(d, 0.0, 0.0, width, height);
	}

	@Override
	public void write(Drawable d, double x, double y, double width, double height) throws IOException {
		Rectangle2D boundsOld = d.getBounds();
		d.setBounds(x, y, width, height);

		int rasterFormat = BufferedImage.TYPE_INT_ARGB;
		if (TYPE_GIF.equals(getMimeType())) {
			rasterFormat = BufferedImage.TYPE_INT_RGB;
		}
		BufferedImage image = new BufferedImage(
				(int)Math.round(width), (int)Math.round(height), rasterFormat);
		d.draw((Graphics2D) image.getGraphics());
		ImageIO.write(image, getMimeType(), getDestination());

		d.setBounds(boundsOld);
	}
}
