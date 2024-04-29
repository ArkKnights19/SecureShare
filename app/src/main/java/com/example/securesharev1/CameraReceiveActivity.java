package com.example.securesharev1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CameraReceiveActivity extends CameraActivity {
    String TAG = "CameraActivity";

    CameraBridgeViewBase cameraBridgeViewBase;
    TextView textView;
    int detectedFrames = 0;
    int notDetectedFrames = 0;
    int frames = 0;
    String detectedString = "";
    String detectedStringWindow = "";
    String detectedText = "";
    int textLength = 0;
    //            boolean readingStarted = false;
    List<Integer> detectionList = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (OpenCVLoader.initDebug()) Log.d(TAG, "success");
        else Log.d(TAG, "err");
        Log.d(TAG, "ONCREATE");
        cameraBridgeViewBase = findViewById(R.id.cameraView);
        textView = findViewById(R.id.receivedMsg);


        cameraBridgeViewBase.setCvCameraViewListener(
                new CameraBridgeViewBase.CvCameraViewListener2() {

                    @Override
                    public void onCameraViewStarted(int width, int height) {

                    }

                    @Override
                    public void onCameraViewStopped() {

                    }

                    @Override
                    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                        return processFrame(inputFrame);
                    }
                }
        );

        if (OpenCVLoader.initDebug()) {
            cameraBridgeViewBase.enableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
//
//        return null;
//    }

    private Mat processFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        Mat gray = new Mat();
        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_BGR2GRAY);
        Mat bw = new Mat();
//                Core.bitwise_not(bw, bw);
        Imgproc.threshold(gray, bw, 254, 255, Imgproc.THRESH_BINARY);


        // getting pixel value
        // Find contours

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bw, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);


        double maxCounterArea = 0;
        // Process each contour
        for (MatOfPoint contour : contours) {
            // Calculate area
            Mat contourMat = new Mat();
            contour.convertTo(contourMat, CvType.CV_32SC2);
            double area = Imgproc.contourArea(contourMat);
            if (maxCounterArea < area) {
                maxCounterArea = area;
            }
        }

        if ((maxCounterArea > 20000)) {
            // Further filter by shape and other properties
//                    detectionList.add(1);
            notDetectedFrames = 0;
            detectedFrames++;

            // This contour is a candidate region where the flashlight is present
        } else {

            notDetectedFrames++;
            //4
            if (notDetectedFrames > 2) {
                if (detectedFrames >= 5) {
                    detectedString = detectedString + "0";
                    detectedStringWindow = detectedStringWindow + "0";
                    Log.d("FLASH", "String.................." + detectedString);
                    String showText = detectedString + "  ..  " + detectedText;
                    Log.d("FLASH", "0 DETECTED .................." + Integer.toString(detectedFrames));
                } else if ((detectedFrames > 1) & (detectedString.length() > 0)) {
                    detectedString = detectedString + "1";
                    detectedStringWindow = detectedStringWindow + "1";
                    Log.d("FLASH", "String.................." + detectedString);
                    String showText = detectedString + "  ..  " + detectedText;
                    Log.d("FLASH", "1 DETECTED .................." + Integer.toString(detectedFrames));
                }

                detectedFrames = 0;


                // convert byte to string
                if (detectedStringWindow.length() >= 8) {
                    int checkStringlength = detectedStringWindow.length();
                    String checkString = detectedStringWindow.substring(checkStringlength - 8, checkStringlength);
                    int number = Integer.parseInt(checkString, 2);

                    //  receiving the length of text
                    if (textLength == 0) {
                        textLength = number;
                    } else {
                        char ch = (char) number;
                        detectedText = detectedText + ch;
                        String showText = detectedText;
                        this.runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(showText);
                                    }
                                }
                        );

                    }
//                          if length is match
                    if (detectedText.length() == textLength) {
                        Log.i("FF", detectedText);
//                        Intent intent = new Intent(CameraScreen.this, ReceiveActivity.class);
//                        intent.putExtra("key", detectedText);
//                        startActivity(intent);
                    }


                    detectedStringWindow = "";
                }

            }
        }
        return bw;
    }

}