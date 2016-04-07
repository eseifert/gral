package de.erichseifert.gral.data.statistics;

import java.util.Arrays;
import java.util.Iterator;

public class Histogram implements Iterable<Integer> {
	private Integer[] bins;
	private int breakCount;

	public Histogram(Iterable<Comparable<?>> data, int breakCount) {
		if (breakCount < 1) {
			throw new IllegalArgumentException("Invalid bucket count: " + breakCount +
					" A histogram requires a positive bucket count.");
		}
		this.breakCount = breakCount;
		bins = new Integer[breakCount];
		Arrays.fill(bins, new Integer(0));

		Statistics statistics = new Statistics(data);
		double minValue = statistics.get(Statistics.MIN);
		double maxValue = statistics.get(Statistics.MAX);
		double range = maxValue - minValue;
		double binWidth = range / breakCount;
		for (Comparable<?> value : data) {
			if (!(value instanceof Number)) {
				continue;
			}
			for (int binIndex = 0; binIndex < bins.length; binIndex++) {
				double lowerBinLimit = minValue + binIndex*binWidth;
				double upperBinLimit = minValue + (binIndex + 1)*binWidth;
				double doubleValue = ((Number) value).doubleValue();
				if (doubleValue >= lowerBinLimit && doubleValue < upperBinLimit) {
					bins[binIndex]++;
				}
			}
		}
	}

	public int size() {
		return breakCount;
	}

	@Override
	public Iterator<Integer> iterator() {
		return Arrays.asList(bins).iterator();
	}
}
