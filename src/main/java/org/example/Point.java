package org.example;

import java.util.ArrayList;
import java.util.List;

public class Point {
	private List<Point> neighbors = new ArrayList<Point>();
	private PointType type;
	
	public Point(PointType type) {
		this.type = type;
	}

	public void clicked() {
	}
	
	public PointType getType() {
		return type;
	}

	public void setType(PointType type) {
		this.type = type;
	}

	public void setState(int s) {
	}

	
	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}
}
