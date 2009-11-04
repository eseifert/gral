package openjchart;

import java.awt.geom.Rectangle2D;

import openjchart.util.Dimension2D;

/**
 * Abstract implementation of the Drawable interface.
 * Implemented functionality includes getting and setting the bounding
 * rectangle in different ways.
 * @see Drawable
 */
public abstract class AbstractDrawable implements Drawable {
	private final Rectangle2D bounds;

	/**
	 * Basic constructor.
	 */
	public AbstractDrawable() {
		bounds = new Rectangle2D.Double();
	}

	@Override
	public Rectangle2D getBounds() {
		Rectangle2D b = new Rectangle2D.Double();
		b.setFrame(bounds);
		return b;
	}

	@Override
	public double getHeight() {
		return bounds.getHeight();
	}

	@Override
	public double getWidth() {
		return bounds.getWidth();
	}

	@Override
	public double getX() {
		return bounds.getX();
	}

	@Override
	public double getY() {
		return bounds.getY();
	}

	@Override
	public void setBounds(Rectangle2D bounds) {
		this.bounds.setFrame(bounds);
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
		this.bounds.setFrame(x, y, width, height);
	}

	@Override
	public Dimension2D getPreferredSize() {
		return new Dimension2D.Double();
	}
}
