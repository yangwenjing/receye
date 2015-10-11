package com.iris.eyeiris;

/**import opencv  **/
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.res.Resources;
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

import java.util.Vector;



public class MainActivity extends Activity {
    Button btnProcess;
    Bitmap srcBitmap;
    Bitmap grayBitmap;
    Bitmap MaskBitmap;
    ImageView imgLena;
    TextView OpCVversion;
    Mat grayMat = null;


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
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        Log.i(TAG, "procSrc2Gray sucess...");

//        procSrc2CircleSrc(); //hough检测
        procCannyCheck(); //边缘检测
    }

    private int iRound(double x){
        int y;
        if(x >= (int)x+0.5)
        y = (int)x++;
        else
        y = (int)x;
        return y;
    }

    /*hough变换识别元*/
    public void procSrc2CircleSrc() {
       if(grayMat == null)
           procSrc2Gray();
        /**
         * 已经获取了灰度图
         * Hough Circles
         */

        Mat circles = new Mat();
//        Imgproc.HoughCircles(grayMat, circles, Imgproc.HOUGH_GRADIENT, 1,
//                10, 140,
//                120, 1, (int) (grayMat.rows() * 0.3));

        //TODO: 如何确定Hough变换的参数
        Imgproc.HoughCircles(grayMat, circles, Imgproc.HOUGH_GRADIENT, 1,
                20, //mindist
                80, //canny
                20, //迭代次数
                (int)(grayMat.size().height*0.1),
                (int)(grayMat.size().height*0.4));

        //
        Log.i(TAG, "circles" + circles.cols() +"," +circles.rows());
        Log.i(TAG, "circles" + circles.size().height +"," +circles.size().width);
        float circle[] = new float[3];

        for (int i = 0; i < circles.cols(); i++)
        {
            circles.get(0, i, circle);
            Point center = new Point();
            center.x = iRound(circle[0]);
            center.y = iRound(circle[1]);
            Imgproc.circle(grayMat, center, (int) circle[2], new
                    Scalar(255, 255, 0, 255), 4);
        }

        Utils.matToBitmap(grayMat,
                grayBitmap);

        Log.i(TAG, "Hough circles sucess...");

    }

    public void procCannyCheck() {
        if(grayMat == null)
            procSrc2Gray();

        /**
         * TODO: Canny检测
         * 参数1， 低于threhold的点不作为边缘
         * 参数2，高于threhold的点不作为边缘
         */

        Mat edges = new Mat();
        int thre1 = 1, thre2 = 300;
        Imgproc.Canny(grayMat, edges, thre1, thre2);

        Utils.matToBitmap(edges,
                grayBitmap);

        Log.i(TAG, "边缘检测完成3");

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
