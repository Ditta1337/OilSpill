package org.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class containing GUI: board + buttons
 */
public class GUI extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private Timer timer;
	private Board board;
	private JButton start;
	private JButton clear;
	private JButton save;
	private JButton load;
	private JSlider speed;
	private JCheckBox showWind;
	private JComboBox<PointType> drawType;
	private JFrame frame;
	private int iterNum = 0;
	private final int maxDelay = 500;
	private final int initDelay = 100;
	private boolean running = false;

	public GUI(JFrame jf) {
		frame = jf;
		timer = new Timer(initDelay, this);
		timer.stop();
	}

	/**
	 * @param container to which GUI and board is added
	 */
	public void initialize(Container container) {
		container.setLayout(new BorderLayout());
		container.setSize(new Dimension(1024, 768));

		JPanel buttonPanel = new JPanel();

		start = new JButton("Start");
		start.setActionCommand("Start");
		start.setToolTipText("Starts clock");
		start.addActionListener(this);

		drawType = new JComboBox<PointType>(PointType.values());
		drawType.setToolTipText("Choose type of point");
		drawType.addActionListener(this);
		drawType.setActionCommand("drawType");

		clear = new JButton("Clear");
		clear.setActionCommand("clear");
		clear.setToolTipText("Clears the board");
		clear.addActionListener(this);

		save = new JButton("Save");
		save.setActionCommand("save");
		save.setToolTipText("Save the board");
		save.addActionListener(this);

		load = new JButton("Load");
		load.setActionCommand("load");
		load.setToolTipText("Load the board");
		load.addActionListener(this);

		speed = new JSlider();
		speed.setMinimum(0);
		speed.setMaximum(maxDelay);
		speed.setToolTipText("Time speed");
		speed.addChangeListener(this);
		speed.setValue(maxDelay - timer.getDelay());

		showWind = new JCheckBox("Show wind");
		showWind.setToolTipText("Show wind");
		showWind.addActionListener(e -> {
			Board.setShowWind(showWind.isSelected());
			board.repaint();
		});


		buttonPanel.add(start);
		buttonPanel.add(drawType);
		buttonPanel.add(clear);
		buttonPanel.add(showWind);
		buttonPanel.add(speed);
		buttonPanel.add(save);
		buttonPanel.add(load);

		board = new Board(1024, 768 - buttonPanel.getHeight());
		container.add(board, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * handles clicking on each button
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(timer)) {
			iterNum++;
			frame.setTitle(board.getTimeElapsed());
			board.iteration();
		} else {
			String command = e.getActionCommand();
			if (command.equals("Start")) {
				if (!running) {
					timer.start();
					start.setText("Pause");
				} else {
					timer.stop();
					start.setText("Start");
				}
				running = !running;
				clear.setEnabled(true);

			}
			else if (command.equals("clear")) {
				iterNum = 0;
				timer.stop();
				start.setEnabled(true);
				board.clear();
				frame.setTitle("Cellular Automata Toolbox");
			}
			else if (command.equals("save")) {
				Optional<String> filename = Optional.ofNullable(JOptionPane.showInputDialog(frame, "Enter filename"));
                filename.ifPresent(name -> Saver.save(board.getMapState(), name));
			}
			else if (command.equals("load")) {
				loadWithDeleteOption();
			}
			else if (command.equals("drawType")) {
				PointType newType = (PointType) drawType.getSelectedItem();
				board.setPointType(newType);
			}
			else
				System.out.println("Unknown command: " + command);

		}
	}

	public void loadWithDeleteOption() {
		String[] files = Loader.getFiles();
		if (files.length == 0) {
			JOptionPane.showMessageDialog(frame, "No files to load");
			return;
		}

		// Create the custom dialog
		JDialog dialog = new JDialog(frame, "Load", true);
		dialog.setLayout(new FlowLayout());

		// Create the file selection combo box
		JComboBox<String> fileSelection = new JComboBox<>(files);
		dialog.add(fileSelection);


		// Create the "OK" button
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			String filename = (String) fileSelection.getSelectedItem();
			if (filename != null) {
				Point[][] mapState = Loader.loadMap(filename);
				if (mapState != null) {
					frame.setTitle("Oil Spill (" + iterNum + " iteration)");
					board.setMapState(mapState);
				}
			}
			dialog.dispose();
		});
		dialog.add(okButton);

		// Create the "Cancel" button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> dialog.dispose());
		dialog.add(cancelButton);

		// Create the "Delete" button
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(e -> {
			String filename = (String) fileSelection.getSelectedItem();
			if (filename != null) {
				Loader.delete(filename);
				fileSelection.removeItem(filename);
			}
		});
		dialog.add(deleteButton);

		// Display the dialog
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * slider to control simulation speed
	 * @see ChangeListener#stateChanged(ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		timer.setDelay(maxDelay - speed.getValue());
	}
}
