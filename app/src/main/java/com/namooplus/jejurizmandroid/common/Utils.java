package com.namooplus.jejurizmandroid.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.namooplus.jejurizmandroid.model.ImageInfoModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

import static com.namooplus.jejurizmandroid.common.AppSetting.IMAGE_STRING_FORMAT;
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


    public static String saveByteToFile(byte[] image) {
        File file = null;
        FileOutputStream fos = null;
        try {
            File dir = new File(SAVE_IMAGE_TEMP_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file = new File(dir, Calendar.getInstance().getTimeInMillis() + IMAGE_STRING_FORMAT);
            if (file.exists()) {
                file.delete();
            }

            fos = new FileOutputStream(file);

            fos.write(image);
            fos.close();
        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

        return file.getAbsolutePath();
    }

    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if(src == null)
            return null;

        int width = src.getWidth();
        int height = src.getHeight();

        if(width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if(width > w)
            x = (width - w)/2;

        if(height > h)
            y = (height - h)/2;

        int cw = w; // crop width
        int ch = h; // crop height

        if(w > width)
            cw = width;

        if(h > height)
            ch = height;


        return Bitmap.createBitmap(src, x, y, cw, ch);
    }


    public static String saveBitmapToFile(Bitmap mBit) {
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(SAVE_IMAGE_TEMP_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file = new File(dir, Calendar.getInstance().getTimeInMillis() + IMAGE_STRING_FORMAT);
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
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return null;
        }

    }

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public static void deleteDir(File filePath) {
        try {
            if (filePath.isDirectory()) {
                // listFiles() may return null when I/O error (ex. Permission not allowed)
                if (filePath.listFiles() != null) {
                    // If directory, traverse down the directory
                    for (File fileDelete : filePath.listFiles()) {
                        deleteDir(fileDelete);
                    }
                    filePath.delete();
                }
            } else {
                //if file, then delete it
                filePath.delete();
            }
        } catch (Exception e) {
        }
    }

    public static String getNanoToMilllisecond(long nanoTime) {
        return nanoTime / 1000000.0 + "ms";
    }

    public static void deleteFile(ArrayList<ImageInfoModel> list) {
        File file = null;
        for (ImageInfoModel item : list) {
            file = new File(item.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
