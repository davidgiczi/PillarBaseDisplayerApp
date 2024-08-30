package com.david.giczi.pillarbasedisplayerapp.service;


public class PolarPoint {

	private final Point pointA;
	private final double distance;
	private final double azimuth;
	private final String newPointID;
	
	
	public PolarPoint(Point pointA, double distance, double azimuth, String newPointID) {
		this.pointA = pointA;
		this.distance = distance;
		this.azimuth = azimuth;
		this.newPointID = newPointID;
	}

	public Point calcPolarPoint() {
		
		double newPointX = pointA.getX_coord() + Math.sin(azimuth) * distance;
		double newPointY = pointA.getY_coord() + Math.cos(azimuth) * distance;
		return new Point(newPointID, newPointX, newPointY);
	}

	public String getNewPointID() {
		return newPointID;
	}
	
	
}
