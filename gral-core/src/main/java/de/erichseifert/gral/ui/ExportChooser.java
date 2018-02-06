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

import java.util.List;

import javax.swing.JFileChooser;

import de.erichseifert.gral.io.IOCapabilities;

/**
 * A file chooser implementation that can be for export purposes.
 */
public class ExportChooser extends JFileChooser {
	/** Version id for serialization. */
	private static final long serialVersionUID = -7885235526259131711L;

	/**
	 * Creates a new instance and initializes it with an array of
	 * {@link de.erichseifert.gral.io.IOCapabilities}.
	 * @param strict Determines whether this dialog allows only the file formats
	 *               specified in {@code capabilities}.
	 * @param capabilities List of objects describing the file formats that
	 *                     are supported by this dialog.
	 */
	public ExportChooser(boolean strict, List<IOCapabilities> capabilities) {
		setAcceptAllFileFilterUsed(!strict);
		for (IOCapabilities c : capabilities) {
			addChoosableFileFilter(new DrawableWriterFilter(c));
		}
	}

}
