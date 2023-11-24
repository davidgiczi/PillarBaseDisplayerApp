package com.david.giczi.pillarbasedisplayerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentBaseBinding;


public class PillarBaseFragment extends Fragment {

    private FragmentBaseBinding fragmentBaseBinding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentBaseBinding = FragmentBaseBinding.inflate(inflater, container, false);
        return fragmentBaseBinding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentBaseBinding.dataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PillarBaseFragment.this)
                        .navigate(R.id.action_BaseFragment_to_DataFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentBaseBinding = null;
    }

}