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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import de.erichseifert.gral.graphics.Container;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.util.Messages;
import de.erichseifert.gral.util.PointND;


/**
 * A panel implementation that displays a {@code Drawable} instance as a
 * rich Swing component.
 */
public class InteractivePanel extends DrawablePanel implements Printable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 9084883142053148090L;

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
	private Point2D popupMenuPos;

	/** Chooser for image export. */
	private final JFileChooser exportImageChooser;

	/** Object to be used as listener for zooming actions. */
	private MouseZoomListener zoomListener;
	/** Object to be used as listener for panning actions. */
	private NavigationMoveListener panListener;

	/**
	 * Listener class for zooming actions.
	 */
	private final static class MouseZoomListener extends MouseAdapter
			implements MouseWheelListener, Serializable {
		/** Version id for serialization. */
		private static final long serialVersionUID = -7323541053291673122L;

		private final InteractivePanel panel;

		public MouseZoomListener(InteractivePanel panel) {
			this.panel = panel;
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			Point2D point = e.getPoint();
			panel.zoom(point, -e.getWheelRotation());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) &&
					(e.getClickCount() == 2))  {
				Point2D point = e.getPoint();
				panel.zoom(point, 1);
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
				zoom(popupMenuPos, 1);
			}
		});
		actions.put("zoomOut", new AbstractAction(Messages.getString( //$NON-NLS-1$
			"InteractivePanel.zoomOut")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				zoom(popupMenuPos, -1);
			}
		});
		actions.put("resetView", new AbstractAction(Messages.getString( //$NON-NLS-1$
				"InteractivePanel.resetView")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				resetZoom(popupMenuPos);
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

		setZoomable(true);
		setPannable(true);
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
	 * Zooms a navigable object in (positive values) or out (negative values).
	 * @param point The location where the zoom was triggered.
	 * @param times Number of times the navigable object will be zoomed.
	 *        Positive values zoom in, negative values zoom out.
	 */
	private void zoom(Point2D point, int times) {
		if (!isZoomable()) {
			return;
		}

		Navigable navigable = InteractivePanel.getNavigableAt(getDrawable(), point);
		if (navigable == null) {
			return;
		}

		Navigator navigator = navigable.getNavigator();
		if (times >= 0) {
			for (int i = 0; i < times; i++) {
				navigator.zoomIn();
			}
		} else {
			for (int i = 0; i < -times; i++) {
				navigator.zoomOut();
			}
		}

		repaint();
	}

	private void resetZoom(Point2D point) {
		if (!isZoomable()) {
			return;
		}

		Navigable navigable = InteractivePanel.getNavigableAt(getDrawable(), point);
		if (navigable == null) {
			return;
		}

		Navigator navigator = navigable.getNavigator();
		navigator.reset();

		repaint();
	}

	/**
	 * Method that exports the current view to a file using a specified file type.
	 * @param component Drawable that will be exported.
	 * @param mimeType File format as MIME type string.
	 * @param file File to export to.
	 * @param documentBounds Document boundary rectangle
	 */
	private void export(Drawable component, String mimeType, File file,
			Rectangle2D documentBounds) {
		try (FileOutputStream destination = new FileOutputStream(file)) {
			DrawableWriter writer = DrawableWriterFactory.getInstance().get(mimeType);
			writer.write(component, destination,
				documentBounds.getX(), documentBounds.getY(),
				documentBounds.getWidth(), documentBounds.getHeight());
		} catch (IOException e) {
			// TODO: Exception handling
			e.printStackTrace();
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
        	popupMenuPos = e.getPoint();
    		menu.show(e.getComponent(), e.getX(), e.getY());
	    }
	}

	/**
	 * Class that handles mouse moves for navigation.
	 */
	private static class NavigationMoveListener extends MouseAdapter {
		/** A reference to the panel for refreshing. */
		private final InteractivePanel panel;
		/** AbstractPlot that will be changed by this class. */
		private Navigable navigable;
		/** Previously clicked point or {@code null}. */
		private Point posPrev;

		/**
		 * Creates a new listener and initializes it with a panel.
		 * @param panel InteractivePanel that should be refreshed.
		 */
		public NavigationMoveListener(InteractivePanel panel) {
			this.panel = panel;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			Point point = e.getPoint();
			navigable = InteractivePanel.getNavigableAt(panel.getDrawable(), point);
			posPrev = point;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (navigable == null) {
				return;
			}

			// Calculate distance that the current view was dragged
			// (screen units)
			Point pos = e.getPoint();
			Navigator navigator = navigable.getNavigator();

			int dx = pos.x - posPrev.x;
			int dy = pos.y - posPrev.y;
			posPrev = pos;

			if (Math.abs(dx) > MIN_DRAG || Math.abs(dy) > MIN_DRAG) {
				PointND<Integer> deltas = new PointND<>(dx, dy);
				navigator.pan(deltas);
				panel.repaint();
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
		if (this.zoomable == zoomable) {
			return;
		}

		this.zoomable = zoomable;

		if (zoomListener != null) {
			removeMouseWheelListener(zoomListener);
			removeMouseListener(zoomListener);
			zoomListener = null;
		}

		if (zoomable) {
			zoomListener = new MouseZoomListener(this);
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
		if (this.pannable == pannable) {
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
			panListener = new NavigationMoveListener(this);
			addMouseListener(panListener);
			addMouseMotionListener(panListener);
		}

		actions.get("resetView").setEnabled(isZoomable() && isPannable()); //$NON-NLS-1$
	}

	/**
	 * Returns a navigable area at the specified point, {@code null} if no
	 * object could be found. If the specified container isn't navigable, its
	 * children are recursively checked.
	 * @param drawable The drawable container to check for navigable children.
	 * @param point Position that should hit the navigable object.
	 * @return A navigable object.
	 */
	private static Navigable getNavigableAt(Drawable drawable, Point2D point) {
		List<Drawable> componentsToCheck;
		if (drawable instanceof Container) {
			componentsToCheck = ((Container) drawable).getDrawablesAt(point);
		} else {
			componentsToCheck = new ArrayList<>(1);
			componentsToCheck.add(drawable);
		}
		for (Drawable component : componentsToCheck) {
			if ((component instanceof Navigable) && component.getBounds().contains(point)) {
				return (Navigable) component;
			}
		}
		return null;
	}
}
