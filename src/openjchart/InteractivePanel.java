/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import openjchart.plots.io.DrawableWriter;
import openjchart.plots.io.DrawableWriterFactory;
import openjchart.plots.io.WriterCapabilities;


/**
 * A class that displays a <code>Drawable</code> instance as a rich Swing component.
 */
public class InteractivePanel extends DrawablePanel {
	private static final long serialVersionUID = 1L;
	
	private JPopupMenu menu;
	private JMenu exportMenu;
	private JFileChooser exportChooser;

	public InteractivePanel(Drawable drawable) {
		super(drawable);
		
		exportChooser = new JFileChooser();

		menu = new JPopupMenu();

		exportMenu = new JMenu("Export");
		menu.add(exportMenu);
		String[] exportFormats = DrawableWriterFactory.getInstance().getSupportedFormats();
		for (final String format : exportFormats) {
			WriterCapabilities capabilities = DrawableWriterFactory.getInstance().getCapabilities(format);
			String exporterLabel = String.format("%s: %s...", capabilities.getFormat(), capabilities.getName());
			Action a = new AbstractAction(exporterLabel) {
				@Override
				public void actionPerformed(ActionEvent e) {
					int ret = exportChooser.showSaveDialog(InteractivePanel.this);
					if (ret == JFileChooser.APPROVE_OPTION) {
						Drawable d = getDrawable();
						ExportDialog ed = new ExportDialog(InteractivePanel.this, d);
						ed.setVisible(true);
						if (ed.getUserAction().equals(ExportDialog.UserAction.APPROVE)) {
							File file = exportChooser.getSelectedFile();
							export(d, format, file, ed.getDocumentX(), ed.getDocumentY(),
									ed.getDocumentWidth(), ed.getDocumentHeight());
						}
					}
				}
			};
			exportMenu.add(a);
		}

		addMouseListener(new PopupListener());
	}

	private void export(Drawable d, String format, File f,
			double x, double y, double width, double height) {
		FileOutputStream destination;
		try {
			destination = new FileOutputStream(f);
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return;
		}
		DrawableWriter w = DrawableWriterFactory.getInstance().getDrawableWriter(format);
		try {
			w.write(d, destination, x, y, width, height);
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
	    public void mousePressed(MouseEvent e) {
	        showPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        showPopup(e);
	    }

	    private void showPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	            menu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	}

	public final static class ExportDialog extends JDialog {
		public static enum UserAction { APPROVE, CANCEL };
		
		private double documentX;
		private double documentY;
		private double documentWidth;
		private double documentHeight;
		private UserAction userAction;

		public ExportDialog(Component parent, Drawable d) {
			super(JOptionPane.getFrameForComponent(parent), true);
			setTitle("Export options");

			documentX = d.getX();
			documentY = d.getX();
			documentWidth = d.getWidth();
			documentHeight = d.getHeight();
			userAction = UserAction.CANCEL;

			JPanel cp = new JPanel(new BorderLayout());
			cp.setBorder(new EmptyBorder(10, 10, 10, 10));
			setContentPane(cp);

			DecimalFormat formatMm = new DecimalFormat();
			formatMm.setMinimumFractionDigits(2);

			JPanel options = new JPanel(new GridLayout(4, 2, 10, 2));
			getContentPane().add(options, BorderLayout.NORTH);

			addInputField("Left", options, formatMm, documentX, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					documentX = ((Number)evt.getNewValue()).doubleValue();
				}
			});
			addInputField("Top", options, formatMm, documentY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					documentY = ((Number)evt.getNewValue()).doubleValue();
				}
			});
			addInputField("Width", options, formatMm, documentWidth, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					documentWidth = ((Number)evt.getNewValue()).doubleValue();
				}
			});
			addInputField("Height", options, formatMm, documentHeight, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					documentHeight = ((Number)evt.getNewValue()).doubleValue();
				}
			});

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

		private static void addInputField(String labelText, java.awt.Container cont,
				DecimalFormat format, Object initialValue, PropertyChangeListener pcl) {
			JLabel label = new JLabel(labelText);
			label.setHorizontalAlignment(JLabel.RIGHT);
			cont.add(label);
			JFormattedTextField input = new JFormattedTextField(format);
			input.setValue(initialValue);
			input.setHorizontalAlignment(JFormattedTextField.RIGHT);
			input.addPropertyChangeListener("value", pcl);
			cont.add(input);
			label.setLabelFor(input);
		}

		public double getDocumentX() {
			return documentX;
		}
		public double getDocumentY() {
			return documentY;
		}
		public double getDocumentWidth() {
			return documentWidth;
		}
		public double getDocumentHeight() {
			return documentHeight;
		}
		public UserAction getUserAction() {
			return userAction;
		}
	}
}
