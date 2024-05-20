package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Point {
	private final Map<Direction, Point> neighbours = new HashMap<>();
	private final double m = 0.002;
	private final double d = 0.05;
	private PointType type;
	private double oil = 0;
	private double nextOil = 0;
	
	public Point(PointType type) {
		this.type = type;
	}

	public void setOil(double oil) {
		this.oil = oil;
	}

	public double getOil() {
		return oil;
	}
	
	public PointType getType() {
		return type;
	}

	public void setType(PointType type) {
		this.type = type;
	}


	public void addNeighbour(Direction direction, Point nei) {
		neighbours.put(direction, nei);
	}

	public void calculateNextOil(WindPoint windPoint) {
		nextOil = Math.min(oil + nextOil, Board.getMaxOil());
		wind(windPoint);
		dispersion();
	}

	private void dispersion() {
		for (Direction dir : Direction.getEdges()) {
			Point nei = neighbours.get(dir);
			if (nei.getType() == PointType.WATER) {
				nextOil -= m * (oil - nei.getOil());
			}
		}

		for (Direction dir : Direction.getCorners()) {
			Point nei = neighbours.get(dir);
			if (nei.getType() == PointType.WATER) {
				nextOil -= m * d * (oil - nei.getOil());
			}
		}
	}

	private void wind(WindPoint windPoint) {
		int size  = Board.getSIZE();
		double dt = Board.getDT();
		int area = size * size;
		Direction[] overlappingDirections = windPoint.direction().getOverlappingDirections();

		// North - South shift of the cell
		double dNS = Math.abs(windPoint.direction().toUnitVector()[0] * dt * windPoint.strength());
		// East - West shift of the cell
		double dEW = Math.abs(windPoint.direction().toUnitVector()[1] * dt * windPoint.strength());

		for (Direction dir : overlappingDirections) {
			Point nei = neighbours.get(dir);
			if (nei.getType() == PointType.WATER && nei.getOil() < Board.getMaxOil()){
				double newArea = 0;
				double proportion = 0;
				switch (dir) {
					case N:
					case S:
						newArea = (size - dEW) * dNS;
						break;
					case E:
					case W:
						newArea = (size - dNS) * dEW;
						break;
					case NE:
					case SW:
					case NW:
					case SE:
						newArea = dEW * dNS;
						break;
					default:
						break;
				}
				proportion = newArea / area;
				partitionOil(proportion, nei);

			}
		}
	}

	private void partitionOil(double proportion, Point nei) {
		if (nei.nextOil + proportion * oil < Board.getMaxOil()) {
			nextOil -= proportion * oil;
			nei.nextOil += proportion * oil;
		} else {
			nextOil -= Board.getMaxOil() - nei.nextOil;
			nei.nextOil = Board.getMaxOil();
		}
	}

	public void updateOil() {
		oil = nextOil;
		nextOil = 0;
	}
}
