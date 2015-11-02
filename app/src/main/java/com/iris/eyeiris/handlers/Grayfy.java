package com.iris.eyeiris.handlers;

import android.graphics.Bitmap;
import android.util.Log;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by ywj on 15/11/2.
 */
public class Grayfy {

    private static final String TAG = "GRAYFY";

    public static Mat procSrc2Gray(Bitmap srcBitmap){

        try {
            Mat rgbMat = new Mat();
            Mat grayMat = new Mat();
            Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
            Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat

            Log.i(TAG, "procSrc2Gray sucess...");
            return grayMat;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Log.i(TAG, "灰度化完成");
        }


    }
}
