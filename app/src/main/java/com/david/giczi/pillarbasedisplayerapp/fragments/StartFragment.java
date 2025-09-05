package com.david.giczi.pillarbasedisplayerapp.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.david.giczi.pillarbasedisplayerapp.MainActivity;
import com.david.giczi.pillarbasedisplayerapp.R;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentStartBinding;


public class StartFragment extends Fragment {

    private FragmentStartBinding fragmentStartBinding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentStartBinding = FragmentStartBinding.inflate(inflater, container,false);
        addBackgroundImage();
        MainActivity.PAGE_COUNTER = 0;
        if( MainActivity.northPoleWindow != null ){
            MainActivity.northPoleWindow.dismiss();
        }
        if( MainActivity.gpsDataWindow != null ){
            MainActivity.gpsDataWindow.dismiss();
        }
        return fragmentStartBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentStartBinding = null;
    }

    private void addBackgroundImage(){
        Drawable backgroundImage;
        switch ((int) (Math.random() * 3) + 1){
            case 1 :
                backgroundImage  = requireActivity().getDrawable(R.drawable.pillars1);
                break;
            case 2 :
                backgroundImage  = requireActivity().getDrawable(R.drawable.pillars2);
                break;
            default:
                backgroundImage  = requireActivity().getDrawable(R.drawable.pillars3);
        }
        fragmentStartBinding.startPage.setBackground(backgroundImage);
    }

}