/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.plots.colors;


/**
 * An abstract base class for implementations of {@code ContinuousColorMapper}
 * that allow to apply a a scaling factor to the values passed to
 * {@link #get(double)}.
 */
public abstract class ScaledContinuousColorMapper
		extends ContinuousColorMapper {
	/** Version id for serialization. */
	private static final long serialVersionUID = 323911118647457851L;

	/** Offset. **/
	private double offset;
	/** Scaling factor. **/
	private double scale;

	/**
	 * Default constructor that initializes a new instance with a default
	 * offset of 0.0 and a scale of 1.0.
	 */
	public ScaledContinuousColorMapper() {
		this(0.0, 1.0);
	}

	/**
	 * Constructor that initializes a new instance with a specified offset and
	 * scaling factor.
	 * @param offset Offset.
	 * @param scale Scaling factor.
	 */
	public ScaledContinuousColorMapper(double offset, double scale) {
		this.offset = offset;
		this.scale = scale;
	}

	/**
	 * Returns the current offset value.
	 * @return Offset value.
	 */
	public double getOffset() {
		return offset;
	}

	/**
	 * Sets a new offset value.
	 * @param offset Offset value.
	 */
	public void setOffset(double offset) {
		this.offset = offset;
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

	/**
	 * Sets offset and scale based on start and end values.
	 * @param start Start value.
	 * @param end End value.
	 */
	public void setRange(double start, double end) {
		setOffset(start);
		setScale(end - start);
	}

	/**
	 * Linearly transforms a value using offset and scale.
	 * @param value Original value.
	 * @return Transformed value.
	 */
	protected Double scale(double value) {
		return (value - getOffset())/getScale();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ScaledContinuousColorMapper)) {
			return false;
		}
		ScaledContinuousColorMapper cm = (ScaledContinuousColorMapper) obj;
		return getOffset() == cm.getOffset() && getScale() == cm.getScale()
			&& getMode() == cm.getMode();
	}

	@Override
	public int hashCode() {
		long bits = Double.doubleToLongBits(getOffset());
		bits ^= Double.doubleToLongBits(getScale()) * 31;
		return ((int) bits) ^ ((int) (bits >> 32));
	}
}
