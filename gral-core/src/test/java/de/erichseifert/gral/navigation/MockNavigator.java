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
package de.erichseifert.gral.navigation;

import de.erichseifert.gral.util.PointND;

final class MockNavigator extends AbstractNavigator {
	private final PointND<Double> center;
	private double zoom;

	private final PointND<Double> centerDefault;
	private double zoomDefault;

	public MockNavigator() {
		center = new PointND<>(0.0, 0.0);
		zoom = 1.0;

		centerDefault = new PointND<>(0.0, 0.0);
		setDefaultState();
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoomNew) {
		if (!isZoomable()) {
			return;
		}
		double zoomOld = zoom;
		if (zoomNew != zoomOld) {
			zoom = zoomNew;
			NavigationEvent<Double> event =
					new NavigationEvent<>(this, zoomOld, zoomNew);
			fireZoomChanged(event);
		}
	}

	public PointND<? extends Number> getCenter() {
		return new PointND<>(
			this.center.get(PointND.X),
			this.center.get(PointND.Y)
		);
	}

	public void setCenter(PointND<? extends Number> center) {
		if (!isPannable()) {
			return;
		}

		PointND<? extends Number> centerOld = getCenter();
		if (!center.equals(centerOld)) {
			this.center.setLocation(
				center.get(PointND.X).doubleValue(),
				center.get(PointND.Y).doubleValue()
			);
			PointND<? extends Number> centerNew = getCenter();

			NavigationEvent<PointND<? extends Number>> event =
					new NavigationEvent<>(this, centerOld, centerNew);
			fireCenterChanged(event);
		}
	}

	public void pan(PointND<? extends Number> deltas) {
		setCenter(new PointND<>(
				center.get(PointND.X) + deltas.get(PointND.X).doubleValue(),
				center.get(PointND.Y) + deltas.get(PointND.Y).doubleValue()
		));
	}

	public void setDefaultState() {
		centerDefault.setLocation(
			center.get(PointND.X),
			center.get(PointND.Y)
		);
		zoomDefault = zoom;
	}

	public void reset() {
		setCenter(new PointND<>(
				centerDefault.get(PointND.X),
				centerDefault.get(PointND.Y)
		));
		setZoom(zoomDefault);
	}
}
