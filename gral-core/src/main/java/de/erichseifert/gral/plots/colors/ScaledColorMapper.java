package de.erichseifert.gral.plots.colors;

/**
 * An abstract implementation of ColorMapper that serves as a base class for
 * mappers that allow to apply a a scaling factor to the values passed to
 * {@link ColorMapper#get(double)}.
 */
public abstract class ScaledColorMapper implements ColorMapper {
	/** Scaling factor. **/
	private double scale;

	/**
	 * Default constructor that initializes a new instance with a default
	 * scale of 1.0.
	 */
	public ScaledColorMapper() {
		scale = 1.0;
	}

	/**
	 * Constructor that initializes a new instance with a specified scaling
	 * factor.
	 * @param scale Scaling factor.
	 */
	public ScaledColorMapper(double scale) {
		this.scale = scale;
	}

	/**
	 * Returns the current scaling factor.
	 * @return Scaling factor.
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * Sets a new scaling factor for passed values.
	 * @param scale Scaling factor.
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}
}
