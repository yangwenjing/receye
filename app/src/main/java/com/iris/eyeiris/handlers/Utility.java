package com.iris.eyeiris.handlers;

import android.graphics.Bitmap;
import android.util.Log;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;




import java.net.DatagramSocketImpl;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywj on 15/11/2.
 */
public class Utility {

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

    public static Mat procShapen(Mat src) {
        try {
            Mat kernel = new Mat(3, 3, CvType.CV_32F, new Scalar(-1));

            Mat dst = new Mat();
            double [] arr = {8.9};
            kernel.put(1, 1, arr);

            Imgproc.filter2D(src, dst, src.depth(), kernel);

            return dst;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "图像锐化失败");
            return null;
        } finally {
            Log.i(TAG, "图像锐化完成");
        }

    }
//

    /**
     * Surf模块不开源
     * @param src 原图
     * @return
     */

    public static Mat procSurfFeature2d(Mat src) {

        try {

            FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.FAST);
            MatOfKeyPoint keyPoints = new MatOfKeyPoint();

            Mat featureLines = new Mat();
            //检测surf特征点
            featureDetector.detect(src, keyPoints);

            //描述surf特征点
            DescriptorExtractor descriptorExtractor =
                    DescriptorExtractor.create(DescriptorExtractor.FREAK);
            Mat descript = new Mat();
            descriptorExtractor.compute(src, keyPoints, descript);
            return descript;
        } catch (Exception e) {
            Log.e(TAG, "特征提取");
            e.printStackTrace();
            return null;
        } finally {
            Log.i(TAG, "Surf特征提取结束");
        }
    }

    public static Mat procLinearPolar(Mat src) {
        try{
            Mat dist = new Mat();
            Imgproc.linearPolar(src, dist,new Point(src.width()/2.0, src.height()/2.0), src.width()/2.0,
                    Imgproc.WARP_FILL_OUTLIERS);

            return dist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Mat procContourlet(Mat src) {
        try {
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(src, contours, new Mat(),
                    Imgproc.RETR_LIST,
                    Imgproc.CHAIN_APPROX_NONE);
            Imgproc.drawContours(src, contours, -1, new Scalar(255, 0, 0), 2);

            //TODO: 如何将MatofPoint向量变为Mat


        } catch (Exception e) {
            Log.e(TAG, "procContourlet 出错!");
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            Log.i(TAG, "procContourlet 完成!");
        }
        return src;
    }

//
//
//    /**
//     * 计算直方图
//     * 绘制成功，但是为什么直方图的值那么小呢？
//     * 因为全市灰度图
//     */
//    public void procHistgram() {
//
//        MatOfInt channels = new MatOfInt(0);
//        Mat hist = new Mat();
//        MatOfInt histSize = new MatOfInt(256);
//        MatOfFloat range = new MatOfFloat(0, 256);
//        List<Mat> mats = new ArrayList<Mat>();
//
//        mats.add(targetMat);
//        Log.i(TAG, "Type:" + targetMat.type());
//
//        Imgproc.calcHist(mats, channels, Mat.ones(targetMat.size(), targetMat.type()),
//                hist,
//                histSize,
//                range
//        );
//
//        int height =256, scale =3;
//        Mat histImg = Mat.zeros(height, height*scale, grayMat.type());
//
//        Core.MinMaxLocResult locRes = Core.minMaxLoc(hist);
//        for(int i=0; i<256; i++) {
//            float[] value = new float[1];
//
//            hist.get(0, i, value);
////            int drawHeight = (int)Math.round(value[0]*height/(locRes.maxVal-locRes.minVal));
//            int drawHeight = (int)Math.round(value[0]*height/(locRes.maxVal-locRes.minVal));
//
//
//
//            Imgproc.rectangle(histImg, new Point(i*2+100, drawHeight-1),
//                    new Point((i+1)*2+100, 0), new Scalar(255, 0, 255));
//        }
//
//        Bitmap histBitmap = Bitmap.createBitmap(histImg.width(), histImg.height(), Bitmap.Config.RGB_565);
//        Utils.matToBitmap(histImg, histBitmap);
//        grayBitmap = histBitmap;
//
//    }

    public static Mat dilate(Mat src, int sizePara) {
        try {
            Mat dst = new Mat();

            Size size1 = new Size(2 * sizePara + 1, 2 * sizePara + 1);
            Point point = new Point(sizePara, sizePara);
            Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, size1, point);

            Imgproc.dilate(src, dst, element);

            return dst;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Log.i(TAG, "腐蚀操作结束");
        }
    }

    public static Mat getSubImg(Mat src, Mat circleMat) {
        try {
            float circle[] = new float[3];

            circleMat.get(0,0, circle);

            Mat mask = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0,0,0));
            int outerRadius = (int)(circle[2]*2);

            Point center = new Point((int)Math.round(circle[0]), (int)Math.round(circle[1]));
            Imgproc.circle(mask, center , outerRadius, new Scalar(255, 0, 0), Core.FILLED);
            Imgproc.circle(mask, center, (int)circle[2], new Scalar(0, 0, 0), Core.FILLED);

            Mat dist = new Mat();
            src.copyTo(dist, mask);
            Rect roi = new Rect(new Point(circle[0]-outerRadius, circle[1]-outerRadius),
                    new Point(circle[0]+outerRadius, circle[1]+outerRadius));

            Mat dist2 = new Mat(dist, roi);

            return dist2;
        }catch (Exception e) {
            Log.e(TAG, "获取子图出错!");
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            Log.i(TAG, "制作掩码结束");
        }

    }

//
    /*hough变换识别元*/
    public static Mat procSrc2CircleSrc(Mat mat) {
        /**
         * 已经获取了灰度图
         * Hough Circles
         */
        Mat circles = new Mat();

        int mindist = (int)(mat.size().height*0.1),
                cannyThreld = 10,
                roundTimes = 10;
        int minRadius = (int)(mat.size().height*0.1);
        int maxRadius = minRadius + 1, radius = minRadius*5;
        int seed = (int)(mat.size().height*0.05);
        boolean flag = false;
        while (maxRadius < radius && maxRadius > mindist) {
            Imgproc.HoughCircles(mat, circles, Imgproc.HOUGH_GRADIENT, 1,
                    mindist, //mindist
                    cannyThreld, //canny
                    roundTimes, //迭代次数
                    minRadius,
                    maxRadius);

            if(circles.cols() == 1) {
                break;
            } else if (circles.cols() > 1) {
                if(flag == false) {
                    seed = (int)(seed * 0.5);
                    flag = true;
                }
                maxRadius = maxRadius - seed;
            } else {
                if (flag == true) {
                    seed = (int)(seed*0.5);
                }
                maxRadius = maxRadius + seed;
            }
        }

        return circles;

    }
//

    /**
     * 通过Canny检测，获取边缘
     * @param src
     * @param thred1
     * @param thred2
     * @param L2gradient
     * @return
     */
    public static Mat procCannyCheck(Mat src, double thred1, double thred2, boolean L2gradient) {

        /**
         * 参数1， 低于threhold的点不作为边缘
         * 参数2，高于threhold的点不作为边缘
         */

        Mat edges = new Mat();
        Imgproc.Canny(src, edges, thred1, thred2);
        return edges;

    }

    public static Mat procThreshold(Mat src, double thred) {

        try {
            Mat dstMat = new Mat();

            Imgproc.threshold(src, dstMat, thred, 255, Imgproc.THRESH_BINARY);

            return dstMat;

        }catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "二值化失败");
            return null;
        } finally {
             Log.i(TAG, "二值化结束");
        }


    }

}