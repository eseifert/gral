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

package openjchart;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import openjchart.io.WriterCapabilities;
import openjchart.io.plots.DrawableWriter;
import openjchart.io.plots.DrawableWriterFactory;


/**
 * A class that displays a <code>Drawable</code> instance as a rich Swing component.
 */
public class InteractivePanel extends DrawablePanel implements Printable {
	private static final long serialVersionUID = 1L;

	// FIXME: Find better method to adjust resolution
	private static final double MM_TO_PT = 72.0/25.4;      // mm -> pt
	private static final double MM_PER_PX = 0.2*MM_TO_PT;  // 1px = 0.2mm
	private final PrinterJob printerJob;

	private final JPopupMenu menu;
	private final JMenuItem refresh;
	private final JMenuItem exportImage;
	private final JMenuItem print;

	private final JFileChooser exportImageChooser;

	public InteractivePanel(Drawable drawable) {
		super(drawable);

		printerJob = PrinterJob.getPrinterJob();
		printerJob.setPrintable(this);
		
		WriterCapabilities[] exportFormats = DrawableWriterFactory.getInstance().getCapabilities();
		exportImageChooser = new ExportChooser(exportFormats);
		exportImageChooser.setDialogTitle("Export image");

		menu = new JPopupMenu();

		refresh = new JMenuItem(new AbstractAction("Refresh") {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		menu.add(refresh);

		menu.addSeparator();

		exportImage = new JMenuItem(new AbstractAction("Export image...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int ret = exportImageChooser.showSaveDialog(InteractivePanel.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					Drawable d = getDrawable();
					ExportDialog ed = new ExportDialog(InteractivePanel.this, d);
					ed.setVisible(true);
					if (ed.getUserAction().equals(ExportDialog.UserAction.APPROVE)) {
						File file = exportImageChooser.getSelectedFile();
						if (exportImageChooser.getSelectedFile() == null) {
							return;
						}
						DrawableWriterFilter filter = (DrawableWriterFilter) exportImageChooser.getFileFilter();
						export(d, filter.getWriterCapabilities().getMimeType(), file, ed.getDocumentBounds());
					}
				}
			}
		});
		menu.add(exportImage);

		print = new JMenuItem(new AbstractAction("Print...") {
			@Override
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
		menu.add(print);

		addMouseListener(new PopupListener());
	}

	private void export(Drawable d, String mimeType, File f, Rectangle2D documentBounds) {
		FileOutputStream destination;
		try {
			destination = new FileOutputStream(f);
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return;
		}
		DrawableWriter w = DrawableWriterFactory.getInstance().getWriter(mimeType);
		try {
			w.write(d, destination, documentBounds.getX(), documentBounds.getY(),
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
	        if (e.isPopupTrigger()) {
	            menu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	}

	public final static class DrawableWriterFilter extends FileFilter {
		private final WriterCapabilities capabilities;

		public DrawableWriterFilter(WriterCapabilities capabilities) {
			this.capabilities = capabilities;
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String ext = getExtension(f).toLowerCase();
			for (String extension : capabilities.getExtensions()) {
				if (extension.equals(ext)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String getDescription() {
			return String.format("%s: %s", capabilities.getFormat(), capabilities.getName());
		}

		public WriterCapabilities getWriterCapabilities() {
			return capabilities;
		}

		private final static String getExtension(File f) {
			String name = f.getName();
			int lastDot = name.lastIndexOf('.');
			if ((lastDot <= 0) || (lastDot == name.length() - 1)) {
				return "";
			}
			return name.substring(lastDot + 1);
		}
	}

	public final static class ExportChooser extends JFileChooser {
		public ExportChooser(WriterCapabilities... capabilities) {
			setAcceptAllFileFilterUsed(false);
			for (WriterCapabilities c : capabilities) {
				addChoosableFileFilter(new DrawableWriterFilter(c));
			}
		}
	}

	public final static class ExportDialog extends JDialog {
		public static enum UserAction { APPROVE, CANCEL };

		private final Rectangle2D documentBounds;
		private UserAction userAction;

		private final JFormattedTextField inputX;
		private final JFormattedTextField inputY;
		private final JFormattedTextField inputW;
		private final JFormattedTextField inputH;

		public ExportDialog(Component parent, Drawable d) {
			super(JOptionPane.getFrameForComponent(parent), true);
			setTitle("Export options");

			documentBounds = new Rectangle2D.Double();
			documentBounds.setFrame(d.getBounds());
			userAction = UserAction.CANCEL;

			JPanel cp = new JPanel(new BorderLayout());
			cp.setBorder(new EmptyBorder(10, 10, 10, 10));
			setContentPane(cp);

			DecimalFormat formatMm = new DecimalFormat();
			formatMm.setMinimumFractionDigits(2);

			JPanel options = new JPanel(new GridLayout(4, 2, 10, 2));
			getContentPane().add(options, BorderLayout.NORTH);

			PropertyChangeListener docBoundsListener = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					documentBounds.setFrame(
						((Number)inputX.getValue()).doubleValue(),
						((Number)inputY.getValue()).doubleValue(),
						((Number)inputW.getValue()).doubleValue(),
						((Number)inputH.getValue()).doubleValue());
				}
			};
			inputX = new JFormattedTextField(formatMm);
			addInputField(inputX, "Left", options, documentBounds.getX(), docBoundsListener);
			inputY = new JFormattedTextField(formatMm);
			addInputField(inputY, "Top", options, documentBounds.getY(), docBoundsListener);
			inputW = new JFormattedTextField(formatMm);
			addInputField(inputW, "Width", options, documentBounds.getWidth(), docBoundsListener);
			inputH = new JFormattedTextField(formatMm);
			addInputField(inputH, "Height", options, documentBounds.getHeight(), docBoundsListener);

			JPanel controls = new JPanel(new FlowLayout());
			cp.add(controls, BorderLayout.SOUTH);

			JButton buttonConfirm = new JButton("OK");
			buttonConfirm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					userAction = UserAction.APPROVE;
					dispose();
				}
			});
			controls.add(buttonConfirm);

			JButton buttonCancel = new JButton("Cancel");
			buttonCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					userAction = UserAction.CANCEL;
					dispose();
				}
			});
			controls.add(buttonCancel);

			pack();
			setLocationRelativeTo(parent);
		}

		private static void addInputField(JFormattedTextField input, String labelText,
				java.awt.Container cont, Object initialValue, PropertyChangeListener pcl) {
			JLabel label = new JLabel(labelText);
			label.setHorizontalAlignment(JLabel.RIGHT);
			cont.add(label);
			input.setValue(initialValue);
			input.setHorizontalAlignment(JFormattedTextField.RIGHT);
			input.addPropertyChangeListener("value", pcl);
			cont.add(input);
			label.setLabelFor(input);
		}

		public Rectangle2D getDocumentBounds() {
			Rectangle2D bounds = new Rectangle2D.Double();
			bounds.setFrame(documentBounds);
			return bounds;
		}
		public UserAction getUserAction() {
			return userAction;
		}
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

		Graphics2D g2d = (Graphics2D) g;
		AffineTransform txOld = g2d.getTransform();
		g2d.scale(MM_PER_PX, MM_PER_PX);

		Rectangle2D boundsOld = getDrawable().getBounds();
		Rectangle2D pageBounds = new Rectangle2D.Double(
				pageFormat.getImageableX()/MM_PER_PX, pageFormat.getImageableY()/MM_PER_PX,
				pageFormat.getImageableWidth()/MM_PER_PX, pageFormat.getImageableHeight()/MM_PER_PX);

		// Set size
		// TODO: Keep Drawable's aspect ratio when scaling
		getDrawable().setBounds(pageBounds);
		// TODO: Be sure to temporarily turn off antialiasing before printing
		try {
			getDrawable().draw(g2d);
		} finally {
			getDrawable().setBounds(boundsOld);
		}
		g2d.setTransform(txOld);
		return Printable.PAGE_EXISTS;
	}

}
