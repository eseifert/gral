/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.data.filters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.erichseifert.gral.util.WindowIterator;

/**
 * <p>A {@link Filter} that replaces each value by a weighted sum of a window of
 * neighboring values. The weights are given as a {@link Kernel}, which also
 * decides the size of the window and which of its positions is the current
 * value.</p>
 *
 * <pre>
 * // Smooth with a binomial kernel of five values, which is already normalized.
 * Kernel kernel = Kernel.getBinomial(5);
 * for (double smoothed : new ConvolutionFilter&lt;&gt;(values, kernel)) {
 *     …
 * }
 * </pre>
 *
 * <p>An unnormalized kernel scales the result, so a smoothing kernel is usually
 * passed through {@link Kernel#normalize()} first. There is no padding at the
 * ends: <i>m</i> input values and a kernel of <i>n</i> weights yield
 * <i>m&nbsp;&minus;&nbsp;n&nbsp;+&nbsp;1</i> results, and none at all if the
 * input is shorter than the kernel. Use {@link Convolution} if a value is
 * needed for every row of a data source.</p>
 *
 * @param <T> Type of the values being filtered.
 */
public class ConvolutionFilter<T extends Number & Comparable<T>> implements Filter<T> {
	/** Filtered values. */
	private final List<Double> filtered;
	private final Iterator<List<T>> windowIterator;

	/**
	 * Initializes a new filter and computes all of its values immediately.
	 * @param data Values to be filtered.
	 * @param kernel Weights to apply to each window of values.
	 */
	public ConvolutionFilter(Iterable<T> data, Kernel kernel) {
		filtered = new LinkedList<>();

		windowIterator = new WindowIterator<>(data.iterator(), kernel.size());

		while (windowIterator.hasNext()) {
			List<T> window = windowIterator.next();
			double convolvedValue = 0.0;
			for (int windowIndex = 0; windowIndex < window.size(); windowIndex++) {
				int kernelIndex = windowIndex - kernel.getOffset();
				convolvedValue += kernel.get(kernelIndex)*window.get(windowIndex).doubleValue();
			}
			filtered.add(convolvedValue);
		}
	}

	@Override
	public Iterator<Double> iterator() {
		return filtered.iterator();
	}
}
