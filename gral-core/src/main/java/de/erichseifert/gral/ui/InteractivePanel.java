/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.NavigationListener;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.PlotNavigator;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.Messages;


/**
 * A panel implementation that displays a {@code Drawable} instance as a
 * rich Swing component. Special handling is applied to {@code XYPlot}
 * instances.
 * @see de.erichseifert.gral.plots.XYPlot
 */
public class InteractivePanel extends DrawablePanel
		implements Printable, NavigationListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1L;

	/** Constants which determine the direction of zoom and pan actions. */
	public static enum NavigationDirection {
		/** Value for zooming and panning horizontally. */
		HORIZONTAL,
		/** Value for zooming and panning vertically. */
		VERTICAL,
		/** Value for zooming and panning in all direction. */
		ARBITRARY
	}

	// FIXME Find better method to adjust resolution
	/** Constant that can be used to convert from millimeters to points
	(1/72 inch). */
	private static final double MM_TO_PT = 72.0/25.4;
	/** Constant that defines how many millimeters a pixel will be. */
	private static final double MM_PER_PX = 0.2*MM_TO_PT;
	/** Job for printing the current panel. */
	private final PrinterJob printerJob;

	/** Value that is necessary before panning is triggered. */
	private static final int MIN_DRAG = 0;
	/** Factor that is used for zoom in and zoom out actions. */
	private static final double ZOOM_FACTOR = 0.8;

	/** Defines whether the panel can be zoomed. */
	private boolean zoomable;
	/** Defines whether the panel can be panned. */
	private boolean pannable;

	/** Map that stored actions by names like "zoomIn", "zoomOut",
	"resetView", "exportImage", or "print". */
	protected final ActionMap actions;

	/** Cache for the popup menu. */
	private JPopupMenu popupMenu;
	private boolean popupMenuEnabled;

	/** Chooser for image export. */
	private final JFileChooser exportImageChooser;

	/** Navigator that controls the plot when zooming or panning. */
	private PlotNavigator navigator;
	/** Object to be used as listener for zooming actions. */
	private MouseZoomListener zoomListener;
	/** Object to be used as listener for panning actions. */
	private NavigationMoveListener panListener;

	/** Workaround to find out when the panel has been drawn the first time. */
	private boolean stateInitialized;

	/**
	 * Listener class for zooming actions.
	 */
	private final class MouseZoomListener extends MouseAdapter
			implements MouseWheelListener, Serializable {
		/** Version id for serialization. */
		private static final long serialVersionUID = 1L;
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			zoom(-e.getWheelRotation());
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) &&
					(e.getClickCount() == 2))  {
				zoom(1);
			}
		}
	}

	/**
	 * Creates a new panel instance and initializes it with a
	 * drawable component.
	 * @param drawable Drawable component.
	 */
	@SuppressWarnings("serial")
	public InteractivePanel(Drawable drawable) {
		super(drawable);

		printerJob = PrinterJob.getPrinterJob();
		printerJob.setPrintable(this);

		List<IOCapabilities> exportFormats = DrawableWriterFactory.getInstance()
			.getCapabilities();
		exportImageChooser = new ExportChooser(true, exportFormats);
		exportImageChooser.setDialogTitle(Messages.getString(
				"InteractivePanel.exportImageTitle")); //$NON-NLS-1$

		actions = new ActionMap();
		actions.put("zoomIn", new AbstractAction(Messages.getString( //$NON-NLS-1$
			"InteractivePanel.zoomIn")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				zoom(1);
			}
		});
		actions.put("zoomOut", new AbstractAction(Messages.getString( //$NON-NLS-1$
			"InteractivePanel.zoomOut")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				zoom(-1);
			}
		});
		actions.put("resetView", new AbstractAction(Messages.getString( //$NON-NLS-1$
				"InteractivePanel.resetView")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				if (navigator != null) {
					navigator.reset();
					repaint();
				}
			}
		});
		actions.put("exportImage", new AbstractAction(Messages.getString( //$NON-NLS-1$
				"InteractivePanel.exportImage")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				int ret = exportImageChooser.showSaveDialog(
						InteractivePanel.this);
				// Clear artifacts of the file chooser
				repaint();
				// If the user aborted we can stop
				if (ret != JFileChooser.APPROVE_OPTION) {
					return;
				}
				// If the user didn't select a file we can stop
				File file = exportImageChooser.getSelectedFile();
				if (file == null) {
					return;
				}
				// If the selected an existing file we ask for permission
				// to overwrite it
				else if (file.exists()) {
					int retOverwrite = JOptionPane.showConfirmDialog(
						InteractivePanel.this,
						Messages.getString("InteractivePanel.exportExistsWarning"), //$NON-NLS-1$
						Messages.getString("InteractivePanel.warning"), //$NON-NLS-1$
						JOptionPane.YES_NO_OPTION
					);
					// Clear artifacts of the confirm dialog
					repaint();
					if (retOverwrite == JOptionPane.NO_OPTION) {
						return;
					}
				}

				// Export current view to the selected file
				Drawable d = getDrawable();
				ExportDialog ed = new ExportDialog(InteractivePanel.this, d);
				ed.setVisible(true);
				if (!ed.getUserAction().equals(
						ExportDialog.UserAction.APPROVE)) {
					return;
				}
				DrawableWriterFilter filter = (DrawableWriterFilter)
					exportImageChooser.getFileFilter();
				export(d, filter.getWriterCapabilities().getMimeType(),
					file, ed.getDocumentBounds());
			}
		});
		actions.put("print", new AbstractAction(Messages.getString( //$NON-NLS-1$
				"InteractivePanel.print")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				if (printerJob.printDialog()) {
					try {
						printerJob.print();
					} catch (PrinterException ex) {
						// TODO Show error dialog
						ex.printStackTrace();
					}
				}
			}
		});

		popupMenuEnabled = true;
		addMouseListener(new PopupListener());

		if (getDrawable() instanceof Plot) {
			Plot plot = (Plot) getDrawable();
			navigator = new PlotNavigator(plot);
			// Register a new handler to zoom the map with the mouse wheel
			setZoomable(true);

			if (getDrawable() instanceof XYPlot) {
				setPannable(true);
			}
		}
	}

	/**
	 * Method that returns the popup menu for a given mouse event. It will be
	 * called on each popup event if the menu is enabled. If the menu is static
	 * caching can be used to prevent unnecessary generation of menu objects.
	 * @param e Mouse event that triggered the popup menu.
	 * @return A popup menu instance, or {@code null} if no popup menu should be shown.
	 * @see #isPopupMenuEnabled()
	 * @see #setPopupMenuEnabled(boolean)
	 */
	protected JPopupMenu getPopupMenu(MouseEvent e) {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(actions.get("zoomIn")); //$NON-NLS-1$
			popupMenu.add(actions.get("zoomOut")); //$NON-NLS-1$
			popupMenu.add(actions.get("resetView")); //$NON-NLS-1$
			popupMenu.addSeparator();
			popupMenu.add(actions.get("exportImage")); //$NON-NLS-1$
			popupMenu.add(actions.get("print")); //$NON-NLS-1$
		}
		return popupMenu;
	}

	/**
	 * Returns whether a popup menu will be shown by this panel when the user
	 * takes the appropriate action. The necessary action depends on the
	 * operating system of the user.
	 * @return {@code true} when a popup menu will be shown,
	 *         otherwise {@code false}.
	 */
	public boolean isPopupMenuEnabled() {
		return popupMenuEnabled;
	}

	/**
	 * Sets whether a popup menu will be shown by this panel when the user
	 * takes the appropriate action. The necessary action depends on the
	 * operating system of the user.
	 * @param popupMenuEnabled {@code true} when a popup menu should be
	 *        shown, otherwise {@code false}.
	 */
	public void setPopupMenuEnabled(boolean popupMenuEnabled) {
		this.popupMenuEnabled = popupMenuEnabled;
	}

	/**
	 * Zooms the plot in (positive values) or out (negative values).
	 * @param times Number of times the plot plot will be zoomed.
	 *        Positive values zoom in, negative values zoom out.
	 */
	private void zoom(double times) {
		if (!isZoomable()) {
			return;
		}
		double zoomNew = navigator.getZoom()*Math.pow(ZOOM_FACTOR, times);
		navigator.setZoom(zoomNew);
		repaint();
	}

	/**
	 * Method that exports the current view to a file using a specified file type.
	 * @param d Drawable that will be exported.
	 * @param mimeType File format as MIME type string.
	 * @param f File to export to.
	 * @param documentBounds Document boundary rectangle
	 */
	private void export(Drawable d, String mimeType, File f,
			Rectangle2D documentBounds) {
		FileOutputStream destination;
		try {
			destination = new FileOutputStream(f);
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return;
		}
		DrawableWriter w = DrawableWriterFactory.getInstance().get(mimeType);
		try {
			w.write(d, destination,
					documentBounds.getX(), documentBounds.getY(),
					documentBounds.getWidth(), documentBounds.getHeight());
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			try {
				destination.close();
			} catch (IOException ex2) {
				// TODO Auto-generated catch block
				ex2.printStackTrace();
			}
		}
	}

	/**
	 * Class that is responsible for showing the popup menu.
	 */
	private class PopupListener extends MouseAdapter {
	    @Override
		public void mousePressed(MouseEvent e) {
	        showPopup(e);
	    }

	    @Override
		public void mouseReleased(MouseEvent e) {
	        showPopup(e);
	    }

	    private void showPopup(MouseEvent e) {
	        if (!isPopupMenuEnabled() || !e.isPopupTrigger()) {
	        	return;
	        }
        	JPopupMenu menu = getPopupMenu(e);
        	if (menu == null) {
        		return;
        	}
    		menu.show(e.getComponent(), e.getX(), e.getY());
	    }
	}

	/**
	 * Class that handles mouse moves for navigation.
	 */
	private static class NavigationMoveListener extends MouseAdapter {
		/** Navigator that will be changed by this class. */
		private final PlotNavigator navigator;
		/** Plot that will be changed by this class. */
		private final Plot plot;
		/** Previously clicked point (or {@code null}). */
		private Point posPrev;

		/**
		 * Creates a new listener and initializes it with a plot.
		 * @param navigator PlotNavigator that should be adjusted.
		 */
		public NavigationMoveListener(PlotNavigator navigator) {
			this.navigator = navigator;
			this.plot = navigator.getPlot();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			posPrev = e.getPoint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// Calculate distance that the current view was dragged
			// (screen units)
			Point pos = e.getPoint();
			int dx = -pos.x + posPrev.x;
			int dy =  pos.y - posPrev.y;
			posPrev = pos;

			if (Math.abs(dx) > MIN_DRAG || Math.abs(dy) > MIN_DRAG) {
				AxisRenderer axisXRenderer =
					plot.getAxisRenderer(XYPlot.AXIS_X);
				if (axisXRenderer != null) {
					boolean swapped = axisXRenderer.<Boolean>getSetting(
							AxisRenderer.SHAPE_DIRECTION_SWAPPED);
					if (swapped) {
						dx = -dx;
					}
					Axis axisX = plot.getAxis(XYPlot.AXIS_X);
					// Fetch current center on screen
					double centerX = axisXRenderer.worldToView(
							axisX, navigator.getCenter(XYPlot.AXIS_X), true);
					// Move center and convert it to axis coordinates
					Number centerXNew = axisXRenderer.viewToWorld(
							axisX, centerX + dx, true);
					// Change axis (world units)
					navigator.setCenter(XYPlot.AXIS_X, centerXNew);
				}
				AxisRenderer axisX2Renderer =
					plot.getAxisRenderer(XYPlot.AXIS_X2);
				if (axisX2Renderer != null) {
					boolean swapped = axisX2Renderer.<Boolean>getSetting(
							AxisRenderer.SHAPE_DIRECTION_SWAPPED);
					if (swapped) {
						dx = -dx;
					}
					Axis axisX2 = plot.getAxis(XYPlot.AXIS_X2);
					// Fetch current center on screen
					double centerX2 = axisX2Renderer.worldToView(
							axisX2, navigator.getCenter(XYPlot.AXIS_X2), true);
					// Move center and convert it to axis coordinates
					Number centerX2New = axisX2Renderer.viewToWorld(
							axisX2, centerX2 + dx, true);
					// Change axis (world units)
					navigator.setCenter(XYPlot.AXIS_X2, centerX2New);
				}

				AxisRenderer axisYRenderer =
					plot.getAxisRenderer(XYPlot.AXIS_Y);
				if (axisYRenderer != null) {
					boolean swapped = axisYRenderer.<Boolean>getSetting(
							AxisRenderer.SHAPE_DIRECTION_SWAPPED);
					if (swapped) {
						dy = -dy;
					}
					Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
					// Fetch current center on screen
					double centerY = axisYRenderer.worldToView(
						axisY, navigator.getCenter(XYPlot.AXIS_Y), true);
					// Move center and convert it to axis coordinates
					Number centerYNew = axisYRenderer.viewToWorld(
						axisY, centerY + dy, true);
					// Change axis (world units)
					navigator.setCenter(XYPlot.AXIS_Y, centerYNew);
				}
				AxisRenderer axisY2Renderer =
					plot.getAxisRenderer(XYPlot.AXIS_Y2);
				if (axisY2Renderer != null) {
					boolean swapped = axisY2Renderer.<Boolean>getSetting(
							AxisRenderer.SHAPE_DIRECTION_SWAPPED);
					if (swapped) {
						dy = -dy;
					}
					Axis axisY2 = plot.getAxis(XYPlot.AXIS_Y2);
					// Fetch current center on screen
					double centerY2 = axisY2Renderer.worldToView(
						axisY2, navigator.getCenter(XYPlot.AXIS_Y2), true);
					// Move center and convert it to axis coordinates
					Number centerY2New = axisY2Renderer.viewToWorld(
						axisY2, centerY2 + dy, true);
					// Change axis (world units)
					navigator.setCenter(XYPlot.AXIS_Y2, centerY2New);
				}

				// Refresh display
				if (e.getSource() instanceof Component) {
					((Component) e.getSource()).repaint();
				}
			}
		}
	}

	/**
     * Prints the page at the specified index into the specified
     * {@link Graphics} context in the specified format.
     * @param g the context into which the page is drawn
     * @param pageFormat the size and orientation of the page being drawn
     * @param pageIndex the zero based index of the page to be drawn
     * @return PAGE_EXISTS if the page is rendered successfully
     *         or NO_SUCH_PAGE if {@code pageIndex} specifies a
     *	       non-existent page.
     * @exception java.awt.print.PrinterException
     *         thrown when the print job is terminated.
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }

		Graphics2D graphics = (Graphics2D) g;
		AffineTransform txOld = graphics.getTransform();
		graphics.scale(MM_PER_PX, MM_PER_PX);

		Rectangle2D boundsOld = getDrawable().getBounds();
		Rectangle2D pageBounds = new Rectangle2D.Double(
			pageFormat.getImageableX()/MM_PER_PX,
			pageFormat.getImageableY()/MM_PER_PX,
			pageFormat.getImageableWidth()/MM_PER_PX,
			pageFormat.getImageableHeight()/MM_PER_PX
		);

		// Set size
		// TODO Keep Drawable's aspect ratio when scaling
		getDrawable().setBounds(pageBounds);
		// TODO Assure to temporarily turn off anti-aliasing before printing
		try {
			getDrawable().draw(new DrawingContext(graphics));
		} finally {
			getDrawable().setBounds(boundsOld);
		}
		graphics.setTransform(txOld);
		return Printable.PAGE_EXISTS;
	}

	/**
	 * Couples the actions of the current and the specified panel. All actions
	 * applied to this panel will be also applied to the specified panel and
	 * vice versa.
	 * @param panel Panel to be bound to this instance.
	 */
	public void connect(final InteractivePanel panel) {
		if (panel != null && panel != this) {
			this.navigator.addNavigationListener(panel);
			panel.navigator.addNavigationListener(this);
		}
	}

	/**
	 * Decouples the actions of the current and the specified panel. All actions
	 * will be applied separately to each panel then.
	 * @param panel Panel to be bound to this instance.
	 */
	public void disconnect(InteractivePanel panel) {
		if (panel != null && panel != this) {
			this.navigator.removeNavigationListener(panel);
			panel.navigator.removeNavigationListener(this);
		}
	}

	/**
	 * A method that gets called when the center of an axis in the
	 * {@code PlotNavigator} has changed.
	 * @param source Object that has caused the change
	 * @param axisName Name of the axis that has changed
	 * @param centerOld Previous value of axis center
	 * @param centerNew New value of axis center
	 */
	public void centerChanged(PlotNavigator source, String axisName,
			Number centerOld, Number centerNew) {
		navigator.setCenter(axisName, centerNew);
		repaint();
	}

	/**
	 * A method that gets called when the zoom level of an axis in the
	 * {@code PlotNavigator} has changed.
	 * @param source Object that has caused the change
	 * @param axisName Name of the axis that has changed
	 * @param zoomOld Previous zoom level of the axis
	 * @param zoomNew New zoom level of the axis
	 */
	public void zoomChanged(PlotNavigator source, String axisName,
			double zoomOld, double zoomNew) {
		navigator.setZoom(zoomNew);
		repaint();
	}

	/**
	 * Returns whether the plot area in the panel can be zoomed.
	 * @return {@code true} if the plot can be zoomed,
	 *         {@code false} otherwise.
	 */
	public boolean isZoomable() {
		return zoomable;
	}

	/**
	 * Sets whether the plot area in the panel can be zoomed.
	 * @param zoomable {@code true} if the plot should be zoomable,
	 *                 {@code false} otherwise.
	 */
	public void setZoomable(boolean zoomable) {
		if (!(getDrawable() instanceof Plot) || (this.zoomable == zoomable)) {
			return;
		}

		this.zoomable = zoomable;

		if (zoomListener != null) {
			removeMouseWheelListener(zoomListener);
			removeMouseListener(zoomListener);
			zoomListener = null;
		}

		if (zoomable) {
			zoomListener = new MouseZoomListener();
			addMouseListener(zoomListener);
			addMouseWheelListener(zoomListener);
		}

		actions.get("zoomIn").setEnabled(isZoomable()); //$NON-NLS-1$
		actions.get("zoomOut").setEnabled(isZoomable()); //$NON-NLS-1$
		actions.get("resetView").setEnabled(isZoomable() && isPannable()); //$NON-NLS-1$
	}

	/**
	 * Returns whether the plot area in the panel can be panned.
	 * @return {@code true} if the plot can be panned,
	 *         {@code false} otherwise.
	 */
	public boolean isPannable() {
		return pannable;
	}

	/**
	 * Sets whether the plot area in the panel can be panned.
	 * @param pannable {@code true} if the plot should be pannable,
	 *                 {@code false} otherwise.
	 */
	public void setPannable(boolean pannable) {
		if (!(getDrawable() instanceof XYPlot) || (this.pannable == pannable)) {
			return;
		}

		this.pannable = pannable;

		if (panListener != null) {
			removeMouseMotionListener(panListener);
			removeMouseListener(panListener);
			panListener = null;
		}
		if (pannable) {
			// Register a new handler to move the map by dragging
			// This requires that an x- and a y-axis do exist in the plot
			panListener = new NavigationMoveListener(navigator);
			addMouseListener(panListener);
			addMouseMotionListener(panListener);
		}

		actions.get("resetView").setEnabled(isZoomable() && isPannable()); //$NON-NLS-1$
	}

	/**
	 * Returns the direction in which can be navigated. {@code null} will
	 * be returned if the displayed plot does not support navigation.
	 * @return the direction in which can be navigated, or {@code null} if
	 *         the displayed plot does not support navigation.
	 */
	public NavigationDirection getNavigateDirection() {
		if (panListener != null) {
			boolean isHorizontal = navigator.getAxes().contains(XYPlot.AXIS_X);
			boolean isVertical = navigator.getAxes().contains(XYPlot.AXIS_Y);
			if (isHorizontal && isVertical) {
				return NavigationDirection.ARBITRARY;
			} else if (isHorizontal) {
				return NavigationDirection.HORIZONTAL;
			} else {
				return NavigationDirection.VERTICAL;
			}
		}
		return null;
	}

	/**
	 * Sets the direction in which can be navigated.
	 * @param direction The direction in which can be navigated.
	 */
	public void setNavigateDirection(NavigationDirection direction) {
		if (panListener != null) {
			if (NavigationDirection.HORIZONTAL.equals(direction)) {
				navigator.setAxes(XYPlot.AXIS_X);
			} else if (NavigationDirection.VERTICAL.equals(direction)) {
				navigator.setAxes(XYPlot.AXIS_Y);
			} else {
				Collection<String> allAxes = navigator.getPlot().getAxesNames();
				navigator.setAxes(allAxes);
			}
		}
	}

	/**
	 * Sets the current position and zoom level as default values.
	 */
	public void setDefaultState() {
		if (navigator != null) {
			navigator.setDefaultState();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		if (!stateInitialized) {
			setDefaultState();
			stateInitialized = true;
		}
		super.paintComponent(g);
	}
}
