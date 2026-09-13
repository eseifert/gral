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

import javafx.scene.canvas.Canvas;

import org.jfree.fx.FXGraphics2D;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;

/**
 * <p>A JavaFX {@code Canvas} that displays a single {@link Drawable}. This is
 * the bridge between GRAL and JavaFX: the canvas keeps the bounds of the
 * drawable in step with its own size and repaints it whenever that size
 * changes.</p>
 *
 * <pre>
 * StackPane root = new StackPane(new DrawableCanvas(plot));
 * stage.setScene(new Scene(root, 800.0, 600.0));
 * stage.show();
 * </pre>
 *
 * <p>Nothing of GRAL is JavaFX-specific: the drawable is painted through
 * <a href="https://github.com/jfree/fxgraphics2d">FXGraphics2D</a>, a
 * {@code Graphics2D} implementation that writes to a JavaFX canvas, so it is
 * the same rendering code that produces a bitmap or a PDF.</p>
 *
 * <p>Unlike a plain {@code Canvas} this one is resizable, so a layout pane
 * stretches it. The drawable is fixed for the lifetime of the canvas; to
 * display a different one, create a new canvas.</p>
 *
 * <p>Use {@link InteractiveCanvas} instead if zooming and panning are
 * wanted.</p>
 */
public class DrawableCanvas extends Canvas {
	/** Drawable that is displayed. */
	private final Drawable drawable;

	/** Graphics context that paints the drawable on this canvas. */
	private final FXGraphics2D graphics;

	/**
	 * Initializes a new canvas showing the specified drawable.
	 * @param drawable {@code Drawable} to be displayed.
	 */
	public DrawableCanvas(Drawable drawable) {
		this.drawable = drawable;
		graphics = new FXGraphics2D(getGraphicsContext2D());
		widthProperty().addListener(observable -> redraw());
		heightProperty().addListener(observable -> redraw());
	}

	/**
	 * Returns the {@code Drawable} instance that is displayed by this canvas.
	 * @return {@code Drawable} instance.
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	/**
	 * Paints the drawable at the current size of this canvas. This happens
	 * automatically when the canvas is resized; call it after the drawable has
	 * been changed in a way that the canvas cannot notice.
	 */
	public void redraw() {
		double width = getWidth();
		double height = getHeight();
		getGraphicsContext2D().clearRect(0.0, 0.0, width, height);
		if ((width <= 0.0) || (height <= 0.0)) {
			return;
		}
		getDrawable().setBounds(0.0, 0.0, width, height);
		getDrawable().draw(new DrawingContext(graphics));
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public void resize(double width, double height) {
		setWidth(width);
		setHeight(height);
	}

	@Override
	public double prefWidth(double height) {
		return getDrawable().getPreferredSize().getWidth();
	}

	@Override
	public double prefHeight(double width) {
		return getDrawable().getPreferredSize().getHeight();
	}

	@Override
	public double minWidth(double height) {
		return 0.0;
	}

	@Override
	public double minHeight(double width) {
		return 0.0;
	}

	@Override
	public double maxWidth(double height) {
		return Double.MAX_VALUE;
	}

	@Override
	public double maxHeight(double width) {
		return Double.MAX_VALUE;
	}
}
