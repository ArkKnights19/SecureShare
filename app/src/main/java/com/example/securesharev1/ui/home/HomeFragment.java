package com.example.securesharev1.ui.home;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.securesharev1.R;
import com.example.securesharev1.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private static final long BIT_DURATION_1 = 50;

    private static final long BIT_DURATION_1_SCREEN = 200;

    private static final long BIT_DURATION_0_SCREEN = 1000;
    private static final long BIT_DURATION_0 = 250;
    String TAG = "Home";
    int counter = 0;
    private Handler handler;
    private CameraManager cameraManager;
    private String cameraId;
    private EditText sendInput;
    private TextView textView;
    private View whiteColorOverlay;
    private FragmentHomeBinding binding;
    private boolean isFlashlightMode;
    private float mPreviousBrightness = -1.0f;
    private Runnable colorRunnable = new Runnable() {
        @Override
        public void run() {
            // Change the background color of your view
            ;
            boolean isColor1 = false;
            if (isColor1) {
                whiteColorOverlay.setBackgroundColor(Color.BLUE);
            } else {
                whiteColorOverlay.setBackgroundColor(Color.RED);
            }
            // Toggle the color flag
            isColor1 = !isColor1;
            // Repeat after a certain interval (in milliseconds)
            handler.postDelayed(this, 1000); // Change color every second
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        handler = new Handler();

        textView = binding.textHome;
        sendInput = binding.editTextTextMultiLine;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        whiteColorOverlay = binding.whiteColorOverlay;
        whiteColorOverlay.bringToFront();
        cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendData();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        binding.isTorch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isFlashlightMode = true;
                } else {
                    isFlashlightMode = false;
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void sendData() throws InterruptedException {
        String msg = String.valueOf(sendInput.getText());
        textView.setText(msg);

        if (isFlashlightMode) {
            sendAsciiDataToTorch(msg);
        } else {
            sendAsciiDataToDisplay(msg);
        }
    }

    private void turnFlashlightOn() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void turnFlashlightOff() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void maximizeScreenBrightness() {
        if (getActivity() == null || getActivity().getWindow() == null) {
            return;
        }

        Window window = getActivity().getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();

        mPreviousBrightness = attributes.screenBrightness;
        attributes.screenBrightness = 1f;
        window.setAttributes(attributes);
    }

    private void restoreScreenBrightness() {
        if (getActivity() == null || getActivity().getWindow() == null) {
            return;
        }

        Window window = getActivity().getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();

        attributes.screenBrightness = mPreviousBrightness;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                window.setAttributes(attributes);
            }
        });
    }

    private void sendAsciiDataToTorch(String data) {

        try {
            cameraId = cameraManager.getCameraIdList()[0]; // use the first camera
            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                String binaryString = Integer.toBinaryString(c); // convert the character to binary string
                if (binaryString.length() < 8) {
                    binaryString = String.join("", Collections.nCopies((8 - binaryString.length()), "0")) + binaryString;
                }
                System.out.println(String.format("%c : %s", c, binaryString));
                for (int j = 0; j < binaryString.length(); j++) {

                    if (binaryString.charAt(j) == '1') {
                        turnFlashlightOn();

                        Thread.sleep(BIT_DURATION_1);

                        turnFlashlightOff();
                        Thread.sleep(150);
                    } else if (binaryString.charAt(j) == '0') {
                        turnFlashlightOn();

                        Thread.sleep(BIT_DURATION_0);

                        turnFlashlightOff();
                        Thread.sleep(150);
                    }
                }
                counter++;
//                progressBar.setProgress(counter);
            }

            Log.i(TAG, String.valueOf(counter));

        } catch (Exception e) {
            e.printStackTrace();
        }

//        whiteColorOverlay.setVisibility(View.INVISIBLE);
    }

    private void sendAsciiDataToDisplay(String data) {
        maximizeScreenBrightness();
        whiteColorOverlay.setVisibility(View.VISIBLE);
        final Activity activity = getActivity();
        Log.i("marker", "sending data to display");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraId = cameraManager.getCameraIdList()[0]; // use the first camera
                    for (int i = 0; i < data.length(); i++) {
                        char c = data.charAt(i);
                        String binaryString = Integer.toBinaryString(c); // convert the character to binary string
                        if (binaryString.length() < 8) {
                            binaryString = String.join("", Collections.nCopies((8 - binaryString.length()), "0")) + binaryString;
                        }
                        System.out.println(String.format("%c : %s", c, binaryString));
                        for (int j = 0; j < binaryString.length(); j++) {

                            if (binaryString.charAt(j) == '1') {
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            whiteColorOverlay.setBackgroundColor(getResources().getColor(R.color.white));
                                        }
                                    });
                                } else {
                                    Log.e("activity", "activity null");
                                }
                                Thread.sleep(BIT_DURATION_1);
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            whiteColorOverlay.setBackgroundColor(getResources().getColor(R.color.black));
                                        }
                                    });
                                } else {
                                    Log.e("activity", "activity null");
                                }
                                Thread.sleep(150);
                            } else if (binaryString.charAt(j) == '0') {
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            whiteColorOverlay.setBackgroundColor(getResources().getColor(R.color.white));
                                        }
                                    });
                                } else {
                                    Log.e("activity", "activity null");
                                }
                                Thread.sleep(BIT_DURATION_0);
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            whiteColorOverlay.setBackgroundColor(getResources().getColor(R.color.black));
                                        }
                                    });
                                } else {
                                    Log.e("activity", "activity null");
                                }
                                Thread.sleep(150);
                            }
                        }
                        counter++;
//                progressBar.setProgress(counter);
                    }
                    restoreScreenBrightness();
                    whiteColorOverlay.setVisibility(View.INVISIBLE);
                    Log.i(TAG, String.valueOf(counter));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}