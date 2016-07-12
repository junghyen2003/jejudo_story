package com.namooplus.jejurizmandroid.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import static com.namooplus.jejurizmandroid.common.AppSetting.SAVE_IMAGE_PATH;
import static com.namooplus.jejurizmandroid.common.AppSetting.SAVE_IMAGE_TEMP_PATH;

/**
 * Created by HeungSun-AndBut on 2016. 6. 5..
 */

public class Utils {
    public static int dpToPx(Context c, int dp) {
        return (int) (dp * c.getResources().getSystem().getDisplayMetrics().density);
    }

    public static Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    public static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }


    public static final boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }

    public static String saveBitmapToFile(Bitmap mBit, int num) {
        Log.i("HS", "start");
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(SAVE_IMAGE_TEMP_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file = new File(SAVE_IMAGE_PATH + num + ".jpg");
            if (file.exists()) {
                file.delete();
            }

            fos = new FileOutputStream(file);

            mBit.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();

        } catch (final Exception e) {

        } finally {
            try {
                mBit.recycle();
                if (fos != null) {
                    fos.close();
                }
            } catch (final Exception e) {
            }
        }
        Log.i("HS", "end");
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return null;
        }

    }

}
