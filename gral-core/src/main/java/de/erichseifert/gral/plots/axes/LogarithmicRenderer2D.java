/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.erichseifert.gral.plots.axes.Tick.TickType;
import de.erichseifert.gral.util.MathUtils;


/**
 * Class that renders axes with a logarithmic scale in two dimensional space.
 */
public class LogarithmicRenderer2D extends AbstractAxisRenderer2D {

	/**
	 * Creates a new renderer for logarithmic scaled axes in two-dimensional
	 * space.
	 */
	public LogarithmicRenderer2D() {
	}

	@Override
	public double worldToView(Axis axis, Number value, boolean extrapolate) {
		checkAxisBounds(axis);
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
		double minLog = (min > 0.0) ? Math.log10(min) : 0.0;
		double maxLog = (max > 0.0) ? Math.log10(max) : 1.0;
		return (Math.log10(value.doubleValue()) - minLog)*getShapeLength() /
			(maxLog - minLog);
	}

	@Override
	public Number viewToWorld(Axis axis, double value, boolean extrapolate) {
		checkAxisBounds(axis);
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
		double minLog = (min > 0.0) ? Math.log10(min) : 0.0;
		double maxLog = (max > 0.0) ? Math.log10(max) : 1.0;
		return Math.pow(10.0,
				value*(maxLog - minLog)/getShapeLength() + minLog);
	}

	@Override
	public List<Tick> getTicks(Axis axis) {
		checkAxisBounds(axis);
		List<Tick> ticks = new LinkedList<Tick>();
		double tickSpacing =
			this.<Number>getSetting(TICKS_SPACING).doubleValue();
		if (tickSpacing <= 0.0) {
			return ticks;
		}
		int ticksMinorCount = this.<Integer>getSetting(TICKS_MINOR_COUNT);
		double tickSpacingMinor = (ticksMinorCount > 0)
			? tickSpacing/(ticksMinorCount + 1) : tickSpacing;
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();

		final double BASE = 10.0;
		double powerMin = MathUtils.magnitude(BASE, min);
		double powerMax = MathUtils.magnitude(BASE, max);
		double minTickMajor = MathUtils.ceil(min, powerMin*tickSpacing);

		int ticksPerPower = (int)Math.floor(BASE/tickSpacingMinor);
		int initialTicksMinor = (int)Math.floor((minTickMajor - min) /
				(powerMin*tickSpacingMinor));

		Set<Number> tickPositions = new HashSet<Number>();
		Set<Number> tickPositionsCustom = getTickPositionsCustom();
		// Add major ticks
		int i = 0;
		for (double power = powerMin; power <= powerMax; power *= BASE) {
			double multipliedTickSpacingMinor = power*tickSpacingMinor;
			double minTick = MathUtils.ceil(power, multipliedTickSpacingMinor);

			for (int pi = 0; pi < ticksPerPower; pi++) {
				double tickPositionWorld =
					minTick + pi*multipliedTickSpacingMinor;
				if (tickPositionWorld < min) {
					continue;
				} else if (tickPositionWorld > max) {
					break;
				}
				TickType tickType = TickType.MINOR;
				if ((i++ - initialTicksMinor) % (ticksMinorCount + 1) == 0) {
					tickType = TickType.MAJOR;
				}
				Tick tick = getTick(tickType, axis, tickPositionWorld);
				if (tick.getPosition() != null
						&& !tickPositions.contains(tickPositionWorld)
						&& !tickPositionsCustom.contains(tickPositionWorld)) {
					ticks.add(tick);
					tickPositions.add(tickPositionWorld);
				}
			}
		}
		// Add custom ticks
		Map<Number, String> labelsCustom = getSetting(TICKS_CUSTOM);
		if (labelsCustom != null) {
			for (Number tickPositionWorldObj : labelsCustom.keySet()) {
				double tickPositionWorld = tickPositionWorldObj.doubleValue();
				if (tickPositionWorld >= min && tickPositionWorld <= max) {
					Tick tick = getTick(
							TickType.CUSTOM, axis, tickPositionWorld);
					ticks.add(tick);
				}
			}
		}

		return ticks;
	}

	/**
	 * Utility method that makes sure that axis bounds comply to rules of
	 * logarithmic axes.
	 * @param axis Axis to be checked
	 */
	private static void checkAxisBounds(Axis axis) {
		if ((axis.getMin().doubleValue() < 0.0)
				|| (axis.getMax().doubleValue() < 0.0)) {
			throw new IllegalStateException(
					"Axis bounds must be greater than or equal to zero."); //$NON-NLS-1$
		}
	}

}
