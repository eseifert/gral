/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

import javax.swing.JFileChooser;

import de.erichseifert.gral.io.IOCapabilities;

/**
 * A file chooser implementation that can be for export purposes.
 */
public class ExportChooser extends JFileChooser {
	/**
	 * Creates a new instance and initializes it with an array of IOCapabilities.
	 * @param strict Determines whether this dialog allows only the file formats
	 *               specified in <code>capabilities</code>.
	 * @param capabilities Array of objects describing the file formats that
	 *                     are supported by this dialog.
	 */
	public ExportChooser(boolean strict, IOCapabilities... capabilities) {
		setAcceptAllFileFilterUsed(!strict);
		for (IOCapabilities c : capabilities) {
			addChoosableFileFilter(new DrawableWriterFilter(c));
		}
	}

}
