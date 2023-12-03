package com.david.giczi.pillarbasedisplayerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentDataBinding;

import java.util.Arrays;
import java.util.List;

public class PillarDataFragment extends Fragment {

    private FragmentDataBinding fragmentDataBinding;
    private List<String> inputData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentDataBinding = FragmentDataBinding.inflate(inflater, container, false);
        inputData = ((MainActivity) getActivity()).BASE_DATA;
        displayInputData();
        return fragmentDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentDataBinding = null;
    }
    private void displayInputData(){
        if( inputData == null || inputData.isEmpty() ){
            return;
        }
        fragmentDataBinding.inputPillarId.setText(inputData.get(1));
        fragmentDataBinding.inputYCoordinate.setText(inputData.get(2));
        fragmentDataBinding.inputXCoordinate.setText(inputData.get(3));
        fragmentDataBinding.inputNextPrevPillarId.setText(inputData.get(4));
        fragmentDataBinding.inputNextPrevYCoordinate.setText(inputData.get(5));
        fragmentDataBinding.inputNextPrevXCoordinate.setText(inputData.get(6));

        if( ((MainActivity) getActivity()).BASE_TYPE[0].equals(inputData.get(0)) ){
            MainActivity.IS_WEIGHT_BASE = true;
            ((MainActivity) getActivity()).optionMenu.findItem(R.id.weight_base).setTitle(R.string.ticked_weight_base_option);
            ((MainActivity) getActivity()).optionMenu.findItem(R.id.plate_base).setTitle(R.string.plate_base_option);
            ((MainActivity) getActivity()).getDataFragmentForWeightBase();
            fragmentDataBinding.inputDistanceOfDirectionPoints.setText(inputData.get(7));
            fragmentDataBinding.inputFootDistancePerpendicularly.setText(inputData.get(8));
            fragmentDataBinding.inputFootDistanceParallel.setText(inputData.get(9));
            fragmentDataBinding.inputHoleDistancePerpendicularly.setText(inputData.get(10));
            fragmentDataBinding.inputHoleDistanceParallel.setText(inputData.get(11));
            if( "0".equals(inputData.get(15))){
                fragmentDataBinding.radioRight.setChecked(true);
            }
            else if( "1".equals(inputData.get(15))){
                fragmentDataBinding.radioLeft.setChecked(true);
            }
            fragmentDataBinding.inputAngle.setText(inputData.get(12));
            fragmentDataBinding.inputMin.setText(inputData.get(13));
            fragmentDataBinding.inputSec.setText(inputData.get(14));
        }
        else if( ((MainActivity) getActivity()).BASE_TYPE[1].equals(inputData.get(0)) ) {
            MainActivity.IS_WEIGHT_BASE = false;
            ((MainActivity) getActivity()). optionMenu.findItem(R.id.weight_base).setTitle(R.string.weight_base_option);
            ((MainActivity) getActivity()).optionMenu.findItem(R.id.plate_base).setTitle(R.string.ticked_plate_base_option);
            ((MainActivity) getActivity()).getDataFragmentForPlateBase();
            fragmentDataBinding.inputHoleDistancePerpendicularly.setText(inputData.get(7));
            fragmentDataBinding.inputHoleDistanceParallel.setText(inputData.get(8));
            fragmentDataBinding.inputFootDistancePerpendicularly.setText(inputData.get(9));
            fragmentDataBinding.inputFootDistanceParallel.setText(inputData.get(10));
            if( "0".equals(inputData.get(14))){
                fragmentDataBinding.radioRight.setChecked(true);
            }
            else if( "1".equals(inputData.get(14))){
                fragmentDataBinding.radioLeft.setChecked(true);
            }
            fragmentDataBinding.inputAngle.setText(inputData.get(11));
            fragmentDataBinding.inputMin.setText(inputData.get(12));
            fragmentDataBinding.inputSec.setText(inputData.get(13));
        }
    }

}