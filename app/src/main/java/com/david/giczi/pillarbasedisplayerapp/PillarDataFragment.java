package com.david.giczi.pillarbasedisplayerapp;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentDataBinding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        showInputData();
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

    private void showInputData(){
        if( inputData == null ){
            return;
        }
       StringBuilder sb = new StringBuilder();
                    for (String data : inputData) {
                        sb.append(data);
                        sb.append("\n");
                    }
      fragmentDataBinding.data.setText(sb.toString());
    }

}