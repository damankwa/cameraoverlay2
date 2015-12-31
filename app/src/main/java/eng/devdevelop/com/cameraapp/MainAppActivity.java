package eng.devdevelop.com.cameraapp;

import android.app.Activity;
import android.content.ContentValues;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;


public class MainAppActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mCameraPreview;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);

        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            /*
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            */
            Uri imageFileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());
            try {
                OutputStream ImgFileOs = getContentResolver().openOutputStream(imageFileUri);
                ImgFileOs.write(data);
                ImgFileOs.close();
            } catch (FileNotFoundException e) {
                Toast t = Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT);
                t.show();

            } catch (IOException e) {
                Toast t = Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT);
                t.show();
            }
        }
    };

 /*   private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }*/
}