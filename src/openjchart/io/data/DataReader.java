/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
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

package openjchart.io.data;

import java.io.IOException;
import java.text.ParseException;

import openjchart.data.DataSource;

/**
 * Interface that provides a function to retrieve a DataSource.
 */
public interface DataReader {
	/**
	 * Returns a DataSource, that was imported.
	 * @return DataSource
	 * @throws IOException
	 * @throws ParseException
	 */
	DataSource read() throws IOException, ParseException;
}
