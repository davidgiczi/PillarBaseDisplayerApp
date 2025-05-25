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
import com.david.giczi.pillarbasedisplayerapp.service.PillarLocationCalculator;

public class PillarMeasDataFragment extends Fragment {

    private FragmentMeasDataBinding fragmentMeasDataBinding;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       fragmentMeasDataBinding = FragmentMeasDataBinding.inflate(inflater, container, false);
        MainActivity.PAGE_COUNTER = 1;
        MainActivity.MENU.findItem(R.id.start_stop_gps).setEnabled(false);
        fragmentMeasDataBinding.btnSend.setOnClickListener(v -> {
            PillarLocationCalculator calcPillarLocationData = new PillarLocationCalculator();
            calcPillarLocationData.setDistance(fragmentMeasDataBinding.distanceOfNewPillar.getText().toString());

            if( fragmentMeasDataBinding.calcMirrorCheckBox.isChecked() ){
                calcPillarLocationData
                        .addCenterPillarMeasData(
                                fragmentMeasDataBinding.directionFoot1YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot1XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot2YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot2XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot3YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot3XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot4YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.directionFoot4XCoordinate.getText().toString());
                calcPillarLocationData
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
                calcPillarLocationData
                        .addCenterPillarMeasData(
                                fragmentMeasDataBinding.centerFoot1YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot1XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot2YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot2XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot3YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot3XCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot4YCoordinate.getText().toString(),
                                fragmentMeasDataBinding.centerFoot4XCoordinate.getText().toString());
                calcPillarLocationData
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
            calcPillarLocationData.calcPillarLocationData();
            if( calcPillarLocationData.centerX == null || calcPillarLocationData.centerY == null
            || calcPillarLocationData.directionX == null || calcPillarLocationData.directionY == null ){
                Toast.makeText(getContext(), "Koordináták nem számíthatók", Toast.LENGTH_LONG).show();
                return;
            }
            Bundle resultData = new Bundle();
            resultData.putString("centerX", calcPillarLocationData.centerX);
            resultData.putString("centerY", calcPillarLocationData.centerY);
            resultData.putString("directionX", calcPillarLocationData.directionX);
            resultData.putString("directionY", calcPillarLocationData.directionY);
            getParentFragmentManager().setFragmentResult("results", resultData);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_MeasDataFragment_to_DataFragment);
        });
        return fragmentMeasDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMeasDataBinding = null;
    }
}
