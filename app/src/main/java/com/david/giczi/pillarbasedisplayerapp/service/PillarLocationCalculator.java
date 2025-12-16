package com.david.giczi.pillarbasedisplayerapp.service;

import androidx.annotation.NonNull;

import com.david.giczi.pillarbasedisplayerapp.MainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PillarLocationCalculator {

    public String centerX;
    public String centerY;
    public double aveCenterX;
    public double aveCenterY;
    public String directionX;
    public String directionY;
    private Double aveDirectionX;
    private Double aveDirectionY;
    private final List<Point> centerPillarMeasData;
    private final List<Point> directionPillarMeasData;
    private Double abscissa_distance;
    private double ordinate_distance;
    private final DecimalFormat df;

    public PillarLocationCalculator() {
        centerPillarMeasData = new ArrayList<>();
        directionPillarMeasData = new ArrayList<>();
        df = new DecimalFormat("0.000");
    }

    public void addCenterPillarMeasData(String foot1Y, String foot1X, String foot2Y, String foot2X,
        String foot3Y, String foot3X, String foot4Y, String foot4X){

        if( !foot1Y.isEmpty() && !MainActivity.isInvalidInputChars(foot1Y) &&
                !foot1X.isEmpty() && !MainActivity.isInvalidInputChars(foot1X) ){
            Point foot1Point = new Point("1", Double.parseDouble(foot1Y.replace(",", ".")),
                    Double.parseDouble(foot1X.replace(",", ".")));
            centerPillarMeasData.add(foot1Point);
        }
        if( !foot2Y.isEmpty() && !MainActivity.isInvalidInputChars(foot2Y) &&
                !foot2X.isEmpty() && !MainActivity.isInvalidInputChars(foot2X) ){
            Point foot2Point = new Point("2", Double.parseDouble(foot2Y.replace(",", ".")),
                    Double.parseDouble(foot2X.replace(",", ".")));
            centerPillarMeasData.add(foot2Point);
        }
        if( !foot3Y.isEmpty() && !MainActivity.isInvalidInputChars(foot3Y) &&
                !foot3X.isEmpty() && !MainActivity.isInvalidInputChars(foot3X) ){
            Point foot3Point = new Point("3", Double.parseDouble(foot3Y.replace(",", ".")),
                    Double.parseDouble(foot3X.replace(",", ".")));
            centerPillarMeasData.add(foot3Point);
        }
        if( !foot4Y.isEmpty() && !MainActivity.isInvalidInputChars(foot4Y) &&
                !foot4X.isEmpty() && !MainActivity.isInvalidInputChars(foot4X) ){
            Point foot4Point = new Point("4", Double.parseDouble(foot4Y.replace(",", ".")),
                    Double.parseDouble(foot4X.replace(",", ".")));
            centerPillarMeasData.add(foot4Point);
        }
    }

    public void addDirectionPillarMeasData(String foot1Y, String foot1X, String foot2Y, String foot2X,
                                        String foot3Y, String foot3X, String foot4Y, String foot4X){

        if( !foot1Y.isEmpty() && !MainActivity.isInvalidInputChars(foot1Y) &&
                !foot1X.isEmpty() && !MainActivity.isInvalidInputChars(foot1X) ){
            Point foot1Point = new Point("1", Double.parseDouble(foot1Y.replace(",", ".")),
                    Double.parseDouble(foot1X.replace(",", ".")));
            directionPillarMeasData.add(foot1Point);
        }
        if( !foot2Y.isEmpty() && !MainActivity.isInvalidInputChars(foot2Y) &&
                !foot2X.isEmpty() && !MainActivity.isInvalidInputChars(foot2X) ){
            Point foot2Point = new Point("2", Double.parseDouble(foot2Y.replace(",", ".")),
                    Double.parseDouble(foot2X.replace(",", ".")));
            directionPillarMeasData.add(foot2Point);
        }
        if( !foot3Y.isEmpty() && !MainActivity.isInvalidInputChars(foot3Y) &&
                !foot3X.isEmpty() && !MainActivity.isInvalidInputChars(foot3X) ){
            Point foot3Point = new Point("3", Double.parseDouble(foot3Y.replace(",", ".")),
                    Double.parseDouble(foot3X.replace(",", ".")));
            directionPillarMeasData.add(foot3Point);
        }
        if( !foot4Y.isEmpty() && !MainActivity.isInvalidInputChars(foot4Y) &&
                !foot4X.isEmpty() && !MainActivity.isInvalidInputChars(foot4X) ){
            Point foot4Point = new Point("4", Double.parseDouble(foot4Y.replace(",", ".")),
                    Double.parseDouble(foot4X.replace(",", ".")));
            directionPillarMeasData.add(foot4Point);
        }

    }

    public void calcPillarLocationData(){

        if( !directionPillarMeasData.isEmpty() && directionPillarMeasData.size() != 3 ){
            aveDirectionX = directionPillarMeasData.stream().mapToDouble(Point::getX_coord)
                    .summaryStatistics().getAverage();
            aveDirectionY = directionPillarMeasData.stream().mapToDouble(Point::getY_coord)
                    .summaryStatistics().getAverage();
            directionX = df.format(aveDirectionX).replace(",", ".");
            directionY = df.format(aveDirectionY).replace(",", ".");
        }


        if( !centerPillarMeasData.isEmpty() && centerPillarMeasData.size() != 3 ){
            aveCenterX = centerPillarMeasData.stream().mapToDouble(Point::getX_coord)
                    .summaryStatistics().getAverage();
            aveCenterY = centerPillarMeasData.stream().mapToDouble(Point::getY_coord)
                    .summaryStatistics().getAverage();

            if( aveDirectionX != null && abscissa_distance == null ){
                Point startPoint = new Point("StartPoint", aveCenterX, aveCenterY);
                Point endPoint = new Point("EndPoint", aveDirectionX, aveDirectionY);
                AzimuthAndDistance mainLineData = new AzimuthAndDistance(startPoint, endPoint);
                Point abscissaPoint = new Point("AbscissaPoint",
                        (aveCenterX + aveDirectionX) / 2.0, (aveCenterY + aveDirectionY) / 2.0);
                if( ordinate_distance == 0 ){
                    centerX = df.format(abscissaPoint.getX_coord()).replace(",", ".");
                    centerY = df.format(abscissaPoint.getY_coord()).replace(",", ".");
                }
                else {
                    PolarPoint resultPoint = new PolarPoint(abscissaPoint, ordinate_distance,
                            ordinate_distance < 0 ? mainLineData.calcAzimuth() + Math.PI / 2.0 :
                                    mainLineData.calcAzimuth() + 3 * Math.PI / 2.0, "ResultPoint");
                    centerX = df.format(resultPoint.calcPolarPoint().getX_coord()).replace(",", ".");
                    centerY = df.format(resultPoint.calcPolarPoint().getY_coord()).replace(",", ".");
                }


            }
            else if( aveDirectionX != null ){
                Point startPoint = new Point("StartPoint", aveCenterX, aveCenterY);
                Point endPoint = new Point("EndPoint", aveDirectionX, aveDirectionY);
                AzimuthAndDistance mainLineData = new AzimuthAndDistance(startPoint, endPoint);
                PolarPoint abscissaPoint = new PolarPoint(startPoint, abscissa_distance,
                        mainLineData.calcAzimuth(), "AbscissaPoint");
                if( ordinate_distance == 0 ){
                    centerX = df.format(abscissaPoint.calcPolarPoint().getX_coord()).replace(",", ".");
                    centerY = df.format(abscissaPoint.calcPolarPoint().getY_coord()).replace(",", ".");
                }
                else{
                    PolarPoint resultPoint = new PolarPoint(abscissaPoint.calcPolarPoint(), ordinate_distance,
                            ordinate_distance < 0 ? mainLineData.calcAzimuth() + Math.PI / 2.0 :
                                    mainLineData.calcAzimuth() + 3 * Math.PI / 2.0, "ResultPoint");
                    centerX = df.format(resultPoint.calcPolarPoint().getX_coord()).replace(",", ".");
                    centerY = df.format(resultPoint.calcPolarPoint().getY_coord()).replace(",", ".");
                }
            }
            else if( centerPillarMeasData.size() == 4 && abscissa_distance != null ) {
                aveDirectionX = (centerPillarMeasData.get(1).getX_coord() + centerPillarMeasData.get(2).getX_coord()) / 2.0;
                aveDirectionY = (centerPillarMeasData.get(1).getY_coord() + centerPillarMeasData.get(2).getY_coord()) / 2.0;
                Point startPoint = new Point("StartPoint", aveCenterX, aveCenterY);
                Point endPoint = new Point("EndPoint", aveDirectionX, aveDirectionY);
                AzimuthAndDistance mainLineData = new AzimuthAndDistance(startPoint, endPoint);
                PolarPoint abscissaPoint = new PolarPoint(startPoint, abscissa_distance,
                        mainLineData.calcAzimuth(), "AbscissaPoint");
                if( ordinate_distance == 0 ){
                    centerX = df.format(abscissaPoint.calcPolarPoint().getX_coord()).replace(",", ".");
                    centerY = df.format(abscissaPoint.calcPolarPoint().getY_coord()).replace(",", ".");
                }
                else{
                    PolarPoint resultPoint = new PolarPoint(abscissaPoint.calcPolarPoint(), ordinate_distance,
                            ordinate_distance < 0 ? mainLineData.calcAzimuth() + Math.PI / 2.0 :
                                    mainLineData.calcAzimuth() + 3 * Math.PI / 2.0, "ResultPoint");
                    centerX = df.format(resultPoint.calcPolarPoint().getX_coord()).replace(",", ".");
                    centerY = df.format(resultPoint.calcPolarPoint().getY_coord()).replace(",", ".");
                }
                directionX = df.format(abscissa_distance == 0 ? aveDirectionX : aveCenterX).replace(",", ".");
                directionY = df.format(abscissa_distance == 0 ? aveDirectionY : aveCenterY).replace(",", ".");
            }
        }
        else if( centerPillarMeasData.size() == 3 && abscissa_distance != null ){
            aveCenterX = (centerPillarMeasData.get(0).getX_coord() + centerPillarMeasData.get(2).getX_coord()) / 2.0;
            aveCenterY = (centerPillarMeasData.get(0).getY_coord() + centerPillarMeasData.get(2).getY_coord()) / 2.0;
            if( aveDirectionX == null ){
                aveDirectionX = (centerPillarMeasData.get(1).getX_coord() + centerPillarMeasData.get(2).getX_coord()) / 2.0;
            }
            if( aveDirectionY == null ){
                aveDirectionY = (centerPillarMeasData.get(1).getY_coord() + centerPillarMeasData.get(2).getY_coord()) / 2.0;
            }
            Point startPoint = new Point("StartPoint", aveCenterX, aveCenterY);
            Point endPoint = new Point("EndPoint", aveDirectionX, aveDirectionY);
            AzimuthAndDistance mainLineData = new AzimuthAndDistance(startPoint, endPoint);
            PolarPoint abscissaPoint = new PolarPoint(startPoint, abscissa_distance,
                    mainLineData.calcAzimuth(), "AbscissaPoint");
            if( ordinate_distance == 0 ){
                centerX = df.format(abscissaPoint.calcPolarPoint().getX_coord()).replace(",", ".");
                centerY = df.format(abscissaPoint.calcPolarPoint().getY_coord()).replace(",", ".");
            }
            else{
                PolarPoint resultPoint = new PolarPoint(abscissaPoint.calcPolarPoint(), ordinate_distance,
                        ordinate_distance < 0 ? mainLineData.calcAzimuth() + Math.PI / 2.0 :
                                mainLineData.calcAzimuth() + 3 * Math.PI / 2.0, "ResultPoint");
                centerX = df.format(resultPoint.calcPolarPoint().getX_coord()).replace(",", ".");
                centerY = df.format(resultPoint.calcPolarPoint().getY_coord()).replace(",", ".");
            }
            directionX = df.format(abscissa_distance == 0 ? aveDirectionX : aveCenterX).replace(",", ".");
            directionY = df.format(abscissa_distance == 0 ? aveDirectionY : aveCenterY).replace(",", ".");
        }

    }

    public void setAbscissa_distance(String abscissa_distance) {
        if( !abscissa_distance.isEmpty() && !MainActivity.isInvalidInputChars(abscissa_distance)){
            this.abscissa_distance = Double.parseDouble(abscissa_distance);
        }
    }

    public void setOrdinate_distance(String ordinate_distance) {
        if( !ordinate_distance.isEmpty() && !MainActivity.isInvalidInputChars(ordinate_distance)){
            this.ordinate_distance = Double.parseDouble(ordinate_distance);
        }
    }

    public String getOrdinateAsString(Point startPoint, Point endPoint, Point basePoint){
        double ordinate = getOrdinateValue(startPoint, endPoint, basePoint);
        return (ordinate > 0 ? "+" : "") +
                String.format(Locale.getDefault(),"%.3fm", ordinate)
                        .replace(",", ".") + " " +
                getOrdinateErrorMargin(basePoint);
    }
    public String getAbscissaAsString(Point startPoint, Point endPoint, Point basePoint){
        double abscissa = getAbscissaValue(startPoint, endPoint, basePoint);
        return  (abscissa > 0 ? "+" :  "")  +
                String.format(Locale.getDefault(), "%.3fm", abscissa)
                        .replace(",", ".") + " " +
                getAbscissaErrorMargin(basePoint);
    }
    private double getOrdinateValue(Point startPoint, Point endPoint, Point basePoint){
        if( startPoint.equals(endPoint) ){
            return Double.NaN;
        }
       else if( startPoint.equals(basePoint) ){
            return this.ordinate_distance;
        }
        double alfa = new AzimuthAndDistance(startPoint, endPoint).calcAzimuth() -
                new AzimuthAndDistance(startPoint, basePoint).calcAzimuth();
        double distance = new AzimuthAndDistance(startPoint, basePoint).calcDistance();

        return Math.sin(alfa) * distance;
    }

    private double getAbscissaValue(Point startPoint, Point endPoint, Point basePoint) {
        if( startPoint.equals(endPoint) ){
            return Double.NaN;
        }
        else if( startPoint.equals(basePoint) ){
            return 0d;
        }
        double alfa = new AzimuthAndDistance(startPoint, endPoint).calcAzimuth() -
                new AzimuthAndDistance(startPoint, basePoint).calcAzimuth();
        double distance = new AzimuthAndDistance(startPoint, basePoint).calcDistance();

        if( this.abscissa_distance == null ){
            return new AzimuthAndDistance(startPoint, endPoint).calcDistance() / 2.0
                    - Math.cos(alfa) * distance;
        }
        return Math.cos(alfa) * distance;
    }

    private String getAbscissaErrorMargin(Point basePoint){
        Point measEndPoint = new Point("MeasEndPoint", aveDirectionX, aveDirectionY);
        double lengthOfMainLine = new AzimuthAndDistance(basePoint, measEndPoint).calcDistance();
        return "|" + String.format(Locale.getDefault(),"%.1f",lengthOfMainLine / 4.0)
                .replace("," , ".") + "cm|";
    }
    private String getOrdinateErrorMargin(Point basePoint){
        Point measEndPoint = new Point("MeasEndPoint", aveDirectionX, aveDirectionY);
        double lengthOfMainLine =  new AzimuthAndDistance(basePoint, measEndPoint).calcDistance();
        return "|" + String.format(Locale.getDefault(),"%.1f",3 * lengthOfMainLine / 10)
                .replace(",", ".") + "cm|";
    }

    public boolean isOkAbscissaValue(Point startPoint, Point endPoint, Point basePoint){
        double lengthOfMainLine =  new AzimuthAndDistance(startPoint, endPoint).calcDistance();
        return 2.5 * lengthOfMainLine / 1000 >= Math.abs(getAbscissaValue(startPoint, endPoint, basePoint));
    }

    public boolean isOkOrdinateValue(Point startPoint, Point endPoint, Point basePoint){
        double lengthOfMainLine = new AzimuthAndDistance(startPoint, endPoint).calcDistance();
        return 3 * lengthOfMainLine / 1000 >= Math.abs(getOrdinateValue(startPoint, endPoint, basePoint));
    }

    @NonNull
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
