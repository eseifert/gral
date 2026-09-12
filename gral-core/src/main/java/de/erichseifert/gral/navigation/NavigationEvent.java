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
package de.erichseifert.gral.navigation;

/**
 * <p>Describes a single change made by a {@link Navigator}: which navigator
 * made it, and the value before and after. A {@link NavigationListener}
 * receives one of these for a change of the center
 * ({@code NavigationEvent<PointND<? extends Number>>}) and one for a change of
 * the zoom level ({@code NavigationEvent<Double>}).</p>
 *
 * <p>The source is what lets connected navigators avoid an endless loop: a
 * navigator ignores an event that it caused itself.</p>
 *
 * @param <T> Data type of the value that has been changed.
 */
public class NavigationEvent<T> {
	/** Object that has caused the change. */
	private final Navigator source;
	/** Value before the change. */
	private final T valueOld;
	/** Value after the change. */
	private final T valueNew;

	/**
	 * Initializes a new instance.
	 * @param source Navigator object that has caused the change.
	 * @param valueOld Value before the change
	 * @param valueNew Value after the change.
	 */
	public NavigationEvent(Navigator source, T valueOld, T valueNew) {
		this.source = source;
		this.valueOld = valueOld;
		this.valueNew = valueNew;
	}

	/**
	 * Returns the navigator that has caused the change.
	 * @return Navigator object that has caused the change.
	 */
	public Navigator getSource() {
		return source;
	}

	/**
	 * Returns the value before the change.
	 * @return Value before the change.
	 */
	public T getValueOld() {
		return valueOld;
	}

	/**
	 * Returns the value after the change.
	 * @return Value after the change.
	 */
	public T getValueNew() {
		return valueNew;
	}
}
