package com.david.giczi.pillarbasedisplayerapp.service;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.Objects;

public class Point {

	private String pointID;
	private double x_coord;
	private double y_coord;
	private final DecimalFormat df = new DecimalFormat("0.000");
	
	
	public Point(String pointID, double x_coord, double y_coord) {

		this.pointID = pointID;
		this.x_coord = x_coord;
		this.y_coord = y_coord;
	}

	public String getPointID() {
		return pointID;
	}

	public void setPointID(String pointID) {
		this.pointID = pointID;
	}

	public double getX_coord() {
		return x_coord;
	}


	public double getY_coord() {
		return y_coord;
	}
	
	public void setX_coord(double x_coord) {
		this.x_coord = x_coord;
	}


	public void setY_coord(double y_coord) {
		this.y_coord = y_coord;
	}


	public String writePointForRTK() {
		return this.pointID + "," + this.x_coord + "," + this.y_coord + ",0";
	}
	
	public String writePointForTPS() {
		return this.pointID + ";" + this.x_coord + ";" + this.y_coord + ";0;;";
	}

	@Override
	public int hashCode() {
		return Objects.hash(pointID, x_coord, y_coord);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Point point = (Point) o;
		return Double.compare(point.x_coord, x_coord) == 0 &&
				Double.compare(point.y_coord, y_coord) == 0 &&
				Objects.equals(pointID, point.pointID) && Objects.equals(df, point.df);
	}

	@NonNull
	@Override
	public String toString() {
		return  df.format(x_coord).replace(",", ".") + "\t\t"
				+ df.format(y_coord).replace(",", ".");
	}
}
