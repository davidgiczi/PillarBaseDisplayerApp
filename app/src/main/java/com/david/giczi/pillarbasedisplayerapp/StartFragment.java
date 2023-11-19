package com.david.giczi.pillarbasedisplayerapp;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentFirstBinding;

public class StartFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        addBackgroundImage();
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addBackgroundImage(){
        Drawable backgroundImage;
        switch ((int) (Math.random() * 3) + 1){
            case 1 :
                backgroundImage  = ((MainActivity) getActivity()).getDrawable(R.drawable.pillars1);
                break;
            case 2 :
                backgroundImage  = ((MainActivity) getActivity()).getDrawable(R.drawable.pillars2);
                break;
            default:
                backgroundImage  =((MainActivity) getActivity()).getDrawable(R.drawable.pillars3);
        }
        binding.startPage.setBackground(backgroundImage);
    }

}