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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
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
 *   <li>scrolling and a double click zoom in and out;</li>
 *   <li>a context menu with <i>zoom in</i>, <i>zoom out</i>, <i>reset
 *   view</i>, <i>export image</i> and <i>print</i>;</li>
 *   <li>exporting offers every format registered with
 *   {@link DrawableWriterFactory}, through {@link ExportDialog}; the exported
 *   size is independent of the size on screen.</li>
 * </ul>
 *
 * <p>Panning and zooming require the displayed drawable to be
 * {@link Navigable}, which the plots are; for anything else the canvas
 * silently behaves like a {@code DrawableCanvas}. Both can be switched off
 * with {@link #setPannable(boolean)} and {@link #setZoomable(boolean)}, and
 * the context menu with {@link #setContextMenuEnabled(boolean)}.</p>
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

	/** Constant that can be used to convert from millimeters to points
	(1/72 inch). */
	private static final double MM_TO_PT = 72.0/25.4;
	/** Constant that defines how many millimeters a pixel will be. */
	private static final double MM_PER_PX = 0.2*MM_TO_PT;

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

	/** Defines whether a context menu is shown. */
	private boolean contextMenuEnabled;

	/** Cache for the context menu. */
	private ContextMenu contextMenu;

	/** Position the context menu was opened at. */
	private Point2D contextMenuPosition;

	/**
	 * Initializes a new canvas showing the specified drawable. Zooming and
	 * panning are enabled by default.
	 * @param drawable {@code Drawable} to be displayed.
	 */
	public InteractiveCanvas(Drawable drawable) {
		super(drawable);
		zoomable = true;
		pannable = true;
		contextMenuEnabled = true;
		setOnMousePressed(this::handleMousePressed);
		setOnMouseDragged(this::handleMouseDragged);
		setOnMouseClicked(this::handleMouseClicked);
		setOnScroll(this::handleScroll);
		setOnContextMenuRequested(this::handleContextMenuRequested);
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
	 * Returns whether a context menu is shown when the user asks for one.
	 * @return {@code true} when a context menu will be shown,
	 *         otherwise {@code false}.
	 */
	public boolean isContextMenuEnabled() {
		return contextMenuEnabled;
	}

	/**
	 * Sets whether a context menu is shown when the user asks for one.
	 * @param contextMenuEnabled {@code true} when a context menu should be
	 *        shown, otherwise {@code false}.
	 */
	public void setContextMenuEnabled(boolean contextMenuEnabled) {
		this.contextMenuEnabled = contextMenuEnabled;
	}

	/**
	 * Returns the context menu of this canvas, creating it on first use. The
	 * menu is cached, so a subclass that wants different entries overrides
	 * this method and builds its own.
	 * @return The context menu, or {@code null} if none should be shown.
	 */
	protected ContextMenu getContextMenu() {
		if (contextMenu == null) {
			var zoomIn = new MenuItem(FxMessages.getString("InteractiveCanvas.zoomIn")); //$NON-NLS-1$
			zoomIn.setOnAction(event -> zoom(getMenuPosition(), 1));
			var zoomOut = new MenuItem(FxMessages.getString("InteractiveCanvas.zoomOut")); //$NON-NLS-1$
			zoomOut.setOnAction(event -> zoom(getMenuPosition(), -1));
			var resetView = new MenuItem(FxMessages.getString("InteractiveCanvas.resetView")); //$NON-NLS-1$
			resetView.setOnAction(event -> resetZoom(getMenuPosition()));
			var exportImage = new MenuItem(FxMessages.getString("InteractiveCanvas.exportImage")); //$NON-NLS-1$
			exportImage.setOnAction(event -> exportImage());
			var print = new MenuItem(FxMessages.getString("InteractiveCanvas.print")); //$NON-NLS-1$
			print.setOnAction(event -> print());

			zoomIn.setDisable(!isZoomable());
			zoomOut.setDisable(!isZoomable());
			resetView.setDisable(!isZoomable() && !isPannable());

			contextMenu = new ContextMenu(zoomIn, zoomOut, resetView,
					new SeparatorMenuItem(), exportImage, print);
		}
		return contextMenu;
	}

	/**
	 * Returns the position the context menu was opened at, or the middle of
	 * the canvas when it has not been opened by hand.
	 * @return Position an entry of the menu acts on.
	 */
	private Point2D getMenuPosition() {
		if (contextMenuPosition != null) {
			return contextMenuPosition;
		}
		return new Point2D.Double(getWidth()/2.0, getHeight()/2.0);
	}

	/**
	 * Opens the context menu where the user asked for it.
	 * @param event Event that requested the menu.
	 */
	private void handleContextMenuRequested(ContextMenuEvent event) {
		if (!isContextMenuEnabled()) {
			return;
		}
		ContextMenu menu = getContextMenu();
		if (menu == null) {
			return;
		}
		contextMenuPosition = new Point2D.Double(event.getX(), event.getY());
		menu.show(this, event.getScreenX(), event.getScreenY());
	}

	/**
	 * Resets the view of the navigable object at the specified point.
	 * @param point The location where the reset was triggered.
	 */
	private void resetZoom(Point2D point) {
		if (!isZoomable()) {
			return;
		}

		Navigable navigable = Navigables.getNavigableAt(getDrawable(), point);
		if (navigable == null) {
			return;
		}

		navigable.getNavigator().reset();
		redraw();
	}

	/**
	 * Asks for a file and for the bounds of the document, and writes the
	 * drawable to it. The format is the one of the chosen file filter.
	 */
	private void exportImage() {
		Window owner = (getScene() != null) ? getScene().getWindow() : null;

		var chooser = new FileChooser();
		chooser.setTitle(FxMessages.getString("InteractiveCanvas.exportImageTitle")); //$NON-NLS-1$
		Map<FileChooser.ExtensionFilter, IOCapabilities> formats = createFilters();
		chooser.getExtensionFilters().addAll(formats.keySet());

		File file = chooser.showSaveDialog(owner);
		if (file == null) {
			return;
		}
		IOCapabilities capabilities = formats.get(chooser.getSelectedExtensionFilter());
		if (capabilities == null) {
			return;
		}

		var dialog = new ExportDialog(owner, getDrawable());
		dialog.getDocumentBounds().ifPresent(
				bounds -> export(capabilities.getMimeType(), file, bounds));
	}

	/**
	 * Returns one file filter per format that can be written, in the order the
	 * factory lists them.
	 * @return The filters, each mapped to the format it stands for.
	 */
	private static Map<FileChooser.ExtensionFilter, IOCapabilities> createFilters() {
		var formats = new LinkedHashMap<FileChooser.ExtensionFilter, IOCapabilities>();
		for (IOCapabilities capabilities : DrawableWriterFactory.getInstance().getCapabilities()) {
			String description = MessageFormat.format(
					FxMessages.getString("IO.formatDescription"), //$NON-NLS-1$
					capabilities.getFormat(), capabilities.getName());
			List<String> patterns = new ArrayList<>();
			for (String extension : capabilities.getExtensions()) {
				patterns.add("*." + extension); //$NON-NLS-1$
			}
			formats.put(new FileChooser.ExtensionFilter(description, patterns), capabilities);
		}
		return formats;
	}

	/**
	 * Writes the displayed drawable to a file.
	 * @param mimeType File format as MIME type string.
	 * @param file File to export to.
	 * @param documentBounds Document boundary rectangle.
	 */
	private void export(String mimeType, File file, Rectangle2D documentBounds) {
		try (OutputStream destination = new FileOutputStream(file)) {
			DrawableWriter writer = DrawableWriterFactory.getInstance().get(mimeType);
			writer.write(getDrawable(), destination,
				documentBounds.getX(), documentBounds.getY(),
				documentBounds.getWidth(), documentBounds.getHeight());
		} catch (IOException | RuntimeException e) {
			showError(FxMessages.getString("InteractiveCanvas.exportFailed"), e); //$NON-NLS-1$
		}
	}

	/**
	 * Prints the displayed drawable, asking for a printer first. Printing goes
	 * through {@code java.awt.print}, so that the result is drawn rather than
	 * photographed from the screen, and it runs on a thread of its own,
	 * because the print dialog would otherwise block the display.
	 */
	private void print() {
		Drawable drawable = getDrawable();
		var printer = PrinterJob.getPrinterJob();
		printer.setPrintable(new DrawablePrintable(drawable));
		var thread = new Thread(() -> {
			try {
				if (printer.printDialog()) {
					printer.print();
				}
			} catch (PrinterException | RuntimeException e) {
				Platform.runLater(() -> showError(
						FxMessages.getString("InteractiveCanvas.printFailed"), e)); //$NON-NLS-1$
			}
		}, "GRAL printing"); //$NON-NLS-1$
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Tells the user that an action failed.
	 * @param message Text describing what did not work.
	 * @param cause Exception that was caught.
	 */
	private static void showError(String message, Throwable cause) {
		var alert = new Alert(Alert.AlertType.ERROR, message);
		alert.setHeaderText(FxMessages.getString("InteractiveCanvas.error")); //$NON-NLS-1$
		alert.setContentText(message + "\n" + cause.getLocalizedMessage()); //$NON-NLS-1$
		alert.show();
	}

	/**
	 * Draws a {@link Drawable} onto a printed page. The drawable is laid out
	 * to the printable area for the duration of the call and restored
	 * afterwards, so printing does not disturb what is on screen.
	 */
	private static final class DrawablePrintable implements Printable {
		/** Drawable that is printed. */
		private final Drawable drawable;

		/**
		 * Creates a printable for the specified drawable.
		 * @param drawable Drawable to print.
		 */
		DrawablePrintable(Drawable drawable) {
			this.drawable = drawable;
		}

		@Override
		public int print(java.awt.Graphics graphics, PageFormat pageFormat, int pageIndex) {
			if (pageIndex > 0) {
				return Printable.NO_SUCH_PAGE;
			}

			var graphics2d = (Graphics2D) graphics;
			AffineTransform transformOld = graphics2d.getTransform();
			graphics2d.scale(MM_PER_PX, MM_PER_PX);

			Rectangle2D boundsOld = drawable.getBounds();
			var pageBounds = new Rectangle2D.Double(
				pageFormat.getImageableX()/MM_PER_PX,
				pageFormat.getImageableY()/MM_PER_PX,
				pageFormat.getImageableWidth()/MM_PER_PX,
				pageFormat.getImageableHeight()/MM_PER_PX
			);

			drawable.setBounds(pageBounds);
			try {
				drawable.draw(new DrawingContext(graphics2d));
			} finally {
				drawable.setBounds(boundsOld);
			}
			graphics2d.setTransform(transformOld);
			return Printable.PAGE_EXISTS;
		}
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
