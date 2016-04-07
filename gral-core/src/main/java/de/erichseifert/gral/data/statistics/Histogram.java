package de.erichseifert.gral.data.statistics;

import java.util.Arrays;
import java.util.Iterator;

public class Histogram implements Iterable<Integer> {
	private Iterable<Comparable<?>> data;
	private Number[] breaks;
	private Integer[] bins;
	private int binCount;

	public Histogram(Iterable<Comparable<?>> data, int binCount) {
		if (binCount < 1) {
			throw new IllegalArgumentException("Invalid bucket count: " + binCount +
					" A histogram requires a positive bucket count.");
		}
		this.data = data;
		this.binCount = binCount;
		bins = new Integer[binCount];
		Arrays.fill(bins, new Integer(0));

		this.breaks = new Number[binCount + 1];
		Statistics statistics = new Statistics(data);
		double minValue = statistics.get(Statistics.MIN);
		double maxValue = statistics.get(Statistics.MAX);
		double range = maxValue - minValue;
		double binWidth = range/binCount;
		for (int breakIndex = 0; breakIndex < breaks.length; breakIndex++) {
			breaks[breakIndex] = minValue + breakIndex*binWidth;
		}

		computeDistribution();
	}

	public Histogram(Iterable<Comparable<?>> data, Number... breaks) {
		this.data = data;
		this.breaks = breaks;
		binCount = breaks.length - 1;
		bins = new Integer[binCount];
		Arrays.fill(bins, new Integer(0));

		computeDistribution();
	}

	private void computeDistribution() {
		for (Comparable<?> value : data) {
			if (!(value instanceof Number)) {
				continue;
			}
			for (int binIndex = 0; binIndex < bins.length; binIndex++) {
				double lowerBinLimit = breaks[binIndex].doubleValue();
				double upperBinLimit = breaks[binIndex + 1].doubleValue();
				double doubleValue = ((Number) value).doubleValue();
				if (doubleValue >= lowerBinLimit && doubleValue < upperBinLimit) {
					bins[binIndex]++;
				}
			}
		}
	}

	public int size() {
		return binCount;
	}

	@Override
	public Iterator<Integer> iterator() {
		return Arrays.asList(bins).iterator();
	}
}
