package com.david.giczi.pillarbasedisplayerapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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
        canvas.drawColor(Color.WHITE);
        fragmentBaseBinding.drawingBase.setImageBitmap(bitmap);
        MainActivity.PAGE_COUNTER = 3;
        if(MainActivity.PILLAR_BASE_COORDINATES != null ){
            transformPillarBasePoints();
            if( MainActivity.IS_WEIGHT_BASE ){
                drawPillarAxesForWeightBase();
            }
            else {
                drawPillarAxesForPlateBase();
                drawPillarBaseHoleForPlateBase();
            }
        }
        return fragmentBaseBinding.getRoot();
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
        this.paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
        paint.setPathEffect(new DashPathEffect(new float[] {30f,20f}, 0f));
        paint.setAntiAlias(true);
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
        this.paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
        paint.setAntiAlias(true);
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
        this.paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
        paint.setPathEffect(new DashPathEffect(new float[] {30f,20f}, 0f));
        paint.setAntiAlias(true);
        canvas.drawLine(100F, 1105F, 100F + 50 * MM, 1105F, paint);
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