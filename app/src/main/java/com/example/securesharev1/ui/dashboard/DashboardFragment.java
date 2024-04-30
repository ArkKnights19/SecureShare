package com.example.securesharev1.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.securesharev1.CameraReceiveActivity;
import com.example.securesharev1.MainActivity;
import com.example.securesharev1.databinding.FragmentDashboardBinding;

import org.opencv.android.OpenCVLoader;

public class DashboardFragment extends Fragment {
    private String TAG = "R";

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        Log.i(TAG, "Receive");

        binding.openRecieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRecieve();
            }
        });

        openCVInit();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void openCVInit() {
        if (OpenCVLoader.initDebug()) {

            Log.d("Check", "OpenCv configured successfully");

        } else {

            Log.d("Check", "OpenCv Isn’t configured successfully");

        }
    }

    public void onClickRecieve() {
        Intent intent = new Intent(getActivity(), CameraReceiveActivity.class);
        getActivity().startActivity(intent);
    }
}