package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Point {
	private final Map<Direction, Point> neighbours = new HashMap<>();
	private static final double EPSILON = 1e-10; // small value to account for floating-point errors

	private final double m = 0.05; //0.05
	private final double d = 0.2; //0.2
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
		nextOil = oil + nextOil;
		wind(windPoint);
		dispersion();
	}

	private void dispersion() {
		for (Direction dir : Direction.getEdges()) {
			Point nei = neighbours.get(dir);
			if (nei.getType() == PointType.WATER) {
				double oilDifference = m * (oil - nei.getOil());
				nextOil -= oilDifference;
				nei.nextOil += oilDifference;

				if (Math.abs(nextOil) < EPSILON) {
					nextOil = 0;
				}
				if (Math.abs(nei.nextOil) < EPSILON) {
					nei.nextOil = 0;
				}
			}
		}

		for (Direction dir : Direction.getCorners()) {
			Point nei = neighbours.get(dir);
			if (nei.getType() == PointType.WATER) {
				double oilDifference = m * d * (oil - nei.getOil());
				nextOil -= oilDifference;
				nei.nextOil += oilDifference;

				if (Math.abs(nextOil) < EPSILON) {
					nextOil = 0;
				}
				if (Math.abs(nei.nextOil) < EPSILON) {
					nei.nextOil = 0;
				}
			}
		}
	}


	private void wind(WindPoint windPoint) {
		int size  = Board.getSIZE();
		double dt = Board.getDT();
		int area = size * size;

		// North - South shift of the cell
		double dNS = Math.abs(windPoint.direction().getVectorAsArray()[0] * dt * windPoint.speed());
		// East - West shift of the cell
		double dEW = Math.abs(windPoint.direction().getVectorAsArray()[1] * dt * windPoint.speed());

		// based on dNS and dEW we can calculate the overlapping directions
		List<Direction> overlappingDirections = new ArrayList<>();
		if (dNS > 0) {
			if (dEW > 0) {
				overlappingDirections.add(Direction.NE);
				overlappingDirections.add(Direction.E);
			}
			if (dEW < 0) {
				overlappingDirections.add(Direction.NW);
				overlappingDirections.add(Direction.W);
			}
			overlappingDirections.add(Direction.N);
		} else {
			if (dEW > 0) {
				overlappingDirections.add(Direction.SE);
				overlappingDirections.add(Direction.E);
			}
			if (dEW < 0) {
				overlappingDirections.add(Direction.SW);
				overlappingDirections.add(Direction.W);
			}
			overlappingDirections.add(Direction.S);
		}

		for (Direction dir : overlappingDirections) {
			Point nei = neighbours.get(dir);
			if (nei.getType() == PointType.WATER) {
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
				double oilToMove = proportion * oil;

				if (oilToMove > oil) {
					oilToMove = oil; // Ensure we do not move more oil than available
				}

//				System.out.println("New area: " + newArea + "Oil to move: " + oilToMove);

				nextOil -= oilToMove;
				nei.nextOil += oilToMove;

				if (Math.abs(nextOil) < EPSILON) {
					nextOil = 0;
				}
				if (Math.abs(nei.nextOil) < EPSILON) {
					nei.nextOil = 0;
				}
			}
		}
//		throw new UnsupportedOperationException("Not implemented yet");
	}




	public void updateOil() {
		oil = nextOil;
		nextOil = 0;
	}
}
