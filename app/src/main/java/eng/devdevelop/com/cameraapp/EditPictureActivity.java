package eng.devdevelop.com.cameraapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class EditPictureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);
        ImageView displayImgView = (ImageView) this.findViewById(R.id.showImg);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Uri imageFileUri = Uri.parse(extras.getString("new_camerapic_uri"));
            Display currentDisplay = getWindowManager().getDefaultDisplay();
            int dw = currentDisplay.getWidth();
            int dh = currentDisplay.getHeight() ;
            try {
                // Load up the image's dimensions not the image itself
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

                bmpFactoryOptions.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);

                bmpFactoryOptions.inSampleSize = 1;

                bmpFactoryOptions.inJustDecodeBounds = false;
                 bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);

               //rotates image by 90because by default it saves image as landscape
                Bitmap scaledBitmap = getRotatePic(bmp);

                displayImgView.setImageBitmap(scaledBitmap);
            } catch (FileNotFoundException e) {
                Log.v("ERROR", e.toString());
            }
        }
    }
    public Bitmap getRotatePic(Bitmap bmp){
        //image retrieved from bitmap is rotated
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        int h = 320; // Height in pixels
        int w = 480; // Width in pixels

        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp , 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, h, w, true);
        return scaledBitmap;
    }

}

