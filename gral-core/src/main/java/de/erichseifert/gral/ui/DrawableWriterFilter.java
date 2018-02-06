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
package de.erichseifert.gral.ui;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.filechooser.FileFilter;

import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.util.Messages;

/**
 * File filter that extracts files that can be read with a certain set of
 * {@link de.erichseifert.gral.io.IOCapabilities}.
 */
public class DrawableWriterFilter extends FileFilter {
	/** Capabilities that describe the data formats that can be processed by
	this filter. */
	private final IOCapabilities capabilities;

	/**
	 * Creates a new instance and initializes it with an
	 * {@link de.erichseifert.gral.io.IOCapabilities} object.
	 * @param capabilities writer capabilities.
	 */
	public DrawableWriterFilter(IOCapabilities capabilities) {
		this.capabilities = capabilities;
	}

	@Override
	public boolean accept(File f) {
		if (f == null) {
			return false;
		}
		if (f.isDirectory()) {
			return true;
		}
		String ext = getExtension(f).toLowerCase();
		for (String extension : capabilities.getExtensions()) {
			if (extension.equals(ext)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return MessageFormat.format(Messages.getString("IO.formatDescription"), //$NON-NLS-1$
				capabilities.getFormat(), capabilities.getName());
	}

	/**
	 * Returns the capabilities filtered by this instance.
	 * @return writer capabilities.
	 */
	public IOCapabilities getWriterCapabilities() {
		return capabilities;
	}

	private static String getExtension(File f) {
		String name = f.getName();
		int lastDot = name.lastIndexOf('.');
		if ((lastDot <= 0) || (lastDot == name.length() - 1)) {
			return ""; //$NON-NLS-1$
		}
		return name.substring(lastDot + 1);
	}
}
