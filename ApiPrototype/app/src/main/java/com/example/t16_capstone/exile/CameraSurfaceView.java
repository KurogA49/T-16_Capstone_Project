/*
package com.example.t16_capstone;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder holder;
    Camera camera = null;

    public CameraSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        holder = getHolder();
        Log.d("dd", "surfaceView init");
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            int cameraId = 0;
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);

                // 전면 카메라를 쓸 것인지 후면 카메라를 쓸것인지 설정 시
                // 전면카메라 사용시 CAMERA_FACING_FRONT 로 조건절
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    cameraId = i;
            }

            if (camera == null) {
                camera = Camera.open(cameraId);
                Log.d("dd", "surface created");
            }

            // 카메라 설정
            Camera.Parameters parameters = camera.getParameters();

            // 카메라의 회전이 가로/세로일때 화면을 설정한다.
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            camera.setParameters(parameters);


            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public boolean capture(Camera.PictureCallback callback) {
        if (camera != null) {
            camera.takePicture(null, null, callback);
            return true;
        } else {
            return false;
        }
    }
}
*/