package de.erichseifert.gral.graphics;

public abstract class AbstractLayout implements Layout {
	private static final long serialVersionUID = 5961215915010787754L;

	/** Horizontal spacing of components. */
	private final double gapX;
	/** Vertical spacing of components. */
	private final double gapY;

	public AbstractLayout(double gapX, double gapY) {
		this.gapX = gapX;
		this.gapY = gapY;
	}

	@Override
	public double getGapX() {
		return gapX;
	}

	@Override
	public double getGapY() {
		return gapY;
	}
}
