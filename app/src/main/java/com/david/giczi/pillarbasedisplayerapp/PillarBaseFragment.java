package com.david.giczi.pillarbasedisplayerapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentBaseBinding;
import java.util.ArrayList;
import java.util.List;


public class PillarBaseFragment extends Fragment {

    private FragmentBaseBinding fragmentBaseBinding;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private static float X_CENTER;
    private static float Y_CENTER;
    private static float MM;
    private static final float SCALE = 400F;
    private static List<Point> transformedPillarBasePoints;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentBaseBinding = FragmentBaseBinding.inflate(inflater, container, false);
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
        MainActivity.PAGE_COUNTER = 3;
        if(MainActivity.PILLAR_BASE_COORDINATES != null ){
            transformPillarBasePoints();
            addNorthSign();
            drawCircleForPoints();
            drawTextsForPillarBase();
            drawMainLineDirections();
            if( MainActivity.IS_WEIGHT_BASE ){
                drawPillarBaseHoleForWeightBase();
                drawPillarAxesForWeightBase();
            }
            else {
                drawPillarBaseHoleForPlateBase();
                drawPillarAxesForPlateBase();
            }
        }
        return fragmentBaseBinding.getRoot();
    }

    private void addNorthSign(){
        Matrix matrix = new Matrix();
        matrix.postScale(0.25F, 0.25F);
        Bitmap northSignResource = BitmapFactory.decodeResource(getResources(), R.drawable.north);
        Bitmap northSign = Bitmap.createBitmap(northSignResource, 0, 0,
                northSignResource.getWidth(), northSignResource.getHeight(), matrix, false);
        canvas.drawBitmap(northSign, 5 * MM, 5 * MM, paint);
    }

    private void drawCircleForPoints() {
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float) transformedPillarBasePoints.get(0).getX_coord(),
                (float) transformedPillarBasePoints.get(0).getY_coord(), 1.2F * MM, paint);
        drawPillarBasePointId(transformedPillarBasePoints.get(0), Color.RED, 40);
        paint.setColor(Color.MAGENTA);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6F);
        for (int i = 1; i < transformedPillarBasePoints.size(); i++) {
            canvas.drawCircle((float) transformedPillarBasePoints.get(i).getX_coord(),
                    (float) transformedPillarBasePoints.get(i).getY_coord(), MM, paint);
        }
    }

    private void drawPillarBasePointId(Point pillarBasePoint, int colorValue, int size){
        paint.setColor(colorValue);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(size);
        canvas.drawText(pillarBasePoint.getPointID(), (float) pillarBasePoint.getX_coord(),
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
        canvas.drawText("M= 1:400",
                5 * MM, getResources().getDisplayMetrics().heightPixels - 10 * MM, paint);
        canvas.drawText(centerPoint.getPointID() + ". és "
                        + directionPoint.getPointID() + ". oszlopok távolsága: " +
        String.format( "%.3fm", mainLineDistance.calcDistance()).replace(",", "."),
               5 * MM, getResources().getDisplayMetrics().heightPixels - 5 * MM, paint);
    }

    private void drawMainLineDirections(){
        paint.setColor(Color.MAGENTA);
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
        drawPillarBasePointId(endPoint.calcPolarPoint(), Color.RED, 40);
    }

    private void drawMainLineDirectionForNormalWeightBase(){
        drawArrow(transformedPillarBasePoints.get(0), transformedPillarBasePoints.get(1));
        drawPillarBasePointId(transformedPillarBasePoints.get(1), Color.RED, 40);
    }

    private void drawMainLineDirectionForRotatedPlateBase(){

    }

    private void drawMainLineDirectionForRotatedWeightBase(){
    AzimuthAndDistance mainLineData = new AzimuthAndDistance(transformedPillarBasePoints.get(0),
            transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 1));
    PolarPoint startPoint = new PolarPoint(transformedPillarBasePoints.get(0), 1.5 * MM,
            mainLineData.calcAzimuth(), "startDirectionPoint");
    PolarPoint endPoint = new PolarPoint(startPoint.calcPolarPoint(), 30 * MM,
                mainLineData.calcAzimuth(),
            transformedPillarBasePoints.get(transformedPillarBasePoints.size() - 1).getPointID());
    canvas.drawLine(
            (float) startPoint.calcPolarPoint().getX_coord(),
            (float) startPoint.calcPolarPoint().getY_coord(),
            (float) endPoint.calcPolarPoint().getX_coord(),
            (float) endPoint.calcPolarPoint().getY_coord(), paint);
    drawArrow(transformedPillarBasePoints.get(0), endPoint.calcPolarPoint());
    drawPillarBasePointId(endPoint.calcPolarPoint(), Color.RED, 40);
    }

    private void drawArrow(Point directionPoint, Point arrowLocationPoint){
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
        paint.setColor(Color.RED);
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
        paint.setColor(Color.RED);
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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentBaseBinding = null;
    }

}