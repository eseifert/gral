package openjchart.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <code>Graphics2D</code> implementation that saves all operations to a SVG string.
 */
public class SVGGraphics2D extends Graphics2D {
	private static final Map<Integer, String> STROKE_ENDCAPS;
	private static final Map<Integer, String> STROKE_LINEJOIN;

	static {
		STROKE_ENDCAPS = new HashMap<Integer, String>();
		STROKE_ENDCAPS.put(BasicStroke.CAP_BUTT, "butt");
		STROKE_ENDCAPS.put(BasicStroke.CAP_ROUND, "round");
		STROKE_ENDCAPS.put(BasicStroke.CAP_SQUARE, "square");
		
		STROKE_LINEJOIN = new HashMap<Integer, String>();
		STROKE_LINEJOIN.put(BasicStroke.JOIN_BEVEL, "bevel");
		STROKE_LINEJOIN.put(BasicStroke.JOIN_MITER, "miter");
		STROKE_LINEJOIN.put(BasicStroke.JOIN_ROUND, "round");
	}

	private final Map hints;
	private final StringBuffer document;
	private Rectangle2D bounds;

	private Color background;
	private Color color;
	private Shape clip;
	private Rectangle clipBounds;
	private Composite composite;
	private GraphicsConfiguration deviceConfig;
	private Font font;
	private FontMetrics fontMetrics;
	private FontRenderContext fontRenderContext;
	private Paint paint;
	private Stroke stroke;
	private AffineTransform transform;
	private Color xorMode;

	/**
	 * Constructor that initializes a new <code>SVGGraphics2D</code> instance.
	 */
	public SVGGraphics2D(double x, double y, double width, double height) {
		hints = new HashMap();
		document = new StringBuffer();
		bounds = new Rectangle2D.Double(x, y, width, height);

		background = Color.white;
		color = Color.BLACK;
		composite = AlphaComposite.getInstance(AlphaComposite.CLEAR);
		font = Font.decode(null);
		fontRenderContext = new FontRenderContext(null, false, true);
		paint = color;
		stroke = new BasicStroke(1f);
		transform = new AffineTransform();
		xorMode = Color.BLACK;

		writeHeader();
	}

	@Override
	public void addRenderingHints(Map hints) {
		this.hints.putAll(hints);
	}

	@Override
	public void clip(Shape s) {
		// TODO
	}

