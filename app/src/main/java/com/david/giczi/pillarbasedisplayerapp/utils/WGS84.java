package com.david.giczi.pillarbasedisplayerapp.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import hu.david.giczi.mvmxpert.wgs.ToWGS;

public class WGS84 {

    private double Y_EOV;
    private double X_EOV;
    private double Z_EOV;

    private static final double a = 6378137.0;
    private static final double b = 6356752.314;
    //private static final double f = (a - b) / a;

    private static final double e2 = ( Math.pow(a, 2) - Math.pow(b, 2) ) / Math.pow(a, 2);
    //private static final double  e_ = Math.sqrt(( Math.pow(a, 2) - Math.pow(b, 2) ) / Math.pow(b, 2));

    public static String getX(double latitude, double longitude, double altitude){
        double N = a / Math.sqrt(1 - e2 * Math.pow(Math.sin(Math.toRadians(latitude)), 2));
        double X = (N + altitude) * Math.cos(Math.toRadians(latitude))
                * Math.cos(Math.toRadians(longitude));
        return String.format(Locale.getDefault(),"%.3fm", X);
    }
    public static String getY(double latitude, double longitude, double altitude){
        double N = a / Math.sqrt(1 - e2 * Math.pow(Math.sin(Math.toRadians(latitude)), 2));
        double Y = (N + altitude) * Math.cos(Math.toRadians(latitude))
                * Math.sin(Math.toRadians(longitude));
        return String.format(Locale.getDefault(), "%.3fm", Y);
    }

    public static String getZ(double latitude, double altitude){
        double N = a / Math.sqrt(1 - e2 * Math.pow(Math.sin(Math.toRadians(latitude)), 2));
        double Z = ((1 - e2) * N + altitude) * Math.sin(Math.toRadians(latitude));
        return String.format(Locale.getDefault(), "%.3fm", Z);
    }

    public String toWGS84(String pointID, double Y_EOV, double X_EOV, double Z_EOV) {
        this.Y_EOV = Y_EOV;
        this.X_EOV = X_EOV;
        this.Z_EOV = Z_EOV;
        List<Double> WGS84 = getWGSCoordinatesByEOV();
        return pointID + "," + String.format(Locale.getDefault(),"%.6f", WGS84.get(0)).replace(",", ".")
                + "," + String.format(Locale.getDefault(),"%.6f", WGS84.get(1)).replace(",", ".");
    }

    private List<Double> getWGSCoordinatesByEOV(){
        List<Double> xyzIUGG67 = getXYZCoordinatesForIUGG67();
        new ToWGS(xyzIUGG67.get(0), xyzIUGG67.get(1), xyzIUGG67.get(2));
        return Arrays.asList(ToWGS.FI_WGS84, ToWGS.LAMBDA_WGS84, ToWGS.H_WGS84);
    }

    private List<Double> getXYZCoordinatesForIUGG67(){
        double sphereFi_ = 2 * Math.atan( Math.pow(Math.E, (X_EOV - 200000) / (EOV.R * EOV.m0))) - Math.PI / 2;
        double sphereLambda_ = (Y_EOV - 650000) / (EOV.R * EOV.m0);
        double sphereFi = Math.asin(Math.sin(sphereFi_) * Math.cos(Math.toRadians(EOV.fi_0)) +
                Math.cos(sphereFi_) * Math.sin(Math.toRadians(EOV.fi_0)) * Math.cos(sphereLambda_));
        double sphereLambda = Math.asin(Math.cos(sphereFi_) * Math.sin(sphereLambda_) / Math.cos(sphereFi));
        double FI = iterateFi(sphereFi);
        double LAMBDA = Math.toRadians(EOV.lambda_0) + sphereLambda / EOV.n;
        double N = EOV.a / Math.sqrt(1 - Math.pow(EOV.e, 2) * Math.pow(Math.sin(FI), 2));
        double X = (N + Z_EOV) * Math.cos(FI) * Math.cos(LAMBDA);
        double Y = (N + Z_EOV) * Math.cos(FI) * Math.sin(LAMBDA);
        double Z = ((1 - Math.pow(EOV.e, 2)) * N + Z_EOV) * Math.sin(FI);
        return Arrays.asList(X, Y, Z);
    }

    private double iterateFi(double preFi){
        double sphereFi = preFi;
        double Fi = preFi;
        for (int i = 0; i < 4; i++) {
            preFi =  2 * Math.atan(Math.pow(
                    Math.tan(Math.PI / 4 + sphereFi / 2) /
                            (EOV.k_EOV * Math.pow((1 - EOV.e * Math.sin(Fi)) /
                                    (1 + EOV.e * Math.sin(Fi)), EOV.n * EOV.e / 2)),
                    1 / EOV.n) ) - Math.PI / 2;
            Fi = preFi;
        }
        return Fi;
    }

}
