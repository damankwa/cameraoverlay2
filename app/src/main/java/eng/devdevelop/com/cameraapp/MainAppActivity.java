package eng.devdevelop.com.cameraapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import android.view.ViewGroup.LayoutParams;


public class MainAppActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    ImageView CapturedImage;
    Uri imageFileUri;
    LayoutInflater controlInflater;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        //get frame layout
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        //Add camera to the Frame Layout
        preview.addView(mCameraPreview);


        //ImageButton captureButton = (ImageButton) findViewById(R.id.takepicture);
        //((ViewGroup)captureButton.getParent()).removeView(captureButton);
        controlInflater = LayoutInflater.from(getApplicationContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        ImageButton captureButton = (ImageButton) viewControl.findViewById(R.id.takepicture);
        preview.addView(viewControl);
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
             imageFileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());
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

            Intent i = new Intent(getApplicationContext(), EditPictureActivity.class);
            i.putExtra("new_camerapic_uri",imageFileUri.toString());
            startActivity(i);

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
    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", imageFileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageFileUri = savedInstanceState.getParcelable("file_uri");
    }
}