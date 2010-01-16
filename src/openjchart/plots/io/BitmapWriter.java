package openjchart.plots.io;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import openjchart.Drawable;

public class BitmapWriter extends AbstractDrawableWriter {
	static {
		WriterCapabilities PNG_CAPABILITIES = new WriterCapabilities(
			FORMAT_PNG,
			"Portable Network Grahpics",
			"image/png",
			"png"
		);

		WriterCapabilities BMP_CAPABILITIES = new WriterCapabilities(
			FORMAT_BMP,
			"Windows Bitmap",
			"image/bmp",
			"bmp", "dib"
		);

		WriterCapabilities WBMP_CAPABILITIES = new WriterCapabilities(
			FORMAT_WBMP,
			"Wireless Application Protocol Bitmap Format",
			"image/vnd.wap.wbmp",
			"wbmp"
		);

		WriterCapabilities GIF_CAPABILITIES = new WriterCapabilities(
			FORMAT_GIF,
			"Graphics Interchange Format",
			"image/gif",
			"gif"
		);

		WriterCapabilities JPG_CAPABILITIES = new WriterCapabilities(
			FORMAT_JPG,
			"JPEG",
			"image/jpeg",
			"jpg", "jpeg", "jpe", "jif", "jfif", "jfi"
		);

		CAPABILITIES = new WriterCapabilities[] {
				PNG_CAPABILITIES, BMP_CAPABILITIES, WBMP_CAPABILITIES,
				GIF_CAPABILITIES, JPG_CAPABILITIES
		};
	}

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
		if (FORMAT_GIF.equals(getFormat())) {
			rasterFormat = BufferedImage.TYPE_INT_RGB;
		}
		BufferedImage image = new BufferedImage(
				(int)Math.round(width), (int)Math.round(height), rasterFormat);
		d.draw((Graphics2D) image.getGraphics());
		ImageIO.write(image, getFormat(), getDestination());

		d.setBounds(boundsOld);
	}

	public static boolean isVectorFormat(String format) {
		if (FORMAT_EPS.equals(format)) {
			return true;
		}
		if (FORMAT_SVG.equals(format)) {
			return true;
		}
		return false;
	}

}
