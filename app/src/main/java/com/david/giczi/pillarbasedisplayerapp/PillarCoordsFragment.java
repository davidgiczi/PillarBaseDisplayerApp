package com.david.giczi.pillarbasedisplayerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentCoordsBinding;

public class PillarCoordsFragment extends Fragment {

    private FragmentCoordsBinding fragmentCoordsBinding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentCoordsBinding = FragmentCoordsBinding.inflate(inflater, container, false);
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
}
