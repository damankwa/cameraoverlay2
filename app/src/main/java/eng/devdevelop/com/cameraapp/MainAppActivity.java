package eng.devdevelop.com.cameraapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.Timestamp;

import android.view.ViewGroup.LayoutParams;

import eng.devdevelop.com.cameraapp.util.BitmapUtil;


public class MainAppActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private int cameraId;
    FrameLayout preview;
    ImageView CapturedImage;
    Uri imageFileUri;
    LayoutInflater controlInflater;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_app);

        // camera surface view created
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

        mCamera = getCameraInstance(cameraId);
        mCameraPreview = new CameraPreview(this, mCamera, cameraId);
        //get frame layout
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        //Add camera to the Frame Layout 0 makes view be the first view to display in the layout. Without index 0, the camera preview will be on top of the buttons
        preview.addView(mCameraPreview, 0);

        ImageButton captureButton = (ImageButton)findViewById(R.id.takepicture);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamera.takePicture(null, null, mPicture);

            }
        });

        Button flipCamera = (Button) findViewById(R.id.saveTake);
        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              flipCamera();

            }
        });



    }

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     * @return
     */
    private Camera getCameraInstance(int cameraId) {
        Camera camera = null;
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    private void flipCamera() {
         cameraId = (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK);
        if (mCamera != null) {
            mCameraPreview.getHolder().removeCallback(mCameraPreview);
            //mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            try {

               // mCamera = Camera.open(id);
                createCamera(cameraId);
               // mCamera.setPreviewDisplay(mCameraPreview.getHolder());
               // mCamera.startPreview();
            }
            catch (final Exception e) {
                mCamera = null;
                e.printStackTrace();
            }
        }

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


            File imageFile = null;
            int rotatedeg;

            try {
                // convert byte array into bitmap
                Bitmap loadedImageBitmap = null;
                Bitmap rotatedBitmap = null;
                loadedImageBitmap = BitmapFactory.decodeByteArray(data, 0,
                        data.length);

                android.hardware.Camera.CameraInfo info =
                        new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(cameraId, info);

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    rotatedeg = -90;
                } else {  // back-facing camera
                   rotatedeg = 90;
                }

                /*
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                try {
                    display.getRealSize(size);
                }catch (NoSuchMethodError e){
                    display.getSize(size);
                }
                int widthpx = size.x;
                int heightpx = size.y;
                */

                Display display = getWindowManager().getDefaultDisplay();
                int realWidthpx;
                int realHeightpx;

                if (Build.VERSION.SDK_INT >= 17){
                    //new pleasant way to get real metrics
                    DisplayMetrics realMetrics = new DisplayMetrics();
                    display.getRealMetrics(realMetrics);
                    realWidthpx = realMetrics.widthPixels;
                    realHeightpx = realMetrics.heightPixels;

                } else if (Build.VERSION.SDK_INT >= 14) {
                    //reflection for this weird in-between time
                    try {
                        Method mGetRawH = Display.class.getMethod("getRawHeight");
                        Method mGetRawW = Display.class.getMethod("getRawWidth");
                        realWidthpx = (Integer) mGetRawW.invoke(display);
                        realHeightpx = (Integer) mGetRawH.invoke(display);
                    } catch (Exception e) {
                        //this may not be 100% accurate, but it's all we've got
                        realWidthpx = display.getWidth();
                        realHeightpx = display.getHeight();
                        Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
                    }

                } else {
                    //This should be close, as lower API devices should not have window navigation bars
                    realWidthpx = display.getWidth();
                    realHeightpx = display.getHeight();
                }

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

                int widthpxtodp = (int) ((realWidthpx/displayMetrics.density)+0.5);
                int heightpxtodp = (int) ((realHeightpx/displayMetrics.density)+0.5);

                rotatedBitmap = BitmapUtil.getRotatedAndScaledBitmap(loadedImageBitmap,rotatedeg,widthpxtodp,heightpxtodp);

                String state = Environment.getExternalStorageState();
                File folder = null;
                if (state.contains(Environment.MEDIA_MOUNTED)) {
                    folder = new File(Environment
                            .getExternalStorageDirectory() + "/DevDevelopCameraApp");
                } else {
                    folder = new File(Environment
                            .getExternalStorageDirectory() + "/DevDevelopCameraApp");
                }

                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    java.util.Date date = new java.util.Date();
                    imageFile = new File(folder.getAbsolutePath()
                            + File.separator
                            + new Timestamp(date.getTime()).toString()
                            + "Image.jpg");

                    imageFile.createNewFile();
                } else {
                    Toast.makeText(getBaseContext(), "Image Not saved",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                //write the roatated image to ostream
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                FileOutputStream fout = new FileOutputStream(imageFile);
                // save image into gallery

                fout.write(ostream.toByteArray());
                fout.close();
                /*
                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.DATE_TAKEN,
                        System.currentTimeMillis());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.DATA,
                        Uri.fromFile(imageFile).toString());

                MainAppActivity.this.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                */

            } catch (IOException e) {
                Toast t = Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT);
                t.show();
            }

            Intent i = new Intent(getApplicationContext(), EditPictureActivity.class);
            //i.putExtra("new_camerapic_uri",imageFileUri.toString());
            i.putExtra("new_camerapic_uri",Uri.fromFile(imageFile).toString());
            startActivity(i);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // Creating the camera
        if (mCamera == null) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            createCamera(cameraId);
        }

    }

    private void createCamera(int cameraId) {
        // Create an instance of Camera

        mCamera = getCameraInstance(cameraId);

        //this creates a new holder callback etc so make sure to release onpause
        mCameraPreview = new CameraPreview(this, mCamera, cameraId);

        //get frame layout
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeViewAt(0);
        //Add camera to the Frame Layout, make it the first child of index 0
        preview.addView(mCameraPreview,0);


    }

    @Override
    protected void onPause() {
        super.onPause();
        // release the camera immediately on pause event
        //also remove holder callback for preview
        releaseCamera();


    }

    private void releaseCamera() {
        if (mCamera != null) {

            mCameraPreview.getHolder().removeCallback(mCameraPreview);
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

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