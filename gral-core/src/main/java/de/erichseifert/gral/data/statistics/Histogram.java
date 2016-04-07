package de.erichseifert.gral.data.statistics;

public class Histogram {
	private int breakCount;

	public Histogram(Iterable<Comparable<?>> data, int breakCount) {
		this.breakCount = breakCount;
	}

	public int size() {
		return breakCount;
	}
}
