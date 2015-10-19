package com.iris.eyeiris;

/**import opencv  **/
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

import com.iris.eyeiris.handlers.LocateHandler;
import com.iris.eyeiris.handlers.SampleLocateHander;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    Button btnProcess;
    Bitmap srcBitmap;
    Bitmap grayBitmap;
    Bitmap MaskBitmap;
    ImageView imgLena;
    TextView OpCVversion;
    Mat grayMat = null;
    Mat cannyMat = null;
    Mat targetMat;
    Point center = null; //圆心
    int radius = 0; //半径

    private static final String TAG = "MainActivity";

    private static final String TAG_HF = "HoughCircle";

    private static boolean flag = true;
    private static boolean isFirst = true;                      // Grey

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        btnProcess.setOnClickListener(new ProcessClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initUI(){
        btnProcess = (Button)findViewById(R.id.button);
        imgLena = (ImageView)findViewById(R.id.imageView);
//        OpCVversion = (TextView)findViewById(R.id.textView3);


        Log.i(TAG, "initUI sucess...");

    }

    /*将图片转为灰度图*/
    public void procSrc2Gray(){
        Mat rgbMat = new Mat();
        grayMat = new Mat();

        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lena);
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Config.RGB_565);

        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.GaussianBlur(rgbMat, rgbMat, new Size(9, 9), 2, 2);

        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        Log.i(TAG, "procSrc2Gray sucess...");

//        procShapen(grayMat);
////        procCannyCheck(grayMat, 0.1, 80, false); //边缘检测
//        procSrc2CircleSrc(grayMat); //hough检测
        LocateHandler handler = new SampleLocateHander();
        Mat resMat = handler.handleLocateIris(grayMat);

        Bitmap resBitmap = Bitmap.createBitmap(resMat.width(), resMat.height(), Config.RGB_565);
        Utils.matToBitmap(resMat, resBitmap); //convert mat to bitmap
        grayBitmap = resBitmap;
