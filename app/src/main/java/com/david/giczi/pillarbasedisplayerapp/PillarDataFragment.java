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
        fragmentDataBinding.coordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProjectFile();
                NavHostFragment.findNavController(PillarDataFragment.this)
                        .navigate(R.id.action_DataFragment_to_CoordsFragment);
            }
        });
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
    private void saveProjectFile() {
        if( inputData == null ){
            return;
        }
        File projectFile =
                new File(Environment.getExternalStorageDirectory(),
                        "/Documents/SavedData.txt");
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(projectFile));
            for (String data : inputData) {
                bw.write(data);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            Toast.makeText(getContext(), projectFile.getAbsolutePath() +
                    " fájl mentése sikertelen", Toast.LENGTH_SHORT).show();
        }finally {
            Toast.makeText(getContext(),
                    "Projekt fájl mentve: "
                            + projectFile.getName() , Toast.LENGTH_SHORT).show();
        }

    }

}