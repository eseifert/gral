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
import java.awt.geom.GeneralPath;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <code>Graphics2D</code> implementation that saves all operations to a SVG string.
 */
public class EPSGraphics2D extends Graphics2D {
	protected static final double MM_IN_UNITS = 72.0 / 25.4;

	private static final Map<Integer, Integer> STROKE_ENDCAPS;
	private static final Map<Integer, Integer> STROKE_LINEJOIN;

	static {
		STROKE_ENDCAPS = new HashMap<Integer, Integer>();
		STROKE_ENDCAPS.put(BasicStroke.CAP_BUTT, 0);
		STROKE_ENDCAPS.put(BasicStroke.CAP_ROUND, 1);
		STROKE_ENDCAPS.put(BasicStroke.CAP_SQUARE, 2);
		
		STROKE_LINEJOIN = new HashMap<Integer, Integer>();
		STROKE_LINEJOIN.put(BasicStroke.JOIN_BEVEL, 2);
		STROKE_LINEJOIN.put(BasicStroke.JOIN_MITER, 0);
		STROKE_LINEJOIN.put(BasicStroke.JOIN_ROUND, 1);
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
	public EPSGraphics2D(double x, double y, double width, double height) {
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
		// TODO: Encode string
		//byte[] bytes = str.getBytes("ISO-8859-1");
		// Escape string
		str = str.replaceAll("\\", "\\\\")
			.replaceAll("\n", "\\n").replaceAll("\r", "\\r")
			.replaceAll("\t", "\\t").replaceAll("\b", "\\b").replaceAll("\f", "\\f")
			.replaceAll("(", "\\(").replaceAll(")", "\\)");
		// Output
		writeln(x, " ", y, " M (", str, ") show");
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
		if (onStroke) {
			Shape sStroke = getStroke().createStrokedShape(s);
			return sStroke.intersects(rect);
		} else  {
			return s.intersects(rect);
		}
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
		BasicStroke bsPrev;
		if (getStroke() instanceof BasicStroke) {
			bsPrev = (BasicStroke) getStroke();
		} else {
			bsPrev = new BasicStroke();
		}

		stroke = s;

		if (s instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke) s;
			if (bs.getLineWidth() != bsPrev.getLineWidth()) {
				writeln(bs.getLineWidth(), " setlinewidth");
			}
			if (bs.getLineJoin() != bsPrev.getLineJoin()) {
				writeln(STROKE_LINEJOIN.get(bs.getLineWidth()), " setlinejoin");
			}
			if (bs.getEndCap() != bsPrev.getEndCap()) {
				writeln(STROKE_ENDCAPS.get(bs.getEndCap()), " setlinecap");
			}
			if ((!Arrays.equals(bs.getDashArray(), bsPrev.getDashArray())) ||
				(bs.getDashPhase() != bsPrev.getDashPhase())) {
				write("[");
				float[] pattern = bs.getDashArray();
				for (int i = 0; i < pattern.length; i++) {
					if (i > 0) {
						write(" ");
					}
					write(pattern[i]);
				}
				writeln("] ", bs.getDashPhase(), " setlinedash");
			}
		}
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
		return this;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
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
		GeneralPath p = new GeneralPath();
		for (int i = 0; i < nPoints; i++) {
			if (i == 0) {
				p.moveTo(xPoints[i], yPoints[i]);
			} else {
				p.lineTo(xPoints[i], yPoints[i]);
			}
		}
		p.closePath();
		draw(p);
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		GeneralPath p = new GeneralPath();
		for (int i = 0; i < nPoints; i++) {
			if (i == 0) {
				p.moveTo(xPoints[i], yPoints[i]);
			} else {
				p.lineTo(xPoints[i], yPoints[i]);
			}
		}
		draw(p);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		write(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
		writeClosingFill();
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
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
		GeneralPath p = new GeneralPath();
		for (int i = 0; i < nPoints; i++) {
			if (i == 0) {
				p.moveTo(xPoints[i], yPoints[i]);
			} else {
				p.lineTo(xPoints[i], yPoints[i]);
			}
		}
		p.closePath();
		fill(p);
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
		if (c != null && (color.getRed() != c.getRed() || color.getGreen() != c.getGreen() || color.getBlue() != c.getBlue())) {
			color = c;
			writeln(c.getRed()/255.0, " ", c.getGreen()/255.0, " ", c.getBlue()/255.0, " rgb");
		}
	}

	@Override
	public void setFont(Font font) {
		if (!this.font.equals(font)) {
			this.font = font;
			writeln("/", font.getPSName(), " ", font.getSize2D(), " selectfont");
		}
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
		int x = (int)Math.floor(bounds.getX() * MM_IN_UNITS);
		int y = (int)Math.floor(bounds.getY() * MM_IN_UNITS);
		int w = (int)Math.ceil(bounds.getWidth() * MM_IN_UNITS);
		int h = (int)Math.ceil(bounds.getHeight() * MM_IN_UNITS);

		writeln("%!PS-Adobe-3.0 EPSF-3.0");
		writeln("%%BoundingBox: ", x, " ", y, " ", w, " ", h);
		writeln("%%LanguageLevel: 2");
		writeln("%%Pages: 1");
		writeln("%%Page: 1 1");

		// Utility functions
		writeln("% Utility functions");
		writeln("/M /moveto load def");
		writeln("/L /lineto load def");
		writeln("/C /curveto load def");
		writeln("/Z /closepath load def");
		writeln("/RL /rlineto load def");
		writeln("/rgb /setrgbcolor load def");
		writeln("/rect { ",
				"/height exch def /width exch def /y exch def /x exch def ",
				"x y M width 0 RL 0 height RL width neg 0 RL ",
				"} bind def");
		// TODO: Round rectangle
		writeln("/rrect { ",
				"/archeight exch def /arcwidth exch def /height exch def /width exch def /y exch def /x exch def ",
				"x y M width 0 RL 0 height RL width neg 0 RL ",
				"} bind def");
		writeln("/ellipse { ",
			"/endangle exch def /startangle exch def /ry exch def /rx exch def /y exch def /x exch def ",
			"/savematrix matrix currentmatrix def ",
			"x y translate rx ry scale 0 0 1 startangle endangle arc ",
			"savematrix setmatrix ",
			"} bind def");
		// Save state
		writeln("gsave  % Save current state");
		// Adjust EPS page size and page origin
		writeln("% Move origin to upper left and scale to millimeters");
		writeln("0 ", h, " translate 1 -1 scale");
		writeln(MM_IN_UNITS, " ", MM_IN_UNITS, " scale");
	}

	/**
	 * Utility method for writing a tag closing fragment for drawing operations.
	 */
	protected void writeClosingDraw() {
		writeln(" stroke");
	}

	/**
	 * Utility method for writing a tag closing fragment for filling operations.
	 */
	protected void writeClosingFill() {
		writeln(" fill");
	}

	/**
	 * Utility method for writing an arbitrary shape to.
	 * It tries to translate Java2D shapes to the corresponding SVG shape tags.
	 */
	protected void writeShape(Shape s) {
		write("newpath ");
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
				write(x1, " ", y1, " M ", x2, " ", y2, " L");
				return;
			} else if (s instanceof Rectangle2D) {
				Rectangle2D r = (Rectangle2D) s;
				double x = sx*r.getX() + tx;
				double y = sy*r.getY() + ty;
				double width = sx*r.getWidth();
				double height = sy*r.getHeight();
				write(x, " ", y, " ", width, " ", height, " rect Z");
				return;
			} else if (s instanceof RoundRectangle2D) {
				// TODO: Use arc
				RoundRectangle2D r = (RoundRectangle2D) s;
				double x = sx*r.getX() + tx;
				double y = sy*r.getY() + ty;
				double width = sx*r.getWidth();
				double height = sy*r.getHeight();
				double arcWidth = sx*r.getArcWidth();
				double arcHeight = sy*r.getArcWidth();
				write(x, " ", y, " ", width, " ", height, " ", arcWidth, " ", arcHeight, " rrect Z");
				return;
			} else if (s instanceof Ellipse2D) {
				// TODO: Use arc
				Ellipse2D e = (Ellipse2D) s;
				double x = sx*e.getX() + tx;
				double y = sy*e.getY() + ty;
				double rx = sx*e.getWidth() / 2.0;
				double ry = sy*e.getHeight() / 2.0;
				write(x, " ", y, " ", rx, " ", ry, " ", 0.0, " ", 360.0, " ellipse Z");
				return;
			} else if (s instanceof Arc2D) {
				// TODO: Use arc
				Arc2D e = (Arc2D) s;
				double x = sx*e.getX() + tx;
				double y = sy*e.getY() + ty;
				double rx = sx*e.getWidth() / 2.0;
				double ry = sy*e.getHeight() / 2.0;
				double startAngle = e.getAngleStart();
				double endAngle = e.getAngleExtent();
				write(x, " ", y, " ", rx, " ", ry, " ", startAngle, " ", endAngle, " ellipse Z");
				return;
			}
		}

