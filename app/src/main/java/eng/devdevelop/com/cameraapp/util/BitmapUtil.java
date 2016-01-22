package eng.devdevelop.com.cameraapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.Display;

/**
 * Created by David on 1/14/2016.
 */
public class BitmapUtil {
    public static Bitmap getRotatedAndScaledBitmap(Bitmap bmp, int rotatedegrees, int dpWidth, int dpHeight){
        //image retrieved from bitmap is rotated
        Matrix matrix = new Matrix();
        //matrix.postRotate(rotatedegrees);
        matrix.preRotate(rotatedegrees);

        int h = 480; // Height in pixels actual device 1920
        int w = 320; // Width in pixels actual device 1080



        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp , 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, dpWidth, dpHeight, true);
        return scaledBitmap;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
