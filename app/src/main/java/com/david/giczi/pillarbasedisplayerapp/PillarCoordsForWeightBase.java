package com.david.giczi.pillarbasedisplayerapp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class PillarCoordsForWeightBase {

	public Point pillarCenterPoint;
	public Point axisDirectionPoint;
	private double horizontalDistanceBetweenPillarLegs;
	private double verticalDistanceBetweenPillarLegs;
	private double horizontalSizeOfHoleOfPillarLeg;
	private double verticalSizeOfHoleOfPillarLeg; 
	private double distanceOnTheAxis;
	private double rotation = 0;
	private double angleValueBetweenMainPath = 0;
	private double angularMinuteValueBetweenMainPath = 0;
	private double angularSecondValueBetweenMainPath = 0;
	private double radRotation = 0;
	private List<Point> pillarPoints;
	private double azimuth;
	private boolean sideOfAngle;
	
	public PillarCoordsForWeightBase(Point pillarCenterPoint, Point axisDirectonPoint) throws InvalidParameterException {
		this.pillarCenterPoint = pillarCenterPoint;
		this.axisDirectionPoint = axisDirectonPoint;
		AzimuthAndDistance azimuthAndDistance = new AzimuthAndDistance(pillarCenterPoint, axisDirectionPoint);
		this.azimuth = azimuthAndDistance.calcAzimuth();
		if(azimuth == Double.NaN) {
			throw new InvalidParameterException();
		}
	}
	
	public Point getPillarCenterPoint() {
		return pillarCenterPoint;
	}
	public void setPillarCenterPoint(Point pillarCenterPoint) {
		this.pillarCenterPoint = pillarCenterPoint;
	}
	public Point getAxisDirectionPoint() {
		return axisDirectionPoint;
	}
	public void setAxisDirectionPoint(Point axisDirectionPoint) {
		this.axisDirectionPoint = axisDirectionPoint;
	}
	public double getHorizontalDistanceBetweenPillarLegs() {
		return horizontalDistanceBetweenPillarLegs;
	}
	public void setHorizontalDistanceBetweenPillarLegs(double horizontalDistanceBetweenPillarLegs) {
		this.horizontalDistanceBetweenPillarLegs = horizontalDistanceBetweenPillarLegs;
	}
	public double getVerticalDistanceBetweenPillarLegs() {
		return verticalDistanceBetweenPillarLegs;
	}
	public void setVerticalDistanceBetweenPillarLegs(double verticalDistanceBetweenPillarLegs) {
		this.verticalDistanceBetweenPillarLegs = verticalDistanceBetweenPillarLegs;
	}
	public double getHorizontalSizeOfHoleOfPillarLeg() {
		return horizontalSizeOfHoleOfPillarLeg;
	}
	public void setHorizontalSizeOfHoleOfPillarLeg(double horizontalSizeOfHoleOfPillarLeg) {
		this.horizontalSizeOfHoleOfPillarLeg = horizontalSizeOfHoleOfPillarLeg;
	}
	public double getVerticalSizeOfHoleOfPillarLeg() {
		return verticalSizeOfHoleOfPillarLeg;
	}
	public void setVerticalSizeOfHoleOfPillarLeg(double verticalSizeOfHoleOfPillarLeg) {
		this.verticalSizeOfHoleOfPillarLeg = verticalSizeOfHoleOfPillarLeg;
	}
	
	public double getDistanceOnTheAxis() {
		return distanceOnTheAxis;
	}
	public void setDistanceOnTheAxis(double distanceOnTheAxis) {
		this.distanceOnTheAxis = distanceOnTheAxis;
	}
	
	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	
	public List<Point> getPillarPoints() {
		return pillarPoints;
	}
	
	public double getAngleValueBetweenMainPath() {
		return angleValueBetweenMainPath;
	}

	public void setAngleValueBetweenMainPath(double angleValueBetweenMainPath) {
		this.angleValueBetweenMainPath = angleValueBetweenMainPath;
	}

	public double getAngularMinuteValueBetweenMainPath() {
		return angularMinuteValueBetweenMainPath;
	}

	public void setAngularMinuteValueBetweenMainPath(double angularMinuteValueBetweenMainPath) {
		this.angularMinuteValueBetweenMainPath = angularMinuteValueBetweenMainPath;
	}

	public double getAngularSecondValueBetweenMainPath() {
		return angularSecondValueBetweenMainPath;
	}

	public void setAngularSecondValueBetweenMainPath(double angularSecondValueBetweenMainPath) {
		this.angularSecondValueBetweenMainPath = angularSecondValueBetweenMainPath;
	}
	
	public double getRadRotation() {
		return radRotation;
	}
	
	public void setSideOfAngle(boolean sideOfAngle) {
		this.sideOfAngle = sideOfAngle;
	}

	public void calculatePillarPoints() {
		this.pillarPoints = new ArrayList<>();
		pillarPoints.add(pillarCenterPoint);
		calculatePointsOnAxis();
		calculatePointsOfLeftAndUpHole();
		calculatePointsOfLeftAndDownHole();
		calculatePointsOfRightAndDownHole();
		calculatePointsOfRightAndUpHole();
		calcRadRotation();
		rotatePillarCoords();
		calculateMainLinePoints();
	}
	
	private void calculatePointsOnAxis() {
		
		PolarPoint point1 = new PolarPoint(pillarCenterPoint, distanceOnTheAxis,
				azimuth, pillarCenterPoint.getPointID() + "_1");
		PolarPoint point2 = new PolarPoint(pillarCenterPoint, distanceOnTheAxis,
				azimuth + Math.PI / 2, pillarCenterPoint.getPointID() + "_2");
		PolarPoint point3 = new PolarPoint(pillarCenterPoint, distanceOnTheAxis, 
				azimuth + Math.PI, pillarCenterPoint.getPointID() + "_3");
		PolarPoint point4 = new PolarPoint(pillarCenterPoint, distanceOnTheAxis, 
				azimuth + 3 * Math.PI / 2, pillarCenterPoint.getPointID() + "_4");
		PolarPoint point5 = new PolarPoint(pillarCenterPoint, 
				(horizontalDistanceBetweenPillarLegs + horizontalSizeOfHoleOfPillarLeg) / 2,
				azimuth, pillarCenterPoint.getPointID() + "_5");
		PolarPoint point6 = new PolarPoint(pillarCenterPoint, 
				(verticalDistanceBetweenPillarLegs + verticalSizeOfHoleOfPillarLeg) / 2,
				azimuth + Math.PI / 2, pillarCenterPoint.getPointID() + "_6");
		PolarPoint point7 = new PolarPoint(pillarCenterPoint,
				(horizontalDistanceBetweenPillarLegs + horizontalSizeOfHoleOfPillarLeg) / 2,
				azimuth + Math.PI, pillarCenterPoint.getPointID() + "_7");
		PolarPoint point8 = new PolarPoint(pillarCenterPoint, 
				(verticalDistanceBetweenPillarLegs + verticalSizeOfHoleOfPillarLeg) / 2,
				azimuth + 3 * Math.PI / 2, pillarCenterPoint.getPointID() + "_8");
		pillarPoints.add(point1.calcPolarPoint());
		pillarPoints.add(point2.calcPolarPoint());
		pillarPoints.add(point3.calcPolarPoint());
		pillarPoints.add(point4.calcPolarPoint());
		pillarPoints.add(point5.calcPolarPoint());
		pillarPoints.add(point6.calcPolarPoint());
		pillarPoints.add(point7.calcPolarPoint());
		pillarPoints.add(point8.calcPolarPoint());
	}
	
	private void calculatePointsOfLeftAndUpHole() {
	PolarPoint point9 = new PolarPoint(pillarPoints.get(5), 
				(verticalDistanceBetweenPillarLegs + verticalSizeOfHoleOfPillarLeg) / 2, 
				azimuth + 3 * Math.PI / 2, pillarCenterPoint.getPointID() + "_9");
	pillarPoints.add(point9.calcPolarPoint());	
	PolarPoint point10 = new PolarPoint(pillarPoints.get(5), 
			(verticalDistanceBetweenPillarLegs - verticalSizeOfHoleOfPillarLeg) / 2, 
			azimuth + 3 * Math.PI / 2, pillarCenterPoint.getPointID() + "_10");
	pillarPoints.add(point10.calcPolarPoint());
	PolarPoint point11 = new PolarPoint(pillarPoints.get(10), 
			horizontalSizeOfHoleOfPillarLeg, 
			azimuth + Math.PI, pillarCenterPoint.getPointID() + "_11");
	pillarPoints.add(point11.calcPolarPoint());
	PolarPoint point12 = new PolarPoint(pillarPoints.get(9), 
			horizontalSizeOfHoleOfPillarLeg, 
		azimuth + Math.PI, pillarCenterPoint.getPointID() + "_12");
		pillarPoints.add(point12.calcPolarPoint());
	}
	
	private void calculatePointsOfLeftAndDownHole() {
		PolarPoint point13 = new PolarPoint(pillarPoints.get(8), 
				(horizontalDistanceBetweenPillarLegs - horizontalSizeOfHoleOfPillarLeg) / 2, 
				azimuth + Math.PI, pillarCenterPoint.getPointID() + "_13");
		pillarPoints.add(point13.calcPolarPoint());
		PolarPoint point14 = new PolarPoint(pillarPoints.get(8), 
				(horizontalDistanceBetweenPillarLegs + horizontalSizeOfHoleOfPillarLeg) / 2, 
				azimuth + Math.PI, pillarCenterPoint.getPointID() + "_14");
		pillarPoints.add(point14.calcPolarPoint());
		PolarPoint point15 = new PolarPoint(pillarPoints.get(14), 
				verticalSizeOfHoleOfPillarLeg, 
				azimuth + Math.PI / 2, pillarCenterPoint.getPointID() + "_15");
		pillarPoints.add(point15.calcPolarPoint());
		PolarPoint point16 = new PolarPoint(pillarPoints.get(13), 
				verticalSizeOfHoleOfPillarLeg, 
				azimuth + Math.PI / 2, pillarCenterPoint.getPointID() + "_16");
		pillarPoints.add(point16.calcPolarPoint());
	}
	
	private void calculatePointsOfRightAndDownHole() {
		PolarPoint point17 = new PolarPoint(pillarPoints.get(7), 
				(verticalDistanceBetweenPillarLegs - verticalSizeOfHoleOfPillarLeg) / 2, 
				azimuth + Math.PI / 2, pillarCenterPoint.getPointID() + "_17");
		pillarPoints.add(point17.calcPolarPoint());
		PolarPoint point18 = new PolarPoint(pillarPoints.get(7), 
				(verticalDistanceBetweenPillarLegs + verticalSizeOfHoleOfPillarLeg) / 2, 
				azimuth + Math.PI / 2, pillarCenterPoint.getPointID() + "_18");
		pillarPoints.add(point18.calcPolarPoint());
		PolarPoint point19 = new PolarPoint(pillarPoints.get(18), 
				horizontalSizeOfHoleOfPillarLeg,
				azimuth, pillarCenterPoint.getPointID() + "_19");
		pillarPoints.add(point19.calcPolarPoint());
		PolarPoint point20 = new PolarPoint(pillarPoints.get(17), 
				horizontalSizeOfHoleOfPillarLeg,
				azimuth, pillarCenterPoint.getPointID() + "_20");
		pillarPoints.add(point20.calcPolarPoint());
	}
	
	private void calculatePointsOfRightAndUpHole() {
		PolarPoint point21 = new PolarPoint(pillarPoints.get(6), 
				(horizontalDistanceBetweenPillarLegs - horizontalSizeOfHoleOfPillarLeg) / 2,
				azimuth, pillarCenterPoint.getPointID() + "_21");
		pillarPoints.add(point21.calcPolarPoint());
		PolarPoint point22 = new PolarPoint(pillarPoints.get(6), 
				(horizontalDistanceBetweenPillarLegs + horizontalSizeOfHoleOfPillarLeg) / 2,
				azimuth, pillarCenterPoint.getPointID() + "_22");
		pillarPoints.add(point22.calcPolarPoint());
		PolarPoint point23 = new PolarPoint(pillarPoints.get(22), 
				verticalSizeOfHoleOfPillarLeg,
				azimuth + 3 * Math.PI / 2, pillarCenterPoint.getPointID() + "_23");
		pillarPoints.add(point23.calcPolarPoint());
		PolarPoint point24 = new PolarPoint(pillarPoints.get(21), 
				verticalSizeOfHoleOfPillarLeg,
				azimuth + 3 * Math.PI / 2, pillarCenterPoint.getPointID() + "_24");
		pillarPoints.add(point24.calcPolarPoint());
	}
	
	private void calcRadRotation() {
		
		 if(angleValueBetweenMainPath != 0 || 
					angularMinuteValueBetweenMainPath != 0 || 
							angularSecondValueBetweenMainPath != 0) {
				
			radRotation = sideOfAngle ? 
					Math.toRadians((180 - (angleValueBetweenMainPath + 
					angularMinuteValueBetweenMainPath / 60 + 
					angularSecondValueBetweenMainPath / 3600)) / 2) :
					Math.toRadians((180 - (360 - (angleValueBetweenMainPath + 
					angularMinuteValueBetweenMainPath / 60 + 
					angularSecondValueBetweenMainPath / 3600))) / 2);		
			
			double rotationValue = sideOfAngle ? 
					angleValueBetweenMainPath + 
					angularMinuteValueBetweenMainPath / 60 + 
					angularSecondValueBetweenMainPath / 3600 : 
					360 - (angleValueBetweenMainPath + 
					angularMinuteValueBetweenMainPath / 60 + 
					angularSecondValueBetweenMainPath / 3600);
			
			
			rotation =  ( angleValueBetweenMainPath == 180 && 
					angularMinuteValueBetweenMainPath == 0 &&
					angularSecondValueBetweenMainPath == 0 ) ? 0 : rotationValue;
		}
	}
	
	private void rotatePillarCoords() {
		
		for (int i = 1; i < pillarPoints.size(); i++) {
			
			double rotated_x = (pillarPoints.get(i).getX_coord() - pillarPoints.get(0).getX_coord()) * Math.cos(radRotation) - 
						(pillarPoints.get(i).getY_coord() - pillarPoints.get(0).getY_coord()) * Math.sin(radRotation);
			double rotated_y = (pillarPoints.get(i).getX_coord() - pillarPoints.get(0).getX_coord()) * Math.sin(radRotation) +
						(pillarPoints.get(i).getY_coord() - pillarPoints.get(0).getY_coord()) * Math.cos(radRotation);
			pillarPoints.get(i)
			.setX_coord(Math.round((pillarPoints.get(0).getX_coord() + rotated_x) * 1000.0) / 1000.0);
			pillarPoints.get(i)
			.setY_coord(Math.round((pillarPoints.get(0).getY_coord() + rotated_y) * 1000.0) / 1000.0);
	}
}
	
	private void calculateMainLinePoints() {
		
		if( angleValueBetweenMainPath == 180 &&
				angularMinuteValueBetweenMainPath == 0 &&
						angularSecondValueBetweenMainPath == 0 )
			return;
		
			AzimuthAndDistance azimuth = new AzimuthAndDistance(pillarCenterPoint, axisDirectionPoint);
			PolarPoint forwardPoint = 
					new PolarPoint(pillarCenterPoint, 20d, azimuth.calcAzimuth(), pillarCenterPoint.getPointID() + "_25");
			
			double backwardDirection = azimuth.calcAzimuth() + (sideOfAngle ?
					Math.toRadians(angleValueBetweenMainPath + 
					angularMinuteValueBetweenMainPath / 60  +
					angularSecondValueBetweenMainPath / 3600) : 
					Math.toRadians(360 - (angleValueBetweenMainPath + 
					angularMinuteValueBetweenMainPath / 60  +
					angularSecondValueBetweenMainPath / 3600)));
			
			PolarPoint backwardPoint =
					new PolarPoint(pillarCenterPoint, 20d, backwardDirection, pillarCenterPoint.getPointID() + "_26" );
			pillarPoints.add(new Point(forwardPoint.getNewPointID(), 
				Math.round(forwardPoint.calcPolarPoint().getX_coord() * 1000.0) / 1000.0, 
				Math.round(forwardPoint.calcPolarPoint().getY_coord() * 1000.0) / 1000.0));
			pillarPoints.add(new Point(backwardPoint.getNewPointID(), 
					Math.round(backwardPoint.calcPolarPoint().getX_coord() * 1000.0) / 1000.0, 
					Math.round(backwardPoint.calcPolarPoint().getY_coord() * 1000.0) / 1000.0));
		}
	
	
}
