/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots.axes;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import openjchart.plots.DataPoint2D;
import openjchart.util.MathUtils;

/**
 * Class that renders 2-dimensional axes with a logarithmic scale.
 */
public class LogarithmicRenderer2D extends AbstractAxisRenderer2D {

	/**
	 * Creates a new LogarithmicRenderer2D object.
	 */
	public LogarithmicRenderer2D() {
	}

	@Override
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
		double axisMin = axis.getMin().doubleValue();
		double axisMax = axis.getMax().doubleValue();
		double axisMinLog = (axisMin > 0.0) ? Math.log10(axisMin) : 0.0;
		double axisMaxLog = (axisMax > 0.0) ? Math.log10(axisMax) : 1.0;
		return (Math.log10(value.doubleValue()) - axisMinLog)*getShapeLength() / (axisMaxLog - axisMinLog);
	}

	@Override
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
		double axisMin = axis.getMin().doubleValue();
		double axisMax = axis.getMax().doubleValue();
		double axisMinLog = (axisMin > 0.0) ? Math.log10(axisMin) : 0.0;
		double axisMaxLog = (axisMax > 0.0) ? Math.log10(axisMax) : 1.0;
		return Math.pow(10.0, value*(axisMaxLog - axisMinLog)/getShapeLength() + axisMinLog);
	}

	@Override
	public List<DataPoint2D> getTicks(Axis axis) {
		double tickSpacing = getSetting(KEY_TICK_SPACING);
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();

		final double BASE = 10.0;
		double powerMin = Math.pow(BASE, Math.floor(Math.log10(min)));
		double powerMax = Math.pow(BASE, Math.floor(Math.log10(max)));

		List<DataPoint2D> ticks = new LinkedList<DataPoint2D>();
		Set<Double> tickPositions = new HashSet<Double>();
		for (double power = powerMin; power <= powerMax; power *= BASE) {
			double step = power*tickSpacing;
			double powerNext = power*BASE;
			for (double tickPositionWorld = step; tickPositionWorld <= powerNext;
					tickPositionWorld = MathUtils.round(tickPositionWorld + step, 1e-14)) {
				if (tickPositionWorld < min) {
					continue;
				} else if (tickPositionWorld > max) {
					break;
				}
				DataPoint2D tick = getTick(axis, tickPositionWorld);
				if (tick.getPosition() != null && !tickPositions.contains(tickPositionWorld)) {
					ticks.add(tick);
					tickPositions.add(tickPositionWorld);
				}
			}
		}

		// Add custom ticks
		ticks.addAll(getCustomTicks(axis));

		return ticks;
	}
}
