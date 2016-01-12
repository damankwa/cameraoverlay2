package eng.devdevelop.com.cameraapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
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
    private Camera mCamera;


    // Constructor that obtains context and camera
    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

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
        mCamera.stopPreview();
        mCamera.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
                               int width, int height) {
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            Camera.Parameters parameters = mCamera.getParameters();
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            {
                parameters.set("orientation", "portrait");
                // For Android Version 2.2 and above
                mCamera.setDisplayOrientation(90);  //
                // For Android Version 2.0 and above
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
