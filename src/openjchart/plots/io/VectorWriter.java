package openjchart.plots.io;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;

import openjchart.Drawable;
import vectorgraphics2d.EPSGraphics2D;
import vectorgraphics2d.SVGGraphics2D;

/**
 * Class that stores Drawables as vector graphics.
 * Supported formats:
 * <ul>
 * <li>EPS</li>
 * <li>SVG</li>
 * </ul>
 */
public class VectorWriter extends AbstractDrawableWriter {
	static {
		WriterCapabilities EPS_CAPABILITIES = new WriterCapabilities(
			"EPS",
			"Encapsulated PostScript",
			TYPE_EPS,
			"eps", "epsf", "epsi"
		);
		addCapabilities(EPS_CAPABILITIES);

		WriterCapabilities SVG_CAPABILITIES = new WriterCapabilities(
			"SVG",
			"Scalable Vector Graphics",
			TYPE_SVG,
			"svg", "svgz"
		);
		addCapabilities(SVG_CAPABILITIES);
	}

	/**
	 * Creates a new VectorWriter object with the specified destination and
	 * format.
	 * @param destination Output destination.
	 * @param format Output format.
	 */
	protected VectorWriter(OutputStream destination, String format) {
		super(destination, format);
	}

	@Override
	public void write(Drawable d, double width, double height) throws IOException {
		write(d, 0.0, 0.0, width, height);
	}

	@Override
	public void write(Drawable d, double x, double y, double width,	double height) throws IOException {
		Rectangle2D boundsOld = d.getBounds();
		d.setBounds(x, y, width, height);

		Graphics2D g2d = null;
		if (TYPE_EPS.equals(getMimeType())) {
			g2d = new EPSGraphics2D(x, y, width, height);
		} else if (TYPE_SVG.equals(getMimeType())) {
			g2d = new SVGGraphics2D(x, y, width, height);
		}
		d.draw(g2d);
		getDestination().write(g2d.toString().getBytes());

		d.setBounds(boundsOld);
	}

}
