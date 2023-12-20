package com.david.giczi.pillarbasedisplayerapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentCoordsBinding;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PillarCoordsFragment extends Fragment {

    private FragmentCoordsBinding fragmentCoordsBinding;
    private Point startPoint;
    private Point endPoint;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentCoordsBinding = FragmentCoordsBinding.inflate(inflater, container, false);
        calcPillarBaseCoordinates();
        displayPillarBaseCoordinates();
        MainActivity.PAGE_COUNTER = 2;
        MainActivity.MENU.findItem(R.id.weight_base).setEnabled(false);
        MainActivity.MENU.findItem(R.id.plate_base).setEnabled(false);
       if( MainActivity.IS_SAVE_RTK_FILE ){
           saveProjectFileForRTK();
       }
        if( MainActivity.IS_SAVE_TPS_FILE ){
            saveProjectFileForTPS();
        }
        return fragmentCoordsBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentCoordsBinding = null;
    }

    private void calcPillarBaseCoordinates(){
        List<String> inputData;
        if( MainActivity.BASE_DATA == null ){
            return;
        }
        else {
          inputData = MainActivity.BASE_DATA;
        }

        Point center = new Point(inputData.get(1),
                Double.parseDouble(inputData.get(2)),
                Double.parseDouble(inputData.get(3)));
        Point direction = new Point(inputData.get(4),
                Double.parseDouble(inputData.get(5)),
                Double.parseDouble(inputData.get(6)));

        if( MainActivity.IS_WEIGHT_BASE ){
        PillarCoordsForWeightBase calculatorForWeightBase =
                new PillarCoordsForWeightBase(center, direction);
        calculatorForWeightBase.setDistanceOnTheAxis(Double.parseDouble(inputData.get(7)));
        calculatorForWeightBase.setHorizontalDistanceBetweenPillarLegs(Double.parseDouble(inputData.get(8)));
        calculatorForWeightBase.setVerticalDistanceBetweenPillarLegs(Double.parseDouble(inputData.get(9)));
        calculatorForWeightBase.setHorizontalSizeOfHoleOfPillarLeg(Double.parseDouble(inputData.get(10)));
        calculatorForWeightBase.setVerticalSizeOfHoleOfPillarLeg(Double.parseDouble(inputData.get(11)));
        calculatorForWeightBase.setAngleValueBetweenMainPath(Double.parseDouble(inputData.get(12)));
        calculatorForWeightBase.setAngularMinuteValueBetweenMainPath(Double.parseDouble(inputData.get(13)));
        calculatorForWeightBase.setAngularSecondValueBetweenMainPath(Double.parseDouble(inputData.get(14)));
            if ("0".equals(inputData.get(15))) {
                calculatorForWeightBase.setSideOfAngle(true);
            } else {
                calculatorForWeightBase.setSideOfAngle(false);
            }
            calculatorForWeightBase.calculatePillarPoints();
            MainActivity.PILLAR_BASE_COORDINATES = calculatorForWeightBase.getPillarPoints();
        }
        else {
            PillarCoordsForPlateBase calculatorForPlateBase =
                    new PillarCoordsForPlateBase(center, direction);
            calculatorForPlateBase.setHorizontalSizeOfHole(Double.parseDouble(inputData.get(7)));
            calculatorForPlateBase.setVerticalSizeOfHole(Double.parseDouble(inputData.get(8)));
            calculatorForPlateBase.setHorizontalDistanceFromTheSideOfHole(Double.parseDouble(inputData.get(9)));
            calculatorForPlateBase.setVerticalDistanceFromTheSideOfHole(Double.parseDouble(inputData.get(10)));
            calculatorForPlateBase.setAngleValueBetweenMainPath(Double.parseDouble(inputData.get(11)));
            calculatorForPlateBase.setAngularMinuteValueBetweenMainPath(Double.parseDouble(inputData.get(12)));
            calculatorForPlateBase.setAngularSecondValueBetweenMainPath(Double.parseDouble(inputData.get(13)));
            if ("0".equals(inputData.get(14))) {
                calculatorForPlateBase.setSideOfAngle(true);
            } else {
                calculatorForPlateBase.setSideOfAngle(false);
            }
            calculatorForPlateBase.calculatePillarPoints();
            MainActivity.PILLAR_BASE_COORDINATES = calculatorForPlateBase.getPillarPoints();

        }
        MainActivity.PILLAR_BASE_COORDINATES.add(direction);
    }

    private void displayPillarBaseCoordinates(){
        for (Point pillarBaseCoordinate : MainActivity.PILLAR_BASE_COORDINATES) {
            String[] idValues = pillarBaseCoordinate.getPointID().split("\\s+");
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView pointId = new TextView(getContext());
            pointId.setTextColor(Color.BLACK);
            pointId.setTextSize(20F);
            pointId.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
            pointId.setMinWidth(300);
            pointId.setId(MainActivity.PILLAR_BASE_COORDINATES.indexOf(pillarBaseCoordinate));
            pointId.setPadding(80, 20, 10, 20 );
            pointId.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if( startPoint == null ){
                        startPoint = MainActivity.PILLAR_BASE_COORDINATES.get(pointId.getId());
                     TextView startPointView = ((MainActivity) getActivity()).findViewById(pointId.getId());
                            startPointView.setTextSize(30F);
                            startPointView.setTextColor(Color.parseColor("#fe7e0f"));
                            MainActivity.MENU.findItem(R.id.goto_next_fragment).setEnabled(false);
                        return;
                    }
                  endPoint = MainActivity.PILLAR_BASE_COORDINATES.get(pointId.getId());
                    TextView endPointView = ((MainActivity) getActivity()).findViewById(pointId.getId());
                        endPointView.setTextSize(30F);
                        endPointView.setTextColor(Color.parseColor("#fe7e0f"));
                  popupDistanceBetweenPoints();
                }
            });
            if( idValues.length == 2 ){
               pointId.setText(idValues[0] + idValues[1]);
            }
            else {
                pointId.setText(pillarBaseCoordinate.getPointID());
            }
            TextView pointCoordinates = new TextView(getContext());
            pointCoordinates.setTextColor(Color.RED);
            pointCoordinates.setTextSize(20F);
            pointCoordinates.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
            pointCoordinates.setText(pillarBaseCoordinate.toString());
            pointCoordinates.setPadding(30, 20, 10, 10 );
            pointCoordinates.setTextIsSelectable(true);
            pointCoordinates.setMinWidth(500);
            row.addView(pointId);
            row.addView(pointCoordinates);
            fragmentCoordsBinding.dataStore.addView(row);
        }
    }

    private void popupDistanceBetweenPoints() {
        ViewGroup container = (ViewGroup) getLayoutInflater()
                .inflate(R.layout.fragment_distance_between_points, null);
        PopupWindow distanceWindow = new PopupWindow(container, 1050, 100, true);
        distanceWindow.showAtLocation(fragmentCoordsBinding.getRoot(), Gravity.CENTER, 0, 0);
        AzimuthAndDistance distance = new AzimuthAndDistance(startPoint, endPoint);
        String[] startPointIdValues = startPoint.getPointID().split("\\s+");
        ((TextView) container.findViewById(R.id.start_point_id))
                .setText(startPointIdValues.length == 2 ?
                        startPointIdValues[0] + startPointIdValues[1] + "." :
                        startPoint.getPointID() + ".");
        String[] endPointIdValues = endPoint.getPointID().split("\\s+");
        ((TextView) container.findViewById(R.id.end_point_id))
                .setText(endPointIdValues.length == 2 ?
                        endPointIdValues[0] + endPointIdValues[1] + "."  :
                        endPoint.getPointID() + ".");
        ((TextView) container.findViewById(R.id.distance_between_points))
                .setText(String.format("%.3fm", distance.calcDistance()));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < MainActivity.PILLAR_BASE_COORDINATES.size(); i++){
                TextView pointId = (TextView)((MainActivity) getActivity()).findViewById(i);
                pointId.setTextColor(Color.BLACK);
                pointId.setTextSize(20F);
                }
                startPoint = null;
                distanceWindow.dismiss();
                MainActivity.MENU.findItem(R.id.goto_next_fragment).setEnabled(true);
            }
        }, 3000);
    }

    private void saveProjectFileForRTK() {
        String fileName = ((TextView)
                (((MainActivity)
                        getActivity()).findViewById(R.id.projectNameTitle))).getText().toString();
        File projectFile =
                new File(Environment.getExternalStorageDirectory(),
                        "/Documents/" + fileName + "_RTK.txt");
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(projectFile, true));
            for (Point point : MainActivity.PILLAR_BASE_COORDINATES) {
                String[] idValues = point.getPointID().split("\\s+");
                if( idValues.length ==  2 ){
                    point.setPointID(idValues[0] + idValues[1]);
                }
                bw.write(point.writePointForRTK());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            Toast.makeText(getContext(), projectFile.getName() +
                    "\nkoordináta fájl mentése sikertelen.", Toast.LENGTH_SHORT).show();
            return;
        }
            Toast.makeText(getContext(),
                    "Koordináta fájl mentve:\n"
                            + projectFile.getName() , Toast.LENGTH_SHORT).show();
    }

    private void saveProjectFileForTPS() {
        String fileName = ((TextView)
                (((MainActivity)
                        getActivity()).findViewById(R.id.projectNameTitle))).getText().toString();
        File projectFile =
                new File(Environment.getExternalStorageDirectory(),
                        "/Documents/" + fileName + "_TPS.txt");
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(projectFile, true));
            for (Point point : MainActivity.PILLAR_BASE_COORDINATES) {
                String[] idValues = point.getPointID().split("\\s+");
                if( idValues.length ==  2 ){
                    point.setPointID(idValues[0] + idValues[1]);
                }
                bw.write(point.writePointForTPS());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            Toast.makeText(getContext(), projectFile.getName() +
                    "\nkoordináta fájl mentése sikertelen.", Toast.LENGTH_SHORT).show();
        }finally {
            Toast.makeText(getContext(),
                    "Koordináta fájl mentve:\n"
                            + projectFile.getName() , Toast.LENGTH_SHORT).show();
        }
    }


}
