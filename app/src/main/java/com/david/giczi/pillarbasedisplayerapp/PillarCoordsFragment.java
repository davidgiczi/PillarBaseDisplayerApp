package com.david.giczi.pillarbasedisplayerapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentCoordsBinding;
import java.util.List;

public class PillarCoordsFragment extends Fragment {

    private FragmentCoordsBinding fragmentCoordsBinding;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentCoordsBinding = FragmentCoordsBinding.inflate(inflater, container, false);
        calcPillarBaseCoordinates();
        displayPillarBaseCoordinates();
        MainActivity.PAGE_COUNTER = 2;
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
                    Toast.makeText(getContext(), "My id is: " +pointId.getId(), Toast.LENGTH_LONG).show();
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
            pointCoordinates.setMinWidth(500);
            pointCoordinates.setSelected(true);
            row.addView(pointId);
            row.addView(pointCoordinates);
            fragmentCoordsBinding.dataStore.addView(row);
        }
    }

}
