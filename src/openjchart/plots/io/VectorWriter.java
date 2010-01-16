package openjchart.plots.io;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;

import openjchart.Drawable;
import openjchart.util.EPSGraphics2D;
import openjchart.util.SVGGraphics2D;

public class VectorWriter extends AbstractDrawableWriter {
	static {
		WriterCapabilities SVG_CAPABILITIES = new WriterCapabilities(
			FORMAT_SVG,
			"Scalable Vector Graphics",
			"image/svg+xml",
			"svg", "svgz"
		);

		WriterCapabilities EPS_CAPABILITIES = new WriterCapabilities(
			FORMAT_EPS,
			"Encapsulated PostScript",
			"application/postscript",
			"eps", "epsf", "epsi"
		);

		CAPABILITIES = new WriterCapabilities[] {
				SVG_CAPABILITIES, EPS_CAPABILITIES
		};
	}

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
		if (FORMAT_EPS.equals(getFormat())) {
			g2d = new EPSGraphics2D(x, y, width, height);
		} else if (FORMAT_SVG.equals(getFormat())) {
			g2d = new SVGGraphics2D(x, y, width, height);
		}
		d.draw(g2d);
		getDestination().write(g2d.toString().getBytes());

		d.setBounds(boundsOld);
	}

}
