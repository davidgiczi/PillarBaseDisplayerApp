package com.david.giczi.pillarbasedisplayerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentCoordsBinding;

import java.util.List;

public class PillarCoordsFragment extends Fragment {

    private FragmentCoordsBinding fragmentCoordsBinding;
    private List<Point> pillarBaseCoordinates;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentCoordsBinding = FragmentCoordsBinding.inflate(inflater, container, false);
        calcPillarBaseCoordinates();
        displayPillarBaseCoordinates();
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

        List<String> inputData =  ((MainActivity) getActivity()).BASE_DATA;
        if( inputData == null || inputData.isEmpty() ){
            return;
        }

        Point center = new Point(inputData.get(1),
                Double.parseDouble(inputData.get(2)),
                Double.parseDouble(inputData.get(3)));
        Point direction = new Point(inputData.get(4),
                Double.parseDouble(inputData.get(5)),
                Double.parseDouble(inputData.get(6)));

        if( ((MainActivity) getActivity()).IS_WEIGHT_BASE ){
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
            pillarBaseCoordinates = calculatorForWeightBase.getPillarPoints();
        }
        else{
            PillarCoordsForPlateBase calculatorForPlateBase =
                    new PillarCoordsForPlateBase(center, direction);
            calculatorForPlateBase.setHorizontalSizeOfHole(Double.parseDouble(inputData.get(7)));
            calculatorForPlateBase.setVerticalSizeOfHole(Double.parseDouble(inputData.get(8)));
            calculatorForPlateBase.setHorizontalDistanceFromTheSideOfHole(Double.parseDouble(inputData.get(9)));
            calculatorForPlateBase.setVerticalDistanceFromTheSideOfHole(Double.parseDouble(inputData.get(10)));
            if ("0".equals(inputData.get(14))) {
                calculatorForPlateBase.setSideOfAngle(true);
            } else {
                calculatorForPlateBase.setSideOfAngle(false);
            }
            calculatorForPlateBase.calculatePillarPoints();
            pillarBaseCoordinates = calculatorForPlateBase.getPillarPoints();
        }
        pillarBaseCoordinates.add(direction);
    }

    private void displayPillarBaseCoordinates(){

        StringBuilder sb = new StringBuilder();

        for (Point pillarBaseCoordinate : pillarBaseCoordinates) {
            sb.append(pillarBaseCoordinate.getPointID())
            .append("\n")
            .append("\n");
        }
        fragmentCoordsBinding.pointId.setText(sb.toString());
        sb.delete(0, sb.length());
        for (Point pillarBaseCoordinate : pillarBaseCoordinates) {
            sb.append(pillarBaseCoordinate)
            .append("\n")
            .append("\n");
        }
        fragmentCoordsBinding.pointCoordinates.setText(sb.toString());
    }

}
