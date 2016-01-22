package eng.devdevelop.com.cameraapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;

import eng.devdevelop.com.cameraapp.util.BitmapUtil;

public class EditPictureActivity extends Activity {
    ImageView iv,dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);

        FrameLayout editpreview = (FrameLayout) findViewById(R.id.edit_preview);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Uri imageFileUri = Uri.parse(extras.getString("new_camerapic_uri"));
            Display currentDisplay = getWindowManager().getDefaultDisplay();
            int dw = currentDisplay.getWidth();
            int dh = currentDisplay.getHeight();
            try {
                // Load up the image's dimensions not the image itself
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

                bmpFactoryOptions.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);

                bmpFactoryOptions.inSampleSize = BitmapUtil.calculateInSampleSize(bmpFactoryOptions, 480, 480);

                bmpFactoryOptions.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);

                //rotates image by 90because by default it saves image as landscape
                // Bitmap scaledBitmap = BitmapUtil.getRotatedAndScaledBitmap(bmp);

                iv = new ImageView(this);
                iv.setDrawingCacheEnabled(true);
                iv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                iv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                iv.setImageBitmap(bmp);


                editpreview.addView(iv);

                dv = new EditPictureView(this);
                dv.setDrawingCacheEnabled(true);
                dv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                dv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

                editpreview.addView(dv);

                Button saveButton = new Button(this);
                saveButton.setText("save");
                saveButton.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT));

                ((FrameLayout.LayoutParams) saveButton.getLayoutParams()).gravity = Gravity.TOP| Gravity.CENTER_HORIZONTAL;



                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap backgrndbmp = Bitmap.createBitmap(iv.getDrawingCache());
                        Bitmap foregrndbmp = Bitmap.createBitmap(dv.getDrawingCache());

                        Bitmap finalbmp = GetCombinedImage(backgrndbmp, foregrndbmp);

                        saveImage(finalbmp);
                    }

                });

                editpreview.addView(saveButton);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Something is wrong",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap GetCombinedImage(Bitmap background_img, Bitmap foreground_img){

        int width = 480, height = 320;
        Bitmap alternate_bmp;

        Matrix matrix = new Matrix();

        alternate_bmp = Bitmap.createBitmap(background_img,0,0,background_img.getWidth(),background_img.getHeight(),matrix,true);

        Canvas comboImage = new Canvas(alternate_bmp);
        //backgroundimg = Bitmap.createScaledBitmap(alternatebmp, width, height, false);
        //comboImage.drawBitmap(backgroundimg, matrix,null);
        comboImage.drawBitmap(foreground_img,matrix, null);

        return alternate_bmp;


    }

    public void saveImage(Bitmap finalbmp){

        try{

            File folder = null;
            File imageFile = null;
            String state = Environment.getExternalStorageState();
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
                        + "Imagefinal.jpg");

                imageFile.createNewFile();
            } else {
                Toast.makeText(getBaseContext(), "Image Not saved",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            //write the roatated image to ostream
            finalbmp.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            FileOutputStream fout = new FileOutputStream(imageFile);
            // save image into gallery

            fout.write(ostream.toByteArray());
            fout.close();

            Toast.makeText(getBaseContext(), "Image  saved",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Image not saved",
                    Toast.LENGTH_SHORT).show();
        }
    }



}

