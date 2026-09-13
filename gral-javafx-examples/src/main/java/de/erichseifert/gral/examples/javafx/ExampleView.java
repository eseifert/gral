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
package de.erichseifert.gral.examples.javafx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import de.erichseifert.gral.examples.Example;
import de.erichseifert.gral.javafx.InteractiveCanvas;

/**
 * <p>The JavaFX view of an {@link Example}: the drawable of the example in an
 * {@link InteractiveCanvas}, so that it can be zoomed, panned, exported and
 * printed.</p>
 *
 * <p>It is the counterpart of {@code ExamplePanel} of the Swing examples, and
 * renders the same declarations in JavaFX terms: {@link Example.Adjustable}
 * becomes a {@code Slider} below the plot, and {@link Example.Animated} a
 * {@code Timeline} that runs while the example is the one on display.</p>
 */
public class ExampleView extends BorderPane {
	/** Example that is displayed. */
	private final Example example;

	/** Canvas showing the drawable of the example. */
	private final InteractiveCanvas canvas;

	/** Timer of an animated example, or {@code null}. */
	private final Timeline timeline;

	/**
	 * Creates a view showing the specified example.
	 * @param example Example to display.
	 */
	public ExampleView(Example example) {
		this.example = example;

		canvas = new InteractiveCanvas(example.getDrawable());
		canvas.setZoomable(example.isNavigable());
		canvas.setPannable(example.isNavigable());
		setCenter(canvas);

		if (example instanceof Example.Adjustable) {
			setBottom(createSlider((Example.Adjustable) example));
		}
		timeline = (example instanceof Example.Animated)
				? createTimeline((Example.Animated) example) : null;
	}

	/**
	 * Returns the example that is displayed by this view.
	 * @return The example.
	 */
	public Example getExample() {
		return example;
	}

	/**
	 * Starts or stops the refreshing of an animated example. A browser calls
	 * this when the example is shown or hidden, so that only the example on
	 * display does any work.
	 * @param running {@code true} to refresh the example, {@code false} to
	 *        leave it alone.
	 */
	public void setRunning(boolean running) {
		if (timeline == null) {
			return;
		}
		if (running) {
			timeline.play();
		} else {
			timeline.stop();
		}
	}

	/**
	 * Creates the slider that drives an adjustable example.
	 * @param adjustable Example to drive.
	 * @return A slider bound to the example.
	 */
	private Slider createSlider(Example.Adjustable adjustable) {
		var slider = new Slider(adjustable.getMinimum(), adjustable.getMaximum(),
				adjustable.getValue());
		slider.setPadding(new Insets(15.0, 15.0, 5.0, 15.0));
		slider.setMajorTickUnit(adjustable.getTickSpacing());
		slider.setMinorTickCount(adjustable.getTickSpacing() - 1);
		slider.setSnapToTicks(true);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			adjustable.setValue((int) Math.round(newValue.doubleValue()));
			canvas.redraw();
		});
		return slider;
	}

	/**
	 * Creates the timer that refreshes an animated example.
	 * @param animated Example to refresh.
	 * @return A timer that is not running yet.
	 */
	private Timeline createTimeline(Example.Animated animated) {
		var frame = new KeyFrame(Duration.millis(animated.getUpdateInterval()), event -> {
			animated.update();
			canvas.redraw();
		});
		var animation = new Timeline(frame);
		animation.setCycleCount(Animation.INDEFINITE);
		return animation;
	}
}
