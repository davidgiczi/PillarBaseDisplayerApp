package com.david.giczi.pillarbasedisplayerapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentDataBinding = FragmentDataBinding.inflate(inflater, container, false);
        if( !((MainActivity) getActivity()).IS_WEIGHT_BASE ){
            fragmentDataBinding.inputDistanceOfDirectionPoints.setVisibility(View.INVISIBLE);
            fragmentDataBinding.inputFootDistancePerpendicularly
                    .setHint(R.string.distance_from_side_of_hole_of_base_perpendicularly);
            fragmentDataBinding.inputFootDistanceParallel
                    .setHint(R.string.distance_from_side_of_hole_of_base_parallel);
        }
        displayInputData();
        ((MainActivity) getActivity()).PAGE_COUNTER = 1;
        return fragmentDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentDataBinding.inputPillarId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    fragmentDataBinding.inputNextPrevPillarId
                            .setText(fragmentDataBinding.inputPillarId.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fragmentDataBinding.inputYCoordinate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(fragmentDataBinding.inputYCoordinate.getText().toString().length() > 3 ){
                    fragmentDataBinding.inputNextPrevYCoordinate
                            .setText(fragmentDataBinding.inputYCoordinate.getText().toString().substring(0, 3));
                }
                else if(fragmentDataBinding.inputYCoordinate.getText().toString().length() < 3 ){
                    fragmentDataBinding.inputNextPrevYCoordinate.setText("");
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        fragmentDataBinding.inputXCoordinate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(fragmentDataBinding.inputXCoordinate.getText().toString().length() > 3 ){
                    fragmentDataBinding.inputNextPrevXCoordinate
                            .setText(fragmentDataBinding.inputXCoordinate.getText().toString().substring(0, 3));
                }
                else if(fragmentDataBinding.inputXCoordinate.getText().toString().length() < 3 ){
                    fragmentDataBinding.inputNextPrevXCoordinate.setText("");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentDataBinding = null;
    }
    private void displayInputData(){
        List<String> inputData =  ((MainActivity) getActivity()).BASE_DATA;
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
            fragmentDataBinding.inputDistanceOfDirectionPoints.setVisibility(View.VISIBLE);
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
            fragmentDataBinding.inputDistanceOfDirectionPoints.setVisibility(View.INVISIBLE);
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
            if( inputData.get(12).contains(".") ){
                fragmentDataBinding.inputMin.setText(inputData.get(12).substring(0, inputData.get(12).indexOf('.')));
            }
            else{
                fragmentDataBinding.inputMin.setText(inputData.get(12));
            }
            if( inputData.get(13).contains(".") ) {
                fragmentDataBinding.inputSec.setText(inputData.get(13).substring(0, inputData.get(13).indexOf('.')));
            }
            else{
                fragmentDataBinding.inputSec.setText(inputData.get(13));
            }
        }
    }

}