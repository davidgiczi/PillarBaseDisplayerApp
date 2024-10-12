package com.david.giczi.pillarbasedisplayerapp.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PillarLocationCalculator {

    public String centerX;
    public String centerY;
    public String directionX;
    public String directionY;
    private final List<Point> centerPillarMeasData;
    private final List<Point> directionPillarMeasData;

    private Double distance;
    private final DecimalFormat df;

    public PillarLocationCalculator() {
        centerPillarMeasData = new ArrayList<>();
        directionPillarMeasData = new ArrayList<>();
        df= new DecimalFormat("0.000");
    }

    public void addCenterPillarMeasData(String foot1Y, String foot1X, String foot2Y, String foot2X,
        String foot3Y, String foot3X, String foot4Y, String foot4X){

        if( !foot1Y.isEmpty() && !foot1X.isEmpty() ){
            Point foot1Point = new Point("1", Double.parseDouble(foot1Y.replace(",", ".")),
                    Double.parseDouble(foot1X.replace(",", ".")));
            centerPillarMeasData.add(foot1Point);
        }
        if( !foot2Y.isEmpty() && !foot2X.isEmpty() ){
            Point foot2Point = new Point("2", Double.parseDouble(foot2Y.replace(",", ".")),
                    Double.parseDouble(foot2X.replace(",", ".")));
            centerPillarMeasData.add(foot2Point);
        }
        if( !foot3Y.isEmpty() && !foot3X.isEmpty() ){
            Point foot3Point = new Point("3", Double.parseDouble(foot3Y.replace(",", ".")),
                    Double.parseDouble(foot3X.replace(",", ".")));
            centerPillarMeasData.add(foot3Point);
        }
        if( !foot4Y.isEmpty() && !foot4X.isEmpty() ){
            Point foot4Point = new Point("4", Double.parseDouble(foot4Y.replace(",", ".")),
                    Double.parseDouble(foot4X.replace(",", ".")));
            centerPillarMeasData.add(foot4Point);
        }
    }

    public void addDirectionPillarMeasData(String foot1Y, String foot1X, String foot2Y, String foot2X,
                                        String foot3Y, String foot3X, String foot4Y, String foot4X){

        if( !foot1Y.isEmpty() && !foot1X.isEmpty() ){
            Point foot1Point = new Point("1", Double.parseDouble(foot1Y.replace(",", ".")),
                    Double.parseDouble(foot1X.replace(",", ".")));
            directionPillarMeasData.add(foot1Point);
        }
        if( !foot2Y.isEmpty() && !foot2X.isEmpty() ){
            Point foot2Point = new Point("2", Double.parseDouble(foot2Y.replace(",", ".")),
                    Double.parseDouble(foot2X.replace(",", ".")));
            directionPillarMeasData.add(foot2Point);
        }
        if( !foot3Y.isEmpty() && !foot3X.isEmpty() ){
            Point foot3Point = new Point("3", Double.parseDouble(foot3Y.replace(",", ".")),
                    Double.parseDouble(foot3X.replace(",", ".")));
            directionPillarMeasData.add(foot3Point);
        }
        if( !foot4Y.isEmpty() && !foot4X.isEmpty() ){
            Point foot4Point = new Point("4", Double.parseDouble(foot4Y.replace(",", ".")),
                    Double.parseDouble(foot4X.replace(",", ".")));
            directionPillarMeasData.add(foot4Point);
        }

    }

    public void calcPillarLocationData(){

        Double aveDirectionX = null;
        Double aveDirectionY = null;

        if( !directionPillarMeasData.isEmpty() && directionPillarMeasData.size() != 3 ){
            aveDirectionX = directionPillarMeasData.stream().mapToDouble(Point::getX_coord)
                    .summaryStatistics().getAverage();
            aveDirectionY = directionPillarMeasData.stream().mapToDouble(Point::getY_coord)
                    .summaryStatistics().getAverage();
            directionX = df.format(aveDirectionX).replace(",", ".");
            directionY = df.format(aveDirectionY).replace(",", ".");
        }

        Double aveCenterX;
        Double aveCenterY;

        if( !centerPillarMeasData.isEmpty() && centerPillarMeasData.size() != 3 ){
            aveCenterX = centerPillarMeasData.stream().mapToDouble(Point::getX_coord)
                    .summaryStatistics().getAverage();
            aveCenterY = centerPillarMeasData.stream().mapToDouble(Point::getY_coord)
                    .summaryStatistics().getAverage();

            if( aveDirectionX != null && distance == null ){
                centerX = df.format((aveCenterX + aveDirectionX) / 2.0).replace(",", ".");
                centerY = df.format((aveCenterY + aveDirectionY) / 2.0).replace(",", ".");
            }
            else if( aveDirectionX != null ){
                Point startPoint = new Point("start", aveCenterX, aveCenterY);
                Point endPoint = new Point("end", aveDirectionX, aveDirectionY);
                AzimuthAndDistance centerPointData = new AzimuthAndDistance(startPoint, endPoint);
                PolarPoint centerPoint = new PolarPoint(startPoint, distance, centerPointData.calcAzimuth(), "center");
                centerX = df.format(centerPoint.calcPolarPoint().getX_coord()).replace(",", ".");
                centerY = df.format(centerPoint.calcPolarPoint().getY_coord()).replace(",", ".");
            }
            else if( centerPillarMeasData.size() == 4 && distance != null ) {
                aveDirectionX = (centerPillarMeasData.get(1).getX_coord() + centerPillarMeasData.get(2).getX_coord()) / 2.0;
                aveDirectionY = (centerPillarMeasData.get(1).getY_coord() + centerPillarMeasData.get(2).getY_coord()) / 2.0;
                Point startPoint = new Point("start", aveCenterX, aveCenterY);
                Point endPoint = new Point("end", aveDirectionX, aveDirectionY);
                AzimuthAndDistance centerPointData = new AzimuthAndDistance(startPoint, endPoint);
                PolarPoint centerPoint = new PolarPoint(startPoint, distance, centerPointData.calcAzimuth(), "center");
                centerX = df.format(centerPoint.calcPolarPoint().getX_coord()).replace(",", ".");
                centerY = df.format(centerPoint.calcPolarPoint().getY_coord()).replace(",", ".");
                directionX = df.format(aveCenterX).replace(",", ".");
                directionY = df.format(aveCenterY).replace(",", ".");
            }

        }
        else if( centerPillarMeasData.size() == 3 && distance != null ){
            aveCenterX = (centerPillarMeasData.get(0).getX_coord() + centerPillarMeasData.get(2).getX_coord()) / 2.0;
            aveCenterY = (centerPillarMeasData.get(0).getY_coord() + centerPillarMeasData.get(2).getY_coord()) / 2.0;
            if( aveDirectionX == null ){
                aveDirectionX = (centerPillarMeasData.get(1).getX_coord() + centerPillarMeasData.get(2).getX_coord()) / 2.0;
            }
            if( aveDirectionY == null ){
                aveDirectionY = (centerPillarMeasData.get(1).getY_coord() + centerPillarMeasData.get(2).getY_coord()) / 2.0;
            }
            Point startPoint = new Point("start", aveCenterX, aveCenterY);
            Point endPoint = new Point("end", aveDirectionX, aveDirectionY);
            AzimuthAndDistance centerPointData = new AzimuthAndDistance(startPoint, endPoint);
            PolarPoint centerPoint = new PolarPoint(startPoint, distance, centerPointData.calcAzimuth(), "center");
            centerX = df.format(centerPoint.calcPolarPoint().getX_coord()).replace(",", ".");
            centerY = df.format(centerPoint.calcPolarPoint().getY_coord()).replace(",", ".");
            directionX = df.format(aveCenterX).replace(",", ".");
            directionY = df.format(aveCenterY).replace(",", ".");
        }

    }

    public void setDistance(String distance) {
        if( !distance.isEmpty() ){
            this.distance = Double.parseDouble(distance);
        }
    }

    @Override
    public String toString() {
        return "PillarLocationCalculator{" +
                "centerX='" + centerX + '\'' +
                ", centerY='" + centerY + '\'' +
                ", directionX='" + directionX + '\'' +
                ", directionY='" + directionY + '\'' +
                '}';
    }
}