	@Override
	public void draw(Shape s) {
		writeShape(s);
		writeClosingDraw();
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		draw(g.getOutline(x, y));
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawString(String str, int x, int y) {
		drawString(str, (float)x, (float)y);
	}

	@Override
	public void drawString(String str, float x, float y) {
		// Encode string
		//str = str;
		// Escape string
		str = str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		// Output
		writeln("<text x=\"", x, "\" y=\"", y, "\">", str, "</text>");
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		drawString(iterator, (float)x, (float)y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		String str = "";
		for (char c = iterator.first(); c != AttributedCharacterIterator.DONE; c = iterator.next()) {
			str += c;
		}
		drawString(str, x, y);
	}

	@Override
	public void fill(Shape s) {
		writeShape(s);
		writeClosingFill();
	}

	@Override
	public Color getBackground() {
		return background;
	}

	@Override
	public Composite getComposite() {
		return composite;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return deviceConfig;
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return fontRenderContext;
	}

	@Override
	public Paint getPaint() {
		return paint;
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return hints.get(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return new RenderingHints(hints);
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public AffineTransform getTransform() {
		return new AffineTransform(transform);
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void rotate(double theta) {
		transform.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		transform.rotate(theta, x, y);
	}

	@Override
	public void scale(double sx, double sy) {
		transform.scale(sx, sy);
	}

	@Override
	public void setBackground(Color color) {
		background = color;
	}

	@Override
	public void setComposite(Composite comp) {
		composite = comp;
	}

	@Override
	public void setPaint(Paint paint) {
		if (paint != null) {
			this.paint = paint;
			if (paint instanceof Color) {
				setColor((Color) paint);
			}
		}
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		hints.put(hintKey, hintValue);
	}

	@Override
	public void setRenderingHints(Map hints) {
		this.hints.putAll(hints);
	}

	@Override
	public void setStroke(Stroke s) {
		stroke = s;
	}

	@Override
	public void setTransform(AffineTransform tx) {
		transform.setTransform(tx);
	}

	@Override
	public void shear(double shx, double shy) {
		transform.shear(shx, shy);
	}

	@Override
	public void transform(AffineTransform tx) {
		transform.concatenate(tx);
	}

	@Override
	public void translate(int x, int y) {
		translate((double)x, (double)y);
	}

	@Override
	public void translate(double tx, double ty) {
		transform.translate(tx, ty);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub
	}

	@Override
	public Graphics create() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO: Use arc command of SVG's <path> tag
		writeShape(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
		writeClosingDraw();
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		writeShape(new Line2D.Double(x1, y1, x2, y2));
		writeClosingDraw();
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		write(new Ellipse2D.Double(x, y, width, height));
		writeClosingDraw();
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		write("<polygon points=\"");
		for (int i = 0; i < nPoints; i++) {
			if (i > 0) {
				write(" ");
			}
			write(xPoints[i], ",", yPoints[i]);
		}
		write("\" ");
		writeClosingDraw();
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		write("<polyline points=\"");
		for (int i = 0; i < nPoints; i++) {
			if (i > 0) {
				write(" ");
			}
			write(xPoints[i], ",", yPoints[i]);
		}
		write("\" ");
		writeClosingDraw();
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		write(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
		writeClosingFill();
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		// TODO: Use arc command of SVG's <path> tag
		writeShape(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.PIE));
		writeClosingFill();
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		write(new Ellipse2D.Double(x, y, width, height));
		writeClosingFill();
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		write("<polygon points=\"");
		for (int i = 0; i < nPoints; i++) {
			if (i > 0) {
				write(" ");
			}
			write(xPoints[i], ",", yPoints[i]);
		}
		write("\" ");
		writeClosingFill();
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		write(new Rectangle2D.Double(x, y, width, height));
		writeClosingFill();
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		write(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
		writeClosingFill();
	}

	@Override
	public Shape getClip() {
		return clip;
	}

	@Override
	public Rectangle getClipBounds() {
		return clipBounds;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return fontMetrics;
	}

	@Override
	public void setClip(Shape clip) {
		this.clip = clip;
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		clip = new Rectangle(x, y, width, height);
	}

	@Override
	public void setColor(Color c) {
		color = c;
	}

	@Override
	public void setFont(Font font) {
		this.font = font;
	}

	@Override
	public void setPaintMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setXORMode(Color c1) {
		xorMode = c1;
	}

	/**
	 * Utility method for writing multiple objects to the SVG document.
	 * @param strs Objects to be written
	 */
	protected void write(Object... strs) {
		for (Object o : strs) {
			String str = o.toString();
			if ((o instanceof Double) || (o instanceof Float)) {
				str = String.format(Locale.ENGLISH, "%.7f", o).replaceAll("\\.?0+$", "");
			}
			document.append(str);
		}
	}

	/**
	 * Utility method for writing a line of multiple objects to the SVG document.
	 * @param strs Objects to be written
	 */
	protected void writeln(Object... strs) {
		write(strs);
		write("\n");
	}

	protected void writeHeader() {
		double x = bounds.getX();
		double y = bounds.getY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writeln("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
		writeln("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.2\" ",
			"x=\"", x, "mm\" y=\"", y, "mm\" ",
			"width=\"", w, "mm\" height=\"", h, "mm\" " +
			"viewBox=\"", x, " ", y, " ", w, " ", h, "\"",
			">"
		);
	}
	
	/**
	 * Utility method for writing a tag closing fragment for drawing operations.
	 */
	protected void writeClosingDraw() {
		write("style=\"fill:none;stroke:", getColorString(color)); 
		if (stroke instanceof BasicStroke) {
			BasicStroke s = (BasicStroke) stroke;
			if (s.getLineWidth() != 1f) {
				write(";stroke-width:", s.getLineWidth());
			}
			if (s.getEndCap() != BasicStroke.CAP_BUTT) {
				write(";stroke-linecap:", STROKE_ENDCAPS.get(s.getEndCap()));
			}
			if (s.getLineJoin() != BasicStroke.JOIN_MITER) {
				write(";stroke-linejoin:", STROKE_LINEJOIN.get(s.getLineJoin()));
			}
			//write(";stroke-miterlimit:", s.getMiterLimit());
			if (s.getDashArray() != null && s.getDashArray().length>0) {
				write(";stroke-dasharray:"); write(s.getDashArray());
				write(";stroke-dashoffset:", s.getDashPhase());
			}
		}
		writeln("\" />");
	}

	/**
	 * Utility method for writing a tag closing fragment for filling operations.
	 */
	protected void writeClosingFill() {
		writeln("style=\"fill:", getColorString(color), ";stroke:none\" />");
	}

	/**
	 * Utility method for writing an arbitrary shape to.
	 * It tries to translate Java2D shapes to the corresponding SVG shape tags.
	 */
	protected void writeShape(Shape s) {
		if (!isDistorted()) {
			double sx = transform.getScaleX();
			double sy = transform.getScaleX();
			double tx = transform.getTranslateX();
			double ty = transform.getTranslateY();
			if (s instanceof Line2D) {
				Line2D l = (Line2D) s;
				double x1 = sx*l.getX1() + tx;
				double y1 = sy*l.getY1() + ty;
				double x2 = sx*l.getX2() + tx;
				double y2 = sy*l.getY2() + ty;
				write("<line x1=\"", x1, "\" y1=\"", y1, "\" x2=\"", x2, "\" y2=\"", y2, "\" ");
				return;
			} else if (s instanceof Rectangle2D) {
				Rectangle2D r = (Rectangle2D) s;
				double x = sx*r.getX() + tx;
				double y = sy*r.getY() + ty;
				double width = sx*r.getWidth();
				double height = sy*r.getHeight();
				write("<rect x=\"", x, "\" y=\"", y, "\" width=\"", width, "\" height=\"", height, "\" ");
				return;
			} else if (s instanceof RoundRectangle2D) {
				RoundRectangle2D r = (RoundRectangle2D) s;
				double x = sx*r.getX() + tx;
				double y = sy*r.getY() + ty;
				double width = sx*r.getWidth();
				double height = sy*r.getHeight();
				double arcWidth = sx*r.getArcWidth();
				double arcHeight = sy*r.getArcHeight();
				write("<rect x=\"", x, "\" y=\"", y, "\" width=\"", width, "\" height=\"", height, "\" rx=\"", arcWidth, "\" ry=\"", arcHeight, "\" ");
				return;
			} else if (s instanceof Ellipse2D) {
				Ellipse2D e = (Ellipse2D) s;
				double x = sx*e.getX() + tx;
				double y = sy*e.getY() + ty;
				double rx = sx*e.getWidth()/2.0;
				double ry = sy*e.getHeight()/2.0;
				write("<ellipse cx=\"", x+rx, "\" cy=\"", y+ry, "\" rx=\"", rx, "\" ry=\"", ry, "\" ");
				return;
			}
		}

		s = transform.createTransformedShape(s);
		write("<path d=\"");
		PathIterator segments = s.getPathIterator(null);
		double[] coords = new double[6];
		for (int i = 0; !segments.isDone(); i++, segments.next()) {
			if (i > 0) {
				write(" ");
			}
			int segmentType = segments.currentSegment(coords);
			switch (segmentType) {
			case PathIterator.SEG_MOVETO:
				write("M", coords[0], ",", coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				write("L", coords[0], ",", coords[1]);
				break;
			case PathIterator.SEG_CUBICTO:
				write("C", coords[0], ",", coords[1], " ", coords[2], ",", coords[3], " ", coords[4], ",", coords[5]);
				break;
			case PathIterator.SEG_QUADTO:
				write("Q", coords[0], ",", coords[1], " ", coords[2], ",", coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				write("Z");
				break;
			}
		}
		write("\" ");
	}

	private static String getColorString(Color c) {
		String color = "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
		if (c.getAlpha() < 255) {
			double opacity = c.getAlpha()/255.0;
			color += ";opacity:" + opacity;
		}
		return color;
	}

	private boolean isDistorted() {
		int type = transform.getType();
		int otherButTranslatedOrScaled = ~(AffineTransform.TYPE_TRANSLATION | AffineTransform.TYPE_MASK_SCALE);
		return (type & otherButTranslatedOrScaled) != 0;
	}

	@Override
	public String toString() {
		return document.toString() + "</svg>\n";
	}

}
