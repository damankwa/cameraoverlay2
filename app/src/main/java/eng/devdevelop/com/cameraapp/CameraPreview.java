package eng.devdevelop.com.cameraapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;

import java.io.IOException;

/**
 * Created by David Amankwa Devdeveloper on 12/30/2015.
 */

public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder;
    public Camera mCamera;
    private int cameraId ;


    // Constructor that obtains context and camera
    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera, int cameraId) {
        super(context);
        this.mCamera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.cameraId = cameraId;

    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {


        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            // left blank for now
            Toast t = Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT);
            t.show();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
                               int width, int height) {


        if (mSurfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // start preview with new settings
        try {
            //from http://developer.android.com/reference/android/hardware/Camera.Parameters.html#setRotation(int)
            //for portrait orientation device is rotated 270 deg


            mCamera.setPreviewDisplay(mSurfaceHolder);
            Camera.Parameters parameters = mCamera.getParameters();
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            {
                parameters.set("orientation", "portrait");
                // For Android Version 2.2 and above make preview image not show in landscape
                /*
                android.hardware.Camera.CameraInfo info =
                        new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(cameraId, info);

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCamera.setDisplayOrientation(270);  //
                    // For Android Version 2.0 and above saves image in portrait mode
                    parameters.setRotation(270);
                } else {  // back-facing camera
                    mCamera.setDisplayOrientation(90);  //
                    // For Android Version 2.0 and above saves image in portrait mode
                    parameters.setRotation(90);
                }*/
                mCamera.setDisplayOrientation(90);  //
                // For Android Version 2.0 and above saves image in portrait mode
                parameters.setRotation(90);

            }

            mCamera.startPreview();


        } catch (Exception e) {
            // intentionally left blank for a test
            Toast t = Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT);
            t.show();
        }
    }


}
