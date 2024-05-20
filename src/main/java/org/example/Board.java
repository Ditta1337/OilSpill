package org.example;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

/**
 * Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final PointType DEFAULTPOINT = PointType.WATER;
	private static final int SIZE = 14;
	private static final double DT = 1;
	private static final double MAXOIL = 1000;
	private Point[][] points;
	private WindPoint[][] windMap;
	private PointType selectedType = PointType.OIL;
	private int length;
	private int height;

	public Board(int length, int height) {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.CYAN);
		setOpaque(true);
	}

	public static double getMaxOil() {
		return MAXOIL;
	}

	public static int getSIZE() {
		return SIZE;
	}

	public static double getDT() {
		return DT;
	}

	// single iteration
	public void iteration() {
		for (int x = 1; x < length - 1; ++x) {
			for (int y = 1; y < height - 1; ++y) {
				points[x][y].calculateNextOil(windMap[x][y]);
			}
		}

		for (int x = 1; x < length - 1; ++x) {
			for (int y = 1; y < height - 1; ++y) {
				points[x][y].updateOil();
			}
		}

		repaint();

	}

	// clearing board
	public void clear() {
		for (int x = 0; x < length; ++x) {
			for (int y = 0; y < height; ++y) {
				points[x][y].setType(DEFAULTPOINT);
				points[x][y].setOil(0);
			}
			this.repaint();
		}
	}

	private void initialize(int length, int height) {
		points = new Point[length][height];
		windMap = WindGenerator.generateSimpleWindMap(length, height, 1, Direction.NE);

		for (int x = 0; x < length; ++x) {
			for (int y = 0; y < height; ++y) {
				points[x][y] = new Point(DEFAULTPOINT);
			}
		}

		initializeNeighbors(length, height);
	}

	private void initializeNeighbors(int length, int height) {
		for (int x = 1; x < length - 1; ++x) {
			for (int y = 1; y < height - 1; ++y) {
				mooreNeighborhood(x, y);
			}
		}
	}

	private void mooreNeighborhood(int x, int y) {
		points[x][y].addNeighbour(Direction.N, points[x][y - 1]);
		points[x][y].addNeighbour(Direction.S, points[x][y + 1]);
		points[x][y].addNeighbour(Direction.E, points[x + 1][y]);
		points[x][y].addNeighbour(Direction.W, points[x - 1][y]);
		points[x][y].addNeighbour(Direction.NE, points[x + 1][y - 1]);
		points[x][y].addNeighbour(Direction.NW, points[x - 1][y - 1]);
		points[x][y].addNeighbour(Direction.SE, points[x + 1][y + 1]);
		points[x][y].addNeighbour(Direction.SW, points[x - 1][y + 1]);
	}

	public Point[][] getMapState() {
		return points;
	}


	public void setMapState(Point[][] mapState) {
		length = mapState.length;
		height = mapState[0].length;
		setPreferredSize(new Dimension(length * SIZE, height * SIZE));
		points = mapState;
		setOil();
		initializeNeighbors(length, height);
		revalidate();
		repaint();

		// prevents componentResized from being called and clearing the board
		removeComponentListener(this);
		SwingUtilities.getWindowAncestor(this).pack();
		addComponentListener(this);
	}

	private void setOil() {
		for (int x = 0; x < length; ++x) {
			for (int y = 0; y < height; ++y) {
				if (points[x][y].getType() == PointType.OIL) {
					points[x][y].setOil(MAXOIL);
				}
			}
		}

	}

	public void setPointType(PointType type) {
		selectedType = type;
	}

	//paint background and separators between cells
	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.GRAY);
		drawNetting(g);
	}

	// draws the background netting
	private void drawNetting(Graphics g) {
		Insets insets = getInsets();
		int firstX = insets.left;
		int firstY = insets.top;
		int lastX = getWidth() - insets.right;
		int lastY = getHeight() - insets.bottom;

		int x = firstX;
		while (x < lastX) {
			g.drawLine(x, firstY, x, lastY);
			x += SIZE;
		}

		int y = firstY;
		while (y < lastY) {
			g.drawLine(firstX, y, lastX, y);
			y += SIZE;
		}

		for (x = 0; x < length; ++x) {
			for (y = 0; y < height; ++y) {
				switch (points[x][y].getType()) {
					case WATER:
						double oil = points[x][y].getOil();
						// set color to shade of cyan based on oil level
						// 0 is cyan and 1000 is black
						g.setColor(new Color(0, (int) (255 - (255 * oil / MAXOIL)), (int) (255 - (255 * oil / MAXOIL))));
						break;
					case LAND:
						g.setColor(Color.YELLOW);
						break;
					}
				g.fillRect((x * SIZE) + 1, (y * SIZE) + 1, (SIZE - 1), (SIZE - 1));
			}
		}
	}



	public void mouseClicked(MouseEvent e) {
		int x = e.getX() / SIZE;
		int y = e.getY() / SIZE;
		if ((x < length) && (x > 0) && (y < height) && (y > 0)) {
			this.repaint();
		}
	}

	public void componentResized(ComponentEvent e) {
		length = (getWidth() / SIZE) + 1;
		height = (getHeight() / SIZE) + 1;
		initialize(length, height);
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / SIZE;
		int y = e.getY() / SIZE;
		if ((x < length) && (x > 0) && (y < height) && (y > 0)) {

			if (selectedType == PointType.OIL) {
				points[x][y].setOil(MAXOIL);
			}
			else {
				points[x][y].setType(selectedType);
			}
			repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
