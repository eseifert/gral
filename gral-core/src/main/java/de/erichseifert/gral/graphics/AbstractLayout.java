package de.erichseifert.gral.graphics;

public abstract class AbstractLayout implements Layout {
	private static final long serialVersionUID = 5961215915010787754L;

	/** Horizontal spacing of components. */
	private double gapX;
	/** Vertical spacing of components. */
	private double gapY;

	public AbstractLayout(double gapX, double gapY) {
		this.gapX = gapX;
		this.gapY = gapY;
	}

	@Override
	public double getGapX() {
		return gapX;
	}

	@Override
	public void setGapX(double gapX) {
		this.gapX = gapX;
	}

	@Override
	public double getGapY() {
		return gapY;
	}

	@Override
	public void setGapY(double gapY) {
		this.gapY = gapY;
	}
}
