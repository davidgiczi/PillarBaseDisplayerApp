package com.david.giczi.pillarbasedisplayerapp;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentStartBinding;


public class StartFragment extends Fragment {

    private FragmentStartBinding fragmentStartBinding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentStartBinding = FragmentStartBinding.inflate(inflater, container,false);
        addBackgroundImage();
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
                backgroundImage  = ((MainActivity) getActivity()).getDrawable(R.drawable.pillars1);
                break;
            case 2 :
                backgroundImage  = ((MainActivity) getActivity()).getDrawable(R.drawable.pillars2);
                break;
            default:
                backgroundImage  =((MainActivity) getActivity()).getDrawable(R.drawable.pillars3);
        }
        fragmentStartBinding.startPage.setBackground(backgroundImage);
    }

}