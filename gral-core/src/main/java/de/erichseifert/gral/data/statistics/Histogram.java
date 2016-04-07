package de.erichseifert.gral.data.statistics;

public class Histogram {
	private int breakCount;

	public Histogram(Iterable<Comparable<?>> data, int breakCount) {
		if (breakCount < 1) {
			throw new IllegalArgumentException("Invalid bucket count: " + breakCount +
					" A histogram requires a positive bucket count.");
		}
		this.breakCount = breakCount;
	}

	public int size() {
		return breakCount;
	}
}
