package com.david.giczi.pillarbasedisplayerapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.david.giczi.pillarbasedisplayerapp.MainActivity;
import com.david.giczi.pillarbasedisplayerapp.R;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentMeasDataBinding;
import com.david.giczi.pillarbasedisplayerapp.db.PillarBaseParamsService;
import com.david.giczi.pillarbasedisplayerapp.service.AzimuthAndDistance;
import com.david.giczi.pillarbasedisplayerapp.service.PillarLocationCalculator;
import com.david.giczi.pillarbasedisplayerapp.service.Point;

import java.util.Locale;

public class PillarMeasDataFragment extends Fragment {

    private FragmentMeasDataBinding fragmentMeasDataBinding;
    private PillarBaseParamsService service;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.fragmentMeasDataBinding = FragmentMeasDataBinding.inflate(inflater, container, false);
        this.service = ((MainActivity) requireActivity()).service;
        MainActivity.PAGE_COUNTER = 1;
        MainActivity.MENU.findItem(R.id.start_stop_gps).setEnabled(false);
        MainActivity.MENU.findItem(R.id.save_pillar_center).setEnabled(false);
        fragmentMeasDataBinding.btnSend.setOnClickListener(v -> {
        MainActivity.calcPillarLocationData = new PillarLocationCalculator();
        MainActivity.calcPillarLocationData.setAbscissa_distance(fragmentMeasDataBinding.abscissaDistanceOfNewPillar.getText().toString());
        MainActivity.calcPillarLocationData.setOrdinate_distance(fragmentMeasDataBinding.ordinateDistanceOfNewPillar.getText().toString());
            if( fragmentMeasDataBinding.calcMirrorCheckBox.isChecked() ){
                MainActivity.calcPillarLocationData
                        .addCenterPillarMeasData(
                                fragmentMeasDataBinding.directionFoot1YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot1XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot2YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot2XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot3YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot3XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot4YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot4XCoordinate.getText().toString());
                MainActivity.calcPillarLocationData
                        .addDirectionPillarMeasData(
                                fragmentMeasDataBinding.centerFoot1YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot1XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot2YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot2XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot3YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot3XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot4YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot4XCoordinate.getText().toString());
            }
            else{
                MainActivity.calcPillarLocationData
                        .addCenterPillarMeasData(
                                fragmentMeasDataBinding.centerFoot1YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot1XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot2YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot2XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot3YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot3XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot4YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot4XCoordinate.getText().toString());
                MainActivity.calcPillarLocationData
                        .addDirectionPillarMeasData(
                                fragmentMeasDataBinding.directionFoot1YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot1XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot2YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot2XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot3YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot3XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot4YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot4XCoordinate.getText().toString());
            }
            MainActivity.calcPillarLocationData.calcPillarLocationData();
            if( MainActivity.calcPillarLocationData.centerX == null || MainActivity.calcPillarLocationData.centerY == null
            || MainActivity.calcPillarLocationData.directionX == null || MainActivity.calcPillarLocationData.directionY == null ){
                Toast.makeText(getContext(), "Koordináták nem számíthatók", Toast.LENGTH_LONG).show();
                return;
            }
            MainActivity.MEAS_PILLAR_DATA = new String[16];
            MainActivity.MEAS_PILLAR_DATA[0] = fragmentMeasDataBinding.centerFoot1YCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[1] = fragmentMeasDataBinding.centerFoot1XCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[2] = fragmentMeasDataBinding.centerFoot2YCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[3] = fragmentMeasDataBinding.centerFoot2XCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[4] = fragmentMeasDataBinding.centerFoot3YCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[5] = fragmentMeasDataBinding.centerFoot3XCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[6] = fragmentMeasDataBinding.centerFoot4YCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[7] = fragmentMeasDataBinding.centerFoot4XCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[8] = fragmentMeasDataBinding.directionFoot1YCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[9] = fragmentMeasDataBinding.directionFoot1XCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[10] = fragmentMeasDataBinding.directionFoot2YCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[11] = fragmentMeasDataBinding.directionFoot2XCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[12] = fragmentMeasDataBinding.directionFoot3YCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[13] = fragmentMeasDataBinding.directionFoot3XCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[14] = fragmentMeasDataBinding.directionFoot4YCoordinate.getText().toString();
            MainActivity.MEAS_PILLAR_DATA[15] = fragmentMeasDataBinding.directionFoot4XCoordinate.getText().toString();
            Bundle resultData = new Bundle();
            resultData.putString("calcCenterX", MainActivity.calcPillarLocationData.centerX);
            resultData.putString("calcCenterY", MainActivity.calcPillarLocationData.centerY);
            resultData.putString("measDirectionX", MainActivity.calcPillarLocationData.directionX);
            resultData.putString("measDirectionY", MainActivity.calcPillarLocationData.directionY);
            getParentFragmentManager().setFragmentResult("results", resultData);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_MeasDataFragment_to_DataFragment);
        });

        return fragmentMeasDataBinding.getRoot();
    }

    private void setMeasPillarData(){

        if( MainActivity.MEAS_PILLAR_DATA != null ){
            fragmentMeasDataBinding.centerFoot1YCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[0]);
            fragmentMeasDataBinding.centerFoot1XCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[1]);
            fragmentMeasDataBinding.centerFoot2YCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[2]);
            fragmentMeasDataBinding.centerFoot2XCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[3]);
            fragmentMeasDataBinding.centerFoot3YCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[4]);
            fragmentMeasDataBinding.centerFoot3XCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[5]);
            fragmentMeasDataBinding.centerFoot4YCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[6]);
            fragmentMeasDataBinding.centerFoot4XCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[7]);
            fragmentMeasDataBinding.directionFoot1YCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[8]);
            fragmentMeasDataBinding.directionFoot1XCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[9]);
            fragmentMeasDataBinding.directionFoot2YCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[10]);
            fragmentMeasDataBinding.directionFoot2XCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[11]);
            fragmentMeasDataBinding.directionFoot3YCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[12]);
            fragmentMeasDataBinding.directionFoot3XCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[13]);
            fragmentMeasDataBinding.directionFoot4YCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[14]);
            fragmentMeasDataBinding.directionFoot4XCoordinate.setText(MainActivity.MEAS_PILLAR_DATA[15]);
        }
    }
    private void setAbscissaAndOrdinateValue(){
        if( service.actualPillarBase.centerPillarX == null ||
                service.actualPillarBase.centerPillarY == null ||
                service.actualPillarBase.directionPillarX == null ||
                service.actualPillarBase.directionPillarY == null ||
                service.actualPillarBase.controlPointX == null ||
                service.actualPillarBase.controlPointY == null ){
            return;
        }
        Point controlPoint = new Point(service.actualPillarBase.controlPointId,
                Double.parseDouble(service.actualPillarBase.controlPointY),
                Double.parseDouble(service.actualPillarBase.controlPointX));
        Point centerPoint = new Point(service.actualPillarBase.centerPillarId,
                Double.parseDouble(service.actualPillarBase.centerPillarY),
                Double.parseDouble(service.actualPillarBase.centerPillarX));
        Point nextPoint = new Point(service.actualPillarBase.directionPillarId,
                Double.parseDouble(service.actualPillarBase.directionPillarY),
                Double.parseDouble(service.actualPillarBase.directionPillarX));
        fragmentMeasDataBinding.abscissaDistanceOfNewPillar
                .setText(getAbscissaValue(controlPoint, centerPoint, nextPoint));
        fragmentMeasDataBinding.ordinateDistanceOfNewPillar
                .setText(getOrdinateValue(controlPoint, centerPoint, nextPoint));
    }
    private String getOrdinateValue(Point controlPoint, Point centerPoint, Point nextPoint){
        if( controlPoint.equals(nextPoint) ){
            return null;
        }
        else if( controlPoint.equals(centerPoint) ){
            return "0.000";
        }
        double alfa = new AzimuthAndDistance(controlPoint, nextPoint).calcAzimuth() -
                new AzimuthAndDistance(controlPoint, centerPoint).calcAzimuth();
        double distance = new AzimuthAndDistance(controlPoint, centerPoint).calcDistance();
        return String.format(Locale.getDefault(),
                "%.3f", Math.sin(alfa) * distance).replace(",", ".");
    }

    private String getAbscissaValue(Point controlPoint, Point centerPoint, Point nextPoint) {
        if( controlPoint.equals(nextPoint) ){
            return null;
        }
        else if( controlPoint.equals(centerPoint) ){
            return "0.000";
        }
        double alfa = new AzimuthAndDistance(controlPoint, nextPoint).calcAzimuth() -
                        new AzimuthAndDistance(controlPoint, centerPoint).calcAzimuth();
        double distance = new AzimuthAndDistance(controlPoint, centerPoint).calcDistance();
        return String.format(Locale.getDefault(), "%.3f",
                Math.cos(alfa) * distance).replace(",", ".");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setMeasPillarData();
        if( service.actualPillarBase != null ){
            setAbscissaAndOrdinateValue();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMeasDataBinding = null;
    }
}
