package com.david.giczi.pillarbasedisplayerapp.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.david.giczi.pillarbasedisplayerapp.MainActivity;
import com.david.giczi.pillarbasedisplayerapp.R;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentBaseBinding;
import com.david.giczi.pillarbasedisplayerapp.service.AzimuthAndDistance;
import com.david.giczi.pillarbasedisplayerapp.service.Point;
import com.david.giczi.pillarbasedisplayerapp.service.PolarPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class PillarBaseFragment extends Fragment {

    private FragmentBaseBinding fragmentBaseBinding;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private static float X_CENTER;
    private static float Y_CENTER;
    private static float MM;
    private static float SCALE;
    private static List<Point> transformedPillarBasePoints;

    private static final List<String> LETTERS = Arrays.asList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
    "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentBaseBinding = FragmentBaseBinding.inflate(inflater, container, false);
        MainActivity.MENU.findItem(R.id.weight_base).setEnabled(false);
        MainActivity.MENU.findItem(R.id.plate_base).setEnabled(false);
        PillarBaseFragment.X_CENTER = getResources().getDisplayMetrics().widthPixels / 2F;
        PillarBaseFragment.Y_CENTER = getResources().getDisplayMetrics().heightPixels / 2F;
        PillarBaseFragment.MM = (float) (Math.sqrt(Math.pow(getResources().getDisplayMetrics().widthPixels, 2) +
                Math.pow(getResources().getDisplayMetrics().heightPixels, 2)) / 140F);
        this.bitmap = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(bitmap);
        this.paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawColor(Color.WHITE);
        fragmentBaseBinding.drawingBase.setImageBitmap(bitmap);
        MainActivity.PAGE_COUNTER = 4;

        if(MainActivity.PILLAR_BASE_COORDINATES != null ){
            setScaleValue();
            transformPillarBasePoints();
            addNorthSign();
            drawCircleForPoints();
            drawFindPointCircle();
            drawTextsForPillarBase();
            drawMainLineDirections();
            if( MainActivity.IS_WEIGHT_BASE ){
                drawPillarBaseHoleForWeightBase();
                drawPillarAxesForWeightBase();
                drawLegNameForWeightBase();
            }
            else {
                drawPillarBaseHoleForPlateBase();
                drawPillarAxesForPlateBase();
                drawLegNameForPlateBase();
            }
        }
        if( MainActivity.northPoleWindow != null ){
            MainActivity
            .northPoleWindow.showAtLocation(((MainActivity) requireActivity()).binding.getRoot(), Gravity.CENTER, 0, - 630 );
        }
        if( MainActivity.gpsDataWindow != null ){
            MainActivity.gpsDataWindow
            .showAtLocation(((MainActivity) requireActivity()).binding.getRoot(), Gravity.CENTER, 0, 800);
        }
        return fragmentBaseBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setScaleValue(){
        if( MainActivity.IS_WEIGHT_BASE ){
            if( Double.parseDouble(MainActivity.BASE_DATA.get(8)) < 5 ||
                    Double.parseDouble(MainActivity.BASE_DATA.get(9)) < 5){
                SCALE = 150F;
            }
            else if(Double.parseDouble(MainActivity.BASE_DATA.get(8)) >= 5 &&
                    Double.parseDouble(MainActivity.BASE_DATA.get(8)) < 9){
                SCALE = 400F;
            }
            else if(Double.parseDouble(MainActivity.BASE_DATA.get(9)) >= 5 &&
                    Double.parseDouble(MainActivity.BASE_DATA.get(9)) < 9){
                SCALE = 400F;
            }
            else if( Double.parseDouble(MainActivity.BASE_DATA.get(8)) >= 9 ||
                    Double.parseDouble(MainActivity.BASE_DATA.get(9)) >= 9){
                SCALE = 500F;
            }
        }
        else {

            if( Double.parseDouble(MainActivity.BASE_DATA.get(7)) < 5 ||
                    Double.parseDouble(MainActivity.BASE_DATA.get(8)) < 5){
                SCALE = 150F;
            }
            else if(Double.parseDouble(MainActivity.BASE_DATA.get(7)) >= 5 &&
                    Double.parseDouble(MainActivity.BASE_DATA.get(7)) <= 10){
                SCALE = 400F;
            }
            else if(Double.parseDouble(MainActivity.BASE_DATA.get(8)) >= 5 &&
                    Double.parseDouble(MainActivity.BASE_DATA.get(8)) <= 10){
                SCALE = 400F;
            }
            else if( Double.parseDouble(MainActivity.BASE_DATA.get(7)) > 10 ||
                    Double.parseDouble(MainActivity.BASE_DATA.get(8)) >10){
                SCALE = 500F;
            }
        }
    }

    private void addNorthSign(){
        Bitmap northSignResource = BitmapFactory.decodeResource(getResources(), R.drawable.north);
        Matrix matrix = new Matrix();
        matrix.postScale(0.25F, 0.25F);
        Bitmap northSign = Bitmap.createBitmap(northSignResource, 0, 0,
                northSignResource.getWidth(), northSignResource.getHeight(), matrix, false);
        canvas.drawBitmap(northSign, (canvas.getWidth() - northSign.getWidth()) / 2f, 6 * MM, paint);
    }

    private void drawCircleForPoints() {
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float) transformedPillarBasePoints.get(0).getX_coord(),
                (float) transformedPillarBasePoints.get(0).getY_coord(), 1.2F * MM, paint);
        drawPillarBasePointId(transformedPillarBasePoints.get(0));
        paint.setColor(Color.MAGENTA);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4F);
        for (int i = 1; i < transformedPillarBasePoints.size() - 1; i++) {
            canvas.drawCircle((float) transformedPillarBasePoints.get(i).getX_coord(),
                    (float) transformedPillarBasePoints.get(i).getY_coord(), 0.75F * MM, paint);
        }
    }

    private void drawFindPointCircle(){
        paint.setColor(ContextCompat.getColor(requireContext(), R.color.green));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float) transformedPillarBasePoints
                        .get(MainActivity.FIND_POINT_INDEX).getX_coord(),
                (float) transformedPillarBasePoints
                        .get(MainActivity.FIND_POINT_INDEX).getY_coord(),
                1.2F * MM, paint);
    }


    private void drawPillarBasePointId(Point pillarBasePoint){
        paint.setColor(ContextCompat.getColor(requireContext(), R.color.red));
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(40);
        String[] idValues = pillarBasePoint.getPointID().split("\\s+");
        canvas.drawText((idValues.length == 2 ? idValues[0] + idValues[1] :
                        pillarBasePoint.getPointID()), (float) pillarBasePoint.getX_coord(),
                (float) pillarBasePoint.getY_coord() - 2 * MM, paint);
    }
    private void drawTextsForPillarBase(){
        Point centerPoint = MainActivity.PILLAR_BASE_COORDINATES.get(0);
        Point directionPoint = MainActivity.PILLAR_BASE_COORDINATES
                .get(MainActivity.PILLAR_BASE_COORDINATES.size() - 1);
        AzimuthAndDistance mainLineDistance = new AzimuthAndDistance(centerPoint, directionPoint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(40);
        canvas.drawText("M= 1:" + (int) SCALE,
                3 * MM, getResources().getDisplayMetrics().heightPixels - 8 * MM, paint);
        String[] centerIdValues = centerPoint.getPointID().split("\\s+");
        String[] directionIdValues = directionPoint.getPointID().split("\\s+");
        canvas.drawText((centerIdValues.length == 2 ?
                        centerIdValues[0] + centerIdValues[1] : centerPoint.getPointID() )+ ". és "
                        + (directionIdValues.length == 2 ?
                       directionIdValues[0] + directionIdValues[1] : directionPoint.getPointID())
                        + ". oszlopok távolsága: " +
        String.format(Locale.getDefault(), "%.3fm", mainLineDistance.calcDistance()).replace(",", "."),
               3 * MM, getResources().getDisplayMetrics().heightPixels - 3 * MM, paint);
        paint.setColor(ContextCompat.getColor(requireContext(), R.color.firebrick));
        for(int i = 1; i < transformedPillarBasePoints.size() - 1; i++){
            String[] idValues = transformedPillarBasePoints.get(i).getPointID().split("\\s+");
            canvas.drawText((idValues.length == 2 ? idValues[0] + idValues[1]  :
                            transformedPillarBasePoints.get(i).getPointID()),
                    (float)  (transformedPillarBasePoints.get(i).getX_coord() - 2 * MM),
                    (float) (transformedPillarBasePoints.get(i).getY_coord() - 2 * MM), paint);
        }
    }
    private boolean isPillarIdIncreased(){
        String[] centerIdValues = transformedPillarBasePoints.get(0).getPointID().split("\\s+");
        String[] directionIdValues = transformedPillarBasePoints
                .get(transformedPillarBasePoints.size() - 1)
                .getPointID().split("\\s+");

        try{

          if(centerIdValues.length == 1 && directionIdValues.length == 1 &&
                  Integer.parseInt(directionIdValues[0]) >= Integer.parseInt(centerIdValues[0]) ){
              return true;
          }
        }
        catch (NumberFormatException ignored){
        }
        try{

            if( centerIdValues.length == 2 && directionIdValues.length == 2 &&
                    directionIdValues[1].charAt(0) >= centerIdValues[1].charAt(0)){
                return true;
            }
        }
        catch (NumberFormatException ignored){

        }

        return false;
    }
    private void drawLegNameForPlateBase(){
        paint.setColor(ContextCompat.getColor(requireContext(), R.color.red));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(100);
        if( isPillarIdIncreased() ){
            AzimuthAndDistance dataA =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(0), transformedPillarBasePoints.get(1));
            PolarPoint posA = new PolarPoint(transformedPillarBasePoints.get(0),
                    dataA.calcDistance() / 2, dataA.calcAzimuth(), "A");
            canvas.drawText("A", (float) posA.calcPolarPoint().getX_coord(),
                    (float) posA.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataB =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(0), transformedPillarBasePoints.get(2));
            PolarPoint posB = new PolarPoint(transformedPillarBasePoints.get(0),
                    dataB.calcDistance() / 2, dataB.calcAzimuth(), "B");
            canvas.drawText("B", (float) posB.calcPolarPoint().getX_coord(),
                    (float) posB.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataC =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(0), transformedPillarBasePoints.get(3));
            PolarPoint posC = new PolarPoint(transformedPillarBasePoints.get(0),
                    dataC.calcDistance() / 2, dataC.calcAzimuth(), "C");
            canvas.drawText("C", (float) posC.calcPolarPoint().getX_coord(),
                    (float) posC.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataD =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(0), transformedPillarBasePoints.get(4));
            PolarPoint posD = new PolarPoint(transformedPillarBasePoints.get(0),
                    dataD.calcDistance() / 2, dataD.calcAzimuth(), "D");
            canvas.drawText("D", (float) posD.calcPolarPoint().getX_coord(),
                    (float) posD.calcPolarPoint().getY_coord(), paint);
        }
        else {
            AzimuthAndDistance dataA =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(0), transformedPillarBasePoints.get(3));
            PolarPoint posA = new PolarPoint(transformedPillarBasePoints.get(0),
                    dataA.calcDistance() / 2, dataA.calcAzimuth(), "A");
            canvas.drawText("A", (float) posA.calcPolarPoint().getX_coord(),
                    (float) posA.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataB =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(0), transformedPillarBasePoints.get(4));
            PolarPoint posB = new PolarPoint(transformedPillarBasePoints.get(0),
                    dataB.calcDistance() / 2, dataB.calcAzimuth(), "B");
            canvas.drawText("B", (float) posB.calcPolarPoint().getX_coord(),
                    (float) posB.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataC =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(0), transformedPillarBasePoints.get(1));
            PolarPoint posC = new PolarPoint(transformedPillarBasePoints.get(0),
                    dataC.calcDistance() / 2, dataC.calcAzimuth(), "C");
            canvas.drawText("C", (float) posC.calcPolarPoint().getX_coord(),
                    (float) posC.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataD =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(0), transformedPillarBasePoints.get(2));
            PolarPoint posD = new PolarPoint(transformedPillarBasePoints.get(0),
                    dataD.calcDistance() / 2, dataD.calcAzimuth(), "D");
            canvas.drawText("D", (float) posD.calcPolarPoint().getX_coord(),
                    (float) posD.calcPolarPoint().getY_coord(), paint);
        }
    }

    private void drawLegNameForWeightBase(){
        paint.setColor(ContextCompat.getColor(requireContext(), R.color.red));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(100);
        if( isPillarIdIncreased() ){
            AzimuthAndDistance dataA =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(9), transformedPillarBasePoints.get(11));
            PolarPoint posA = new PolarPoint(transformedPillarBasePoints.get(9),
                    dataA.calcDistance() / 2, dataA.calcAzimuth(), "A");
            canvas.drawText("A", (float) posA.calcPolarPoint().getX_coord(),
                    (float) posA.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataB =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(23), transformedPillarBasePoints.get(21));
            PolarPoint posB = new PolarPoint(transformedPillarBasePoints.get(23),
                    dataB.calcDistance() / 2, dataB.calcAzimuth(), "B");
            canvas.drawText("B", (float) posB.calcPolarPoint().getX_coord(),
                    (float) posB.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataC =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(20), transformedPillarBasePoints.get(18));
            PolarPoint posC = new PolarPoint(transformedPillarBasePoints.get(20),
                    dataC.calcDistance() / 2, dataC.calcAzimuth(), "C");
            canvas.drawText("C", (float) posC.calcPolarPoint().getX_coord(),
                    (float) posC.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataD =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(13), transformedPillarBasePoints.get(15));
            PolarPoint posD = new PolarPoint(transformedPillarBasePoints.get(13),
                    dataD.calcDistance() / 2, dataD.calcAzimuth(), "D");
            canvas.drawText("D", (float) posD.calcPolarPoint().getX_coord(),
                    (float) posD.calcPolarPoint().getY_coord(), paint);
        }
        else {
            AzimuthAndDistance dataA =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(20), transformedPillarBasePoints.get(18));
            PolarPoint posA = new PolarPoint(transformedPillarBasePoints.get(20),
                    dataA.calcDistance() / 2, dataA.calcAzimuth(), "A");
            canvas.drawText("A", (float) posA.calcPolarPoint().getX_coord(),
                    (float) posA.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataB =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(13), transformedPillarBasePoints.get(15));
            PolarPoint posB = new PolarPoint(transformedPillarBasePoints.get(13),
                    dataB.calcDistance() / 2, dataB.calcAzimuth(), "B");
            canvas.drawText("B", (float) posB.calcPolarPoint().getX_coord(),
                    (float) posB.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataC =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(9), transformedPillarBasePoints.get(11));
            PolarPoint posC = new PolarPoint(transformedPillarBasePoints.get(9),
                    dataC.calcDistance() / 2, dataC.calcAzimuth(), "C");
            canvas.drawText("C", (float) posC.calcPolarPoint().getX_coord(),
                    (float) posC.calcPolarPoint().getY_coord(), paint);
            AzimuthAndDistance dataD =
                    new AzimuthAndDistance(transformedPillarBasePoints.get(23), transformedPillarBasePoints.get(21));
            PolarPoint posD = new PolarPoint(transformedPillarBasePoints.get(23),
                    dataD.calcDistance() / 2, dataD.calcAzimuth(), "D");
            canvas.drawText("D", (float) posD.calcPolarPoint().getX_coord(),
                    (float) posD.calcPolarPoint().getY_coord(), paint);
        }
    }

    private void drawMainLineDirections(){
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
       switch ( transformedPillarBasePoints.size() ){
           case 10:
               drawMainLineDirectionForNormalPlateBase();
               break;
           case 12:
               drawMainLineDirectionForRotatedPlateBase();
               break;
           case 26:
               drawMainLineDirectionForNormalWeightBase();
               break;
           case 28:
               drawMainLineDirectionForRotatedWeightBase();
           default:
       }
    }

    private void drawMainLineDirectionForNormalPlateBase(){
        paint.setColor(Color.MAGENTA);
        AzimuthAndDistance mainLineData =
                new AzimuthAndDistance(transformedPillarBasePoints.get(0),
                        transformedPillarBasePoints.get(6));
        PolarPoint startPoint = new PolarPoint(transformedPillarBasePoints.get(6),
                1.5F * MM, mainLineData.calcAzimuth(), "startForwardDirection");
        PolarPoint endPoint = new PolarPoint(transformedPillarBasePoints.get(6),
                10 * MM, mainLineData.calcAzimuth(),
                transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 1).getPointID());
        canvas.drawLine(
                (float) startPoint.calcPolarPoint().getX_coord(),
                (float) startPoint.calcPolarPoint().getY_coord(),
                (float) endPoint.calcPolarPoint().getX_coord(),
                (float) endPoint.calcPolarPoint().getY_coord(), paint);
        drawArrow(transformedPillarBasePoints.get(0), endPoint.calcPolarPoint());
        drawPillarBasePointId(endPoint.calcPolarPoint());
    }

    private void drawMainLineDirectionForNormalWeightBase(){
        paint.setColor(Color.MAGENTA);
        AzimuthAndDistance mainLineData =
                new AzimuthAndDistance(transformedPillarBasePoints.get(0),
                        transformedPillarBasePoints.get(1));
        PolarPoint startPoint = new PolarPoint(transformedPillarBasePoints.get(1),
                1.5F * MM, mainLineData.calcAzimuth(), "startForwardDirection");
        PolarPoint endPoint = new PolarPoint(startPoint.calcPolarPoint(),
                10 * MM, mainLineData.calcAzimuth(),
                transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 1).getPointID());
        canvas.drawLine(
                (float) startPoint.calcPolarPoint().getX_coord(),
                (float) startPoint.calcPolarPoint().getY_coord(),
                (float) endPoint.calcPolarPoint().getX_coord(),
                (float) endPoint.calcPolarPoint().getY_coord(), paint);
        drawArrow(transformedPillarBasePoints.get(0), endPoint.calcPolarPoint());
        drawPillarBasePointId(endPoint.calcPolarPoint());
    }

    private void drawMainLineDirectionForRotatedPlateBase(){
        paint.setColor(Color.MAGENTA);
        AzimuthAndDistance mainLineData = new AzimuthAndDistance(transformedPillarBasePoints.get(0),
                transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 3));
        PolarPoint startPoint = new PolarPoint(transformedPillarBasePoints.get(0), 1.5 * MM,
                mainLineData.calcAzimuth(), "startForwardDirectionPoint");
        PolarPoint endPoint = new PolarPoint(startPoint.calcPolarPoint(), 30 * MM,
                mainLineData.calcAzimuth(),
                transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 1).getPointID());
        canvas.drawLine(
                (float) startPoint.calcPolarPoint().getX_coord(),
                (float) startPoint.calcPolarPoint().getY_coord(),
                (float) endPoint.calcPolarPoint().getX_coord(),
                (float) endPoint.calcPolarPoint().getY_coord(), paint);
        drawArrow(transformedPillarBasePoints.get(0), endPoint.calcPolarPoint());
        drawPillarBasePointId(endPoint.calcPolarPoint());
        paint.setColor(Color.MAGENTA);
        mainLineData = new AzimuthAndDistance(transformedPillarBasePoints.get(0),
                transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 2));
        startPoint = new PolarPoint(transformedPillarBasePoints.get(0), 1.5 * MM,
                mainLineData.calcAzimuth(), "startBackwardDirectionPoint");
        endPoint = new PolarPoint(startPoint.calcPolarPoint(), 30 * MM,
                mainLineData.calcAzimuth(), getBackwardDirectionPointId());
        canvas.drawLine(
                (float) startPoint.calcPolarPoint().getX_coord(),
                (float) startPoint.calcPolarPoint().getY_coord(),
                (float) endPoint.calcPolarPoint().getX_coord(),
                (float) endPoint.calcPolarPoint().getY_coord(), paint);
        drawArrow(transformedPillarBasePoints.get(0), endPoint.calcPolarPoint());
        drawPillarBasePointId(endPoint.calcPolarPoint());
    }

    private void drawMainLineDirectionForRotatedWeightBase(){
    paint.setColor(Color.MAGENTA);
    AzimuthAndDistance mainLineData = new AzimuthAndDistance(transformedPillarBasePoints.get(0),
            transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 3));
    PolarPoint startPoint = new PolarPoint(transformedPillarBasePoints.get(0), 1.5 * MM,
            mainLineData.calcAzimuth(), "startForwardDirectionPoint");
    PolarPoint endPoint = new PolarPoint(startPoint.calcPolarPoint(), 30 * MM,
                mainLineData.calcAzimuth(),
            transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 1).getPointID());
    canvas.drawLine(
            (float) startPoint.calcPolarPoint().getX_coord(),
            (float) startPoint.calcPolarPoint().getY_coord(),
            (float) endPoint.calcPolarPoint().getX_coord(),
            (float) endPoint.calcPolarPoint().getY_coord(), paint);
    drawArrow(transformedPillarBasePoints.get(0), endPoint.calcPolarPoint());
    drawPillarBasePointId(endPoint.calcPolarPoint());
        paint.setColor(Color.MAGENTA);
        mainLineData = new AzimuthAndDistance(transformedPillarBasePoints.get(0),
                transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 2));
        startPoint = new PolarPoint(transformedPillarBasePoints.get(0), 1.5 * MM,
                mainLineData.calcAzimuth(), "startBackwardDirectionPoint");
        endPoint = new PolarPoint(startPoint.calcPolarPoint(), 30 * MM,
                mainLineData.calcAzimuth(), getBackwardDirectionPointId());
        canvas.drawLine(
                (float) startPoint.calcPolarPoint().getX_coord(),
                (float) startPoint.calcPolarPoint().getY_coord(),
                (float) endPoint.calcPolarPoint().getX_coord(),
                (float) endPoint.calcPolarPoint().getY_coord(), paint);
        drawArrow(transformedPillarBasePoints.get(0), endPoint.calcPolarPoint());
        drawPillarBasePointId(endPoint.calcPolarPoint());
    }

    private void drawArrow(Point directionPoint, Point arrowLocationPoint){
        paint.setColor(Color.MAGENTA);
        AzimuthAndDistance directionLineData = new AzimuthAndDistance(arrowLocationPoint, directionPoint);
        PolarPoint slavePoint1 = new PolarPoint(arrowLocationPoint, 2 * MM,
                directionLineData.calcAzimuth() + Math.PI / 6, "arrow1");
        PolarPoint slavePoint2 = new PolarPoint(arrowLocationPoint, 2 * MM,
                directionLineData.calcAzimuth() - Math.PI / 6, "arrow2");
        canvas.drawLine(
                (float) arrowLocationPoint.getX_coord(),
                (float) arrowLocationPoint.getY_coord(),
                (float) slavePoint1.calcPolarPoint().getX_coord(),
                (float) slavePoint1.calcPolarPoint().getY_coord(), paint);
        canvas.drawLine(
                (float) arrowLocationPoint.getX_coord(),
                (float) arrowLocationPoint.getY_coord(),
                (float) slavePoint2.calcPolarPoint().getX_coord(),
                (float) slavePoint2.calcPolarPoint().getY_coord(), paint);
    }

    private String getBackwardDirectionPointId(){
        String[] centerIdValues = transformedPillarBasePoints.get(0).getPointID().split("\\s+");
        String[] directionIdValues = transformedPillarBasePoints
                .get(transformedPillarBasePoints.size() - 1).getPointID().split("\\s+");
        int centerPointId = 0;
        int directionPointId = 1;
        try {
            if( centerIdValues.length == 1 ){
                centerPointId = Integer.parseInt(centerIdValues[0]);
            }
            if( directionIdValues.length == 1 ) {
                directionPointId = Integer.parseInt(directionIdValues[0]);
            }
         } catch (NumberFormatException e){
            return directionPointId >= centerPointId ?
                    String.valueOf( centerPointId - 1) : String.valueOf( centerPointId + 1);
    }

        try {
            if( centerIdValues.length == 2 ){
                centerPointId = Integer.parseInt(centerIdValues[0]);
            }
            if( directionIdValues.length == 2){
                directionPointId = Integer.parseInt(directionIdValues[0]);
            }
            if( centerIdValues.length == 2 && LETTERS.indexOf(centerIdValues[1]) == 0 ){
                return "-1";
            }
            if( centerIdValues.length == 2 && !LETTERS.contains(centerIdValues[1].toUpperCase())){
                throw new NumberFormatException();
            }
            if( directionIdValues.length == 2 && !LETTERS.contains(directionIdValues[1].toUpperCase())){
                throw new NumberFormatException();
            }
            if(centerIdValues.length == 2 && directionIdValues.length == 2 &&
                    LETTERS.indexOf(directionIdValues[1].toUpperCase()) >= LETTERS.indexOf(centerIdValues[1].toUpperCase())){
               return centerIdValues[0] + " " + LETTERS.get(LETTERS.indexOf(centerIdValues[1].toUpperCase()) - 1);
           }
            else if(centerIdValues.length == 2 && directionIdValues.length == 2 &&
                    LETTERS.indexOf(directionIdValues[1].toUpperCase()) < LETTERS.indexOf(centerIdValues[1].toUpperCase()) &&
                    LETTERS.indexOf(centerIdValues[1].toUpperCase()) + 1 < LETTERS.size()) {
                return centerIdValues[0] + " " + LETTERS.get(LETTERS.indexOf(centerIdValues[1].toUpperCase()) + 1);
            }
        }catch (NumberFormatException ignored){
        }
        try {
            if (centerIdValues.length == 2) {
                centerPointId = Integer.parseInt(centerIdValues[1]);
            }
            if (directionIdValues.length == 2) {
                directionPointId = Integer.parseInt(directionIdValues[1]);
            }
            if(centerIdValues.length == 2 && directionIdValues.length == 2 &&
                    directionPointId >= centerPointId ){
                return centerIdValues[0] + " " + (centerPointId - 1);
            }
            else if(centerIdValues.length == 2 && directionIdValues.length == 2){
                return centerIdValues[0] + " " + (centerPointId + 1);
            }
        }catch (NumberFormatException ignored){
        }

        return directionPointId >= centerPointId ?
                String.valueOf( centerPointId - 1) : String.valueOf( centerPointId + 1);
    }

    private void transformPillarBasePoints(){
        transformedPillarBasePoints = new ArrayList<>();
        double X = MainActivity.PILLAR_BASE_COORDINATES.get(0).getX_coord();
        double Y = MainActivity.PILLAR_BASE_COORDINATES.get(0).getY_coord();
        for (Point pillarBasePoint : MainActivity.PILLAR_BASE_COORDINATES) {
            Point transformedPoint = new Point(pillarBasePoint.getPointID(),
                    X_CENTER + ((pillarBasePoint.getX_coord() - X) * 1000.0 * MM) / SCALE,
                     Y_CENTER - ((pillarBasePoint.getY_coord() - Y) * 1000.0 * MM)  / SCALE);
            transformedPillarBasePoints.add(transformedPoint);
        }
    }

    private void drawPillarAxesForPlateBase(){
        paint.setColor(ContextCompat.getColor(requireContext(), R.color.red));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
        paint.setPathEffect(new DashPathEffect(new float[] {30f,20f}, 0f));
        canvas.drawLine((float) transformedPillarBasePoints.get(5).getX_coord(),
                (float) transformedPillarBasePoints.get(5).getY_coord(),
                (float) transformedPillarBasePoints.get(7).getX_coord(),
                (float) transformedPillarBasePoints.get(7).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(6).getX_coord(),
                (float) transformedPillarBasePoints.get(6).getY_coord(),
                (float) transformedPillarBasePoints.get(8).getX_coord(),
                (float) transformedPillarBasePoints.get(8).getY_coord(), paint);
        fragmentBaseBinding.drawingBase.setImageBitmap(bitmap);
    }

    private void drawPillarBaseHoleForPlateBase(){
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
        canvas.drawLine((float) transformedPillarBasePoints.get(1).getX_coord(),
                (float) transformedPillarBasePoints.get(1).getY_coord(),
                (float) transformedPillarBasePoints.get(2).getX_coord(),
                (float) transformedPillarBasePoints.get(2).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(2).getX_coord(),
                (float) transformedPillarBasePoints.get(2).getY_coord(),
                (float) transformedPillarBasePoints.get(3).getX_coord(),
                (float) transformedPillarBasePoints.get(3).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(3).getX_coord(),
                (float) transformedPillarBasePoints.get(3).getY_coord(),
                (float) transformedPillarBasePoints.get(4).getX_coord(),
                (float) transformedPillarBasePoints.get(4).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(4).getX_coord(),
                (float) transformedPillarBasePoints.get(4).getY_coord(),
                (float) transformedPillarBasePoints.get(1).getX_coord(),
                (float) transformedPillarBasePoints.get(1).getY_coord(), paint);
        fragmentBaseBinding.drawingBase.setImageBitmap(bitmap);
    }

    private void drawPillarAxesForWeightBase(){
        paint.setColor(ContextCompat.getColor(requireContext(), R.color.red));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
        paint.setPathEffect(new DashPathEffect(new float[] {30f,20f}, 0f));
        canvas.drawLine((float) transformedPillarBasePoints.get(1).getX_coord(),
                (float) transformedPillarBasePoints.get(1).getY_coord(),
                (float) transformedPillarBasePoints.get(3).getX_coord(),
                (float) transformedPillarBasePoints.get(3).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(2).getX_coord(),
                (float) transformedPillarBasePoints.get(2).getY_coord(),
                (float) transformedPillarBasePoints.get(4).getX_coord(),
                (float) transformedPillarBasePoints.get(4).getY_coord(), paint);
        fragmentBaseBinding.drawingBase.setImageBitmap(bitmap);
    }

    private void drawPillarBaseHoleForWeightBase(){
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
        //hole A
        canvas.drawLine((float) transformedPillarBasePoints.get(9).getX_coord(),
                (float) transformedPillarBasePoints.get(9).getY_coord(),
                (float) transformedPillarBasePoints.get(10).getX_coord(),
                (float) transformedPillarBasePoints.get(10).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(10).getX_coord(),
                (float) transformedPillarBasePoints.get(10).getY_coord(),
                (float) transformedPillarBasePoints.get(11).getX_coord(),
                (float) transformedPillarBasePoints.get(11).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(11).getX_coord(),
                (float) transformedPillarBasePoints.get(11).getY_coord(),
                (float) transformedPillarBasePoints.get(12).getX_coord(),
                (float) transformedPillarBasePoints.get(12).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(12).getX_coord(),
                (float) transformedPillarBasePoints.get(12).getY_coord(),
                (float) transformedPillarBasePoints.get(9).getX_coord(),
                (float) transformedPillarBasePoints.get(9).getY_coord(), paint);
        //hole B
        canvas.drawLine((float) transformedPillarBasePoints.get(21).getX_coord(),
                (float) transformedPillarBasePoints.get(21).getY_coord(),
                (float) transformedPillarBasePoints.get(22).getX_coord(),
                (float) transformedPillarBasePoints.get(22).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(22).getX_coord(),
                (float) transformedPillarBasePoints.get(22).getY_coord(),
                (float) transformedPillarBasePoints.get(23).getX_coord(),
                (float) transformedPillarBasePoints.get(23).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(23).getX_coord(),
                (float) transformedPillarBasePoints.get(23).getY_coord(),
                (float) transformedPillarBasePoints.get(24).getX_coord(),
                (float) transformedPillarBasePoints.get(24).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(24).getX_coord(),
                (float) transformedPillarBasePoints.get(24).getY_coord(),
                (float) transformedPillarBasePoints.get(21).getX_coord(),
                (float) transformedPillarBasePoints.get(21).getY_coord(), paint);
        //hole C
        canvas.drawLine((float) transformedPillarBasePoints.get(17).getX_coord(),
                (float) transformedPillarBasePoints.get(17).getY_coord(),
                (float) transformedPillarBasePoints.get(18).getX_coord(),
                (float) transformedPillarBasePoints.get(18).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(18).getX_coord(),
                (float) transformedPillarBasePoints.get(18).getY_coord(),
                (float) transformedPillarBasePoints.get(19).getX_coord(),
                (float) transformedPillarBasePoints.get(19).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(19).getX_coord(),
                (float) transformedPillarBasePoints.get(19).getY_coord(),
                (float) transformedPillarBasePoints.get(20).getX_coord(),
                (float) transformedPillarBasePoints.get(20).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(20).getX_coord(),
                (float) transformedPillarBasePoints.get(20).getY_coord(),
                (float) transformedPillarBasePoints.get(17).getX_coord(),
                (float) transformedPillarBasePoints.get(17).getY_coord(), paint);
        //hole D
        canvas.drawLine((float) transformedPillarBasePoints.get(13).getX_coord(),
                (float) transformedPillarBasePoints.get(13).getY_coord(),
                (float) transformedPillarBasePoints.get(14).getX_coord(),
                (float) transformedPillarBasePoints.get(14).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(14).getX_coord(),
                (float) transformedPillarBasePoints.get(14).getY_coord(),
                (float) transformedPillarBasePoints.get(15).getX_coord(),
                (float) transformedPillarBasePoints.get(15).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(15).getX_coord(),
                (float) transformedPillarBasePoints.get(15).getY_coord(),
                (float) transformedPillarBasePoints.get(16).getX_coord(),
                (float) transformedPillarBasePoints.get(16).getY_coord(), paint);
        canvas.drawLine((float) transformedPillarBasePoints.get(16).getX_coord(),
                (float) transformedPillarBasePoints.get(16).getY_coord(),
                (float) transformedPillarBasePoints.get(13).getX_coord(),
                (float) transformedPillarBasePoints.get(13).getY_coord(), paint);
        fragmentBaseBinding.drawingBase.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentBaseBinding = null;
    }

}