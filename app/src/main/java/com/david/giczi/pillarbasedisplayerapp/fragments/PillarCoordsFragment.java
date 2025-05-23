package com.david.giczi.pillarbasedisplayerapp.fragments;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.david.giczi.pillarbasedisplayerapp.service.AzimuthAndDistance;
import com.david.giczi.pillarbasedisplayerapp.MainActivity;
import com.david.giczi.pillarbasedisplayerapp.service.PillarCoordsForPlateBase;
import com.david.giczi.pillarbasedisplayerapp.service.PillarCoordsForWeightBase;
import com.david.giczi.pillarbasedisplayerapp.service.Point;
import com.david.giczi.pillarbasedisplayerapp.R;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentCoordsBinding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PillarCoordsFragment extends Fragment {

    private FragmentCoordsBinding fragmentCoordsBinding;
    private Point startPoint;
    private Point endPoint;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentCoordsBinding = FragmentCoordsBinding.inflate(inflater, container, false);
        try {
            calcPillarBaseCoordinates();
        }catch (InvalidParameterException e){
            Toast.makeText(getContext(), "Koordináták nem számíthatók", Toast.LENGTH_LONG).show();
            NavController navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_CoordsFragment_to_DataFragment);
            return fragmentCoordsBinding.getRoot();
        }
        displayPillarBaseCoordinates();
        MainActivity.PAGE_COUNTER = 3;
        MainActivity.MENU.findItem(R.id.weight_base).setEnabled(false);
        MainActivity.MENU.findItem(R.id.plate_base).setEnabled(false);
        if( MainActivity.IS_SAVE_RTK_FILE ){
           saveProjectFileForRTK();
        }
        if( MainActivity.IS_SAVE_TPS_FILE ){
            saveProjectFileForTPS();
        }
        if( MainActivity.northPoleWindow != null ){
            MainActivity.northPoleWindow.dismiss();
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
            calculatorForWeightBase.setSideOfAngle("0".equals(inputData.get(15)));
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
            calculatorForPlateBase.setSideOfAngle("0".equals(inputData.get(14)));
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
            pointId.setOnClickListener(v -> {

                if( startPoint == null ){
                    startPoint = MainActivity.PILLAR_BASE_COORDINATES.get(pointId.getId());
                 TextView startPointView = requireActivity().findViewById(pointId.getId());
                        startPointView.setTextSize(30F);
                        startPointView.setTextColor(Color.parseColor("#fe7e0f"));
                        MainActivity.MENU.findItem(R.id.goto_next_fragment).setEnabled(false);
                    return;
                }
              endPoint = MainActivity.PILLAR_BASE_COORDINATES.get(pointId.getId());
                TextView endPointView = ((MainActivity) requireActivity()).findViewById(pointId.getId());
                    endPointView.setTextSize(30F);
                    endPointView.setTextColor(Color.parseColor("#fe7e0f"));
              popupDistanceBetweenPoints();
            });
            if( idValues.length == 2 ){
                String id = idValues[0] + idValues[1];
               pointId.setText(id);
            }
            else {
                pointId.setText(pillarBaseCoordinate.getPointID());
            }
            TextView pointCoordinates = new TextView(getContext());
            pointCoordinates.setId(MainActivity.PILLAR_BASE_COORDINATES.indexOf(pillarBaseCoordinate) + 100);
            pointCoordinates.setOnClickListener(c ->{
                for (int i = 0; i < MainActivity.PILLAR_BASE_COORDINATES.size(); i++) {
                    TextView coordinates = (TextView) ((MainActivity) requireActivity()).findViewById(i + 100);
                    coordinates.setTextColor(Color.RED);
                }
                pointCoordinates.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
                int coordinatesId = pointCoordinates.getId();
                MainActivity.FIND_POINT = MainActivity.PILLAR_BASE_COORDINATES.get(coordinatesId - 100);
            });
            int coordinatesId = pointCoordinates.getId();
            if( MainActivity.FIND_POINT != null &&
                    MainActivity.PILLAR_BASE_COORDINATES.indexOf(MainActivity.FIND_POINT) == coordinatesId - 100){
                pointCoordinates.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            }
            else if( MainActivity.FIND_POINT == null && MainActivity.IS_WEIGHT_BASE &&
                    MainActivity.PILLAR_BASE_COORDINATES.indexOf(pillarBaseCoordinate) == 9 ){
                pointCoordinates.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            }
            else if( MainActivity.FIND_POINT == null && !MainActivity.IS_WEIGHT_BASE &&
                    MainActivity.PILLAR_BASE_COORDINATES.indexOf(pillarBaseCoordinate) == 1 ){
                pointCoordinates.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            }
            else {
                pointCoordinates.setTextColor(Color.RED);
            }
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
                .setText(String.format(Locale.getDefault(),"%.3fm", distance.calcDistance()));

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            for (int i = 0; i < MainActivity.PILLAR_BASE_COORDINATES.size(); i++){
            TextView pointId = (TextView)((MainActivity) requireActivity()).findViewById(i);
            pointId.setTextColor(Color.BLACK);
            pointId.setTextSize(20F);
            }
            startPoint = null;
            distanceWindow.dismiss();
            MainActivity.MENU.findItem(R.id.goto_next_fragment).setEnabled(true);
        }, 3000);
    }

    private void saveProjectFileForRTK() {
        String fileName = "RTK_" +((TextView)
                (((MainActivity)
                        requireActivity()).findViewById(R.id.projectNameTitle))).getText().toString() + ".txt";
        File projectFile =
                new File(Environment.getExternalStorageDirectory(),
                        "/Documents/" + fileName);
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
        String fileName = "TPS_" + ((TextView)
                (((MainActivity)
                        requireActivity()).findViewById(R.id.projectNameTitle))).getText().toString() + ".txt";
        File projectFile =
                new File(Environment.getExternalStorageDirectory(),
                        "/Documents/" + fileName);
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
