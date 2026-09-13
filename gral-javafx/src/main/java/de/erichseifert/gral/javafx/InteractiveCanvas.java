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
package de.erichseifert.gral.javafx;

import java.awt.geom.Point2D;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigables;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.util.PointND;

/**
 * <p>A {@link DrawableCanvas} with the interaction expected of a plot on
 * screen. Using it is no different from using the plain canvas:</p>
 *
 * <pre>
 * StackPane root = new StackPane(new InteractiveCanvas(plot));
 * </pre>
 *
 * <p>What it adds:</p>
 * <ul>
 *   <li>dragging with the primary mouse button pans the view;</li>
 *   <li>scrolling and a double click zoom in and out.</li>
 * </ul>
 *
 * <p>Panning and zooming require the displayed drawable to be
 * {@link Navigable}, which the plots are; for anything else the canvas
 * silently behaves like a {@code DrawableCanvas}. Both can be switched off
 * with {@link #setPannable(boolean)} and {@link #setZoomable(boolean)}.</p>
 *
 * <p>Interaction is applied through the {@link Navigator} of the drawable, the
 * same one the Swing components use, so connecting the navigators of two views
 * makes them move together &mdash; across toolkits as well.</p>
 */
public class InteractiveCanvas extends DrawableCanvas {
	/**
	 * Vertical distance that JavaFX reports for one notch of a mouse wheel.
	 * A device that scrolls smoothly, like a touch pad, reports many smaller
	 * distances instead, which are added up until they amount to a notch.
	 */
	private static final double SCROLL_NOTCH = 40.0;

	/** Defines whether the displayed drawable can be zoomed. */
	private boolean zoomable;

	/** Defines whether the displayed drawable can be panned. */
	private boolean pannable;

	/** Navigable object that is currently being dragged, or {@code null}. */
	private Navigable dragged;

	/** Position of the previous drag event in canvas coordinates. */
	private Point2D dragPosition;

	/** Scroll distance that has not been turned into a zoom step yet. */
	private double scrollOffset;

	/**
	 * Initializes a new canvas showing the specified drawable. Zooming and
	 * panning are enabled by default.
	 * @param drawable {@code Drawable} to be displayed.
	 */
	public InteractiveCanvas(Drawable drawable) {
		super(drawable);
		zoomable = true;
		pannable = true;
		setOnMousePressed(this::handleMousePressed);
		setOnMouseDragged(this::handleMouseDragged);
		setOnMouseClicked(this::handleMouseClicked);
		setOnScroll(this::handleScroll);
	}

	/**
	 * Returns whether the displayed drawable can be zoomed.
	 * @return {@code true} if the drawable can be zoomed,
	 *         {@code false} otherwise.
	 */
	public boolean isZoomable() {
		return zoomable;
	}

	/**
	 * Sets whether the displayed drawable can be zoomed.
	 * @param zoomable {@code true} if the drawable should be zoomable,
	 *                 {@code false} otherwise.
	 */
	public void setZoomable(boolean zoomable) {
		this.zoomable = zoomable;
	}

	/**
	 * Returns whether the displayed drawable can be panned.
	 * @return {@code true} if the drawable can be panned,
	 *         {@code false} otherwise.
	 */
	public boolean isPannable() {
		return pannable;
	}

	/**
	 * Sets whether the displayed drawable can be panned.
	 * @param pannable {@code true} if the drawable should be pannable,
	 *                 {@code false} otherwise.
	 */
	public void setPannable(boolean pannable) {
		this.pannable = pannable;
	}

	/**
	 * Remembers which navigable object a drag started on.
	 * @param event Mouse event that started the gesture.
	 */
	private void handleMousePressed(MouseEvent event) {
		Point2D position = getPosition(event);
		dragged = Navigables.getNavigableAt(getDrawable(), position);
		dragPosition = position;
	}

	/**
	 * Moves the view of the navigable object the drag started on.
	 * @param event Mouse event that continued the gesture.
	 */
	private void handleMouseDragged(MouseEvent event) {
		if (!isPannable() || (dragged == null)) {
			return;
		}

		// Calculate the distance that the current view was dragged
		// (screen units)
		Point2D position = getPosition(event);
		int dx = (int) Math.round(position.getX() - dragPosition.getX());
		int dy = (int) Math.round(position.getY() - dragPosition.getY());
		dragPosition = position;

		if ((dx == 0) && (dy == 0)) {
			return;
		}

		dragged.getNavigator().pan(new PointND<>(dx, dy));
		redraw();
	}

	/**
	 * Zooms in on a double click.
	 * @param event Mouse event of the click.
	 */
	private void handleMouseClicked(MouseEvent event) {
		if ((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2)) {
			zoom(getPosition(event), 1);
		}
	}

	/**
	 * Zooms in or out, depending on the direction of the scroll gesture. One
	 * notch of a mouse wheel is one zoom step, as it is in Swing.
	 * @param event Scroll event of the gesture.
	 */
	private void handleScroll(ScrollEvent event) {
		double delta = event.getDeltaY();
		/*
		 * A notch of a mouse wheel arrives as two events, the first of which
		 * reports no distance at all. Reading that one as a direction would
		 * zoom in twice per notch upwards, and in and straight out again
		 * downwards.
		 */
		if (delta == 0.0) {
			return;
		}
		// What is left over from a gesture in the other direction is stale.
		if (Math.signum(delta) != Math.signum(scrollOffset)) {
			scrollOffset = 0.0;
		}
		scrollOffset += delta;

		int notches = (int) (scrollOffset/SCROLL_NOTCH);
		if (notches == 0) {
			return;
		}
		scrollOffset -= notches*SCROLL_NOTCH;
		zoom(getPosition(event), notches);
	}

	/**
	 * Zooms the navigable object at the specified point in (positive values)
	 * or out (negative values).
	 * @param point The location where the zoom was triggered.
	 * @param times Number of times the navigable object will be zoomed.
	 *        Positive values zoom in, negative values zoom out.
	 */
	private void zoom(Point2D point, int times) {
		if (!isZoomable()) {
			return;
		}

		Navigable navigable = Navigables.getNavigableAt(getDrawable(), point);
		if (navigable == null) {
			return;
		}

		Navigator navigator = navigable.getNavigator();
		for (int i = 0; i < Math.abs(times); i++) {
			if (times >= 0) {
				navigator.zoomIn();
			} else {
				navigator.zoomOut();
			}
		}

		redraw();
	}

	/**
	 * Returns the position of a mouse event in the coordinates of the
	 * displayed drawable, which are those of this canvas.
	 * @param event Mouse event to take the position from.
	 * @return Position of the event.
	 */
	private static Point2D getPosition(MouseEvent event) {
		return new Point2D.Double(event.getX(), event.getY());
	}

	/**
	 * Returns the position of a scroll event in the coordinates of the
	 * displayed drawable, which are those of this canvas.
	 * @param event Scroll event to take the position from.
	 * @return Position of the event.
	 */
	private static Point2D getPosition(ScrollEvent event) {
		return new Point2D.Double(event.getX(), event.getY());
	}
}
