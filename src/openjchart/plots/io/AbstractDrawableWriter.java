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

package openjchart.plots.io;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class that implements the basic functions of a DrawableWriter.
 * Functionality includes the implementation of getters.
 */
public abstract class AbstractDrawableWriter implements DrawableWriter {
	private static final Set<WriterCapabilities> capabilities = new HashSet<WriterCapabilities>();

	private final String mimeType;

	/**
	 * Creates a new AbstractDrawableWriter object with the specified
	 * destination and format.
	 * @param mimeType MIME-Type.
	 */
	protected AbstractDrawableWriter(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns a Set with WriterCapabilities for all supported formats.
	 * @return WriterCapabilities.
	 */
	public static Set<WriterCapabilities> getCapabilities() {
		return Collections.unmodifiableSet(capabilities);
	}

	/**
	 * Adds the specified WriterCapabilities to the Set of supported formats.
	 * @param capabilities WriterCapabilities to be added.
	 */
	protected final static void addCapabilities(WriterCapabilities capabilities) {
		AbstractDrawableWriter.capabilities.add(capabilities);
	}
}
