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
package de.erichseifert.gral.plots.axes;

import java.util.List;
import java.util.Set;

import de.erichseifert.gral.plots.axes.Tick.TickType;
import de.erichseifert.gral.util.MathUtils;

/**
 * Class that renders axes with a linear scale in two dimensional space.
 */
public class LinearRenderer2D extends AbstractAxisRenderer2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = -1257582880797196423L;

	/**
	 * Creates a new renderer for linear axes in two-dimensional space.
	 */
	public LinearRenderer2D() {
	}

	/**
	 * Converts a world (axis) coordinate value to a view (screen) coordinate
	 * value.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @return Screen coordinate value
	 */
	public double worldToView(Axis axis, Number value, boolean extrapolate) {
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		double val = value.doubleValue();
		if (!extrapolate) {
			if (val <= min) {
				return 0.0;
			}
			if (val >= max) {
				return getShapeLength();
			}
		}
		return (val - min)/(max - min)*getShapeLength();
	}

	/**
	 * Converts a view (screen) coordinate value to a world (axis) coordinate
	 * value.
	 * @param axis Axis
	 * @param value View coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @return World coordinate value
	 */
	public Number viewToWorld(Axis axis, double value, boolean extrapolate) {
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		if (!extrapolate) {
			if (value <= 0.0) {
				return min;
			}
			if (value >= getShapeLength()) {
				return max;
			}
		}
		return value/getShapeLength()*(max - min) + min;
	}

	@Override
	protected void createTicks(List<Tick> ticks, Axis axis, double min,
			double max, Set<Double> tickPositions, boolean isAutoSpacing) {
		double tickSpacing = 1.0;
		int ticksMinorCount = 3;
		if (isAutoSpacing) {
			// TODO Use number of screen units to decide whether to subdivide
			double range = max - min;
			// 1-steppings (0.1, 1, 10)
			tickSpacing = MathUtils.magnitude(10.0, range/4.0);
			// 2-steppings (0.2, 2, 20)
			if (range/tickSpacing > 8.0) {
				tickSpacing *= 2.0;
				ticksMinorCount = 1;
			}
			// 5-steppings (0.5, 5, 50)
			if (range/tickSpacing > 8.0) {
				tickSpacing *= 2.5;
				ticksMinorCount = 4;
			}
		} else {
			tickSpacing = getTickSpacing().doubleValue();
			ticksMinorCount = getMinorTicksCount();
		}

		double tickSpacingMinor = tickSpacing;
		if (ticksMinorCount > 0) {
			tickSpacingMinor = tickSpacing/(ticksMinorCount + 1);
		}

		double minTickMajor = MathUtils.ceil(min, tickSpacing);
		double minTickMinor = MathUtils.ceil(min, tickSpacingMinor);

		int ticksTotal = (int) Math.ceil((max - min)/tickSpacingMinor);
		int initialTicksMinor = (int) ((minTickMajor - min)/tickSpacingMinor);

		// Add major and minor ticks
		// (Use integer to avoid rounding errors)
		for (int tickCur = 0; tickCur < ticksTotal; tickCur++) {
			double tickPositionWorld = minTickMinor + tickCur*tickSpacingMinor;
			if (tickPositions.contains(tickPositionWorld)) {
				continue;
			}
			TickType tickType = TickType.MINOR;
			if ((tickCur - initialTicksMinor) % (ticksMinorCount + 1) == 0) {
				tickType = TickType.MAJOR;
			}
			Tick tick = getTick(tickType, axis, tickPositionWorld);
			if (tick.position != null) {
				ticks.add(tick);
				tickPositions.add(tickPositionWorld);
			}
		}
	}
}