//        getSubImg(grayMat);//先实现mask和图片定位, 纹理提取和归一化
//        procShapen(targetMat);
////        //再次边缘检测
//        procCannyCheck(targetMat, 0.01, 80, false);
////
//        procContourlet(cannyMat);
//        procHistgram();  //绘画处直方图, 直方图处理灰度图不行

    }

    public void procShapen(Mat src) {
        try {
            Mat kernel = new Mat(3, 3, CvType.CV_32F, new Scalar(-1));

            double [] arr = {8.9};
            kernel.put(1, 1, arr);

            Imgproc.filter2D(src, src, src.depth(), kernel);

            grayMat = src;
            targetMat = src;
            Utils.matToBitmap(src, grayBitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void procContourlet(Mat src) {
        try {
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(src, contours, new Mat(),
                    Imgproc.RETR_LIST,
                    Imgproc.CHAIN_APPROX_NONE);
            Imgproc.drawContours(src, contours, -1, new Scalar(255, 0, 0), 2);

            Utils.matToBitmap(src, grayBitmap);

        } catch (Exception e) {
            Log.e(TAG, "procContourlet 出错!");
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            Log.i(TAG, "procContourlet 完成!");
        }
    }

    /**
     * 计算直方图
     * 绘制成功，但是为什么直方图的值那么小呢？
     * 因为全市灰度图
     */
    public void procHistgram() {

        MatOfInt channels = new MatOfInt(0);
        Mat hist = new Mat();
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat range = new MatOfFloat(0, 256);
        List<Mat> mats = new ArrayList<Mat>();

        mats.add(targetMat);
        Log.i(TAG, "Type:" + targetMat.type());

        Imgproc.calcHist(mats, channels, Mat.ones(targetMat.size(), targetMat.type()),
                hist,
                histSize,
                range
        );

        int height =256, scale =3;
        Mat histImg = Mat.zeros(height, height*scale, grayMat.type());

        Core.MinMaxLocResult locRes = Core.minMaxLoc(hist);
        for(int i=0; i<256; i++) {
            float[] value = new float[1];

            hist.get(0, i, value);
//            int drawHeight = (int)Math.round(value[0]*height/(locRes.maxVal-locRes.minVal));
            int drawHeight = (int)Math.round(value[0]*height/(locRes.maxVal-locRes.minVal));



            Imgproc.rectangle(histImg, new Point(i*2+100, drawHeight-1),
                    new Point((i+1)*2+100, 0), new Scalar(255, 0, 255));
        }

        Bitmap histBitmap = Bitmap.createBitmap(histImg.width(), histImg.height(), Config.RGB_565);
        Utils.matToBitmap(histImg, histBitmap);
        grayBitmap = histBitmap;

    }

    public void getSubImg(Mat src) {
        try {
            Mat mask = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0,0,0));
            int outerRadius = (int)(radius*2);

            Imgproc.circle(mask, this.center, outerRadius, new Scalar(255, 0, 0), Core.FILLED);
            Imgproc.circle(mask, this.center, this.radius, new Scalar(0, 0, 0), Core.FILLED);

            Mat dist = new Mat();
            src.copyTo(dist, mask);
            Rect roi = new Rect(new Point(this.center.x-outerRadius, this.center.y-outerRadius),
                    new Point(this.center.x+outerRadius, this.center.y+outerRadius));

            Mat dist2 = new Mat(dist, roi);

            Bitmap newGrayBitmap = Bitmap.createBitmap(dist2.width(), dist2.height(), Config.RGB_565);
            Utils.matToBitmap(dist2, newGrayBitmap);
            targetMat = dist2;
            grayMat = dist2;
            grayBitmap = newGrayBitmap;
        }catch (Exception e) {
            Log.e(TAG, "获取子图出错!");
            Log.e(TAG, e.getMessage());
        } finally {
            Log.i(TAG, "制作掩码结束");
            Log.i(TAG, "radius:"+this.radius+", center (" +this.center.x+"," +this.center.y+")");
        }

    }

    /*hough变换识别元*/
    public void procSrc2CircleSrc(Mat mat) {
        /**
         * 已经获取了灰度图
         * Hough Circles
         */
        Mat circles = new Mat();

        //TODO: 如何确定Hough变换的参数
        int mindist = (int)(mat.size().height*0.1),
                cannyThreld = 30,
                roundTimes = 20;
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
        //
        float circle[] = new float[3];

        for (int i = 0; i < circles.cols(); i++)
        {
            circles.get(0, i, circle);
            Point center = new Point();
            center.x = Math.round(circle[0]);
            center.y = Math.round(circle[1]);

            if(i==0) {
                this.center = center;
                this.radius = (int) circle[2];
            }
            Imgproc.circle(mat, center, (int) circle[2], new
                    Scalar(255, 0, 255), 4);

        }

        Utils.matToBitmap(mat,
                grayBitmap);

        Log.i(TAG, "Hough circles sucess...: mindist "+ mindist +" ,canny_threld "+cannyThreld +
                " ,roundTimes" +roundTimes+ ", circles" +circles.cols());

    }

    public void procCannyCheck(Mat src, double thred1, double thred2, boolean L2gradient) {

        /**
         * 参数1， 低于threhold的点不作为边缘
         * 参数2，高于threhold的点不作为边缘
         */

        Mat edges = new Mat();
        Imgproc.Canny(src, edges, thred1, thred2);

        Utils.matToBitmap(edges,
                grayBitmap);

        cannyMat = edges; //保存边缘检测的结果
        Log.i(TAG, "边缘检测完成: thred1 +"+ thred1+",thred2"+thred2);

    }

    /**
     * 监听点击变为灰度图的事件
     */
    private class ProcessClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(isFirst)
            {
                procSrc2Gray();
                isFirst = false;
            }
            if(flag){
                imgLena.setImageBitmap(grayBitmap);
                btnProcess.setText("Origin");
                flag = false;
            }
            else{
                imgLena.setImageBitmap(srcBitmap);
                btnProcess.setText("Grey");
                flag = true;
            }
        }

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "Load success");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "Load fail");
                    break;
            }

        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //load OpenCV engine and init OpenCV library
        isFirst = true;
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        Log.i(TAG, "onResume sucess load OpenCV...");

    }

}