		s = transform.createTransformedShape(s);
		PathIterator segments = s.getPathIterator(null);
		double[] coordsCur = new double[6];
		double[] pointPrev = new double[2];
		for (int i = 0; !segments.isDone(); i++, segments.next()) {
			if (i > 0) {
				write(" ");
			}
			int segmentType = segments.currentSegment(coordsCur);
			switch (segmentType) {
			case PathIterator.SEG_MOVETO:
				write(coordsCur[0], " ", coordsCur[1], " M");
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];
				break;
			case PathIterator.SEG_LINETO:
				write(coordsCur[0], " ", coordsCur[1], " L");
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];
				break;
			case PathIterator.SEG_CUBICTO:
				write(coordsCur[0], " ", coordsCur[1], " ", coordsCur[2], " ", coordsCur[3], " ", coordsCur[4], " ", coordsCur[5], " C");
				pointPrev[0] = coordsCur[4];
				pointPrev[1] = coordsCur[5];
				break;
			case PathIterator.SEG_QUADTO:
				double x1 = pointPrev[0] + 2.0/3.0*(coordsCur[0] - pointPrev[0]);
				double y1 = pointPrev[1] + 2.0/3.0*(coordsCur[1] - pointPrev[1]);
				double x2 = coordsCur[0] + 1.0/3.0*(coordsCur[2] - coordsCur[0]);
				double y2 = coordsCur[1] + 1.0/3.0*(coordsCur[3] - coordsCur[1]);
				double x3 = coordsCur[2];
				double y3 = coordsCur[3];
				write(x1, " ", y1, " ", x2, " ", y2, " ", x3, " ", y3, " C");
				pointPrev[0] = x3;
				pointPrev[1] = y3;
				break;
			case PathIterator.SEG_CLOSE:
				write("Z");
				break;
			}
		}
	}

	private boolean isDistorted() {
		int type = transform.getType();
		int otherButTranslatedOrScaled = ~(AffineTransform.TYPE_TRANSLATION | AffineTransform.TYPE_MASK_SCALE);
		return (type & otherButTranslatedOrScaled) != 0;
	}

	@Override
	public String toString() {
		return document.toString() + "grestore  % Restore state\n%%EOF\n";
	}

}
