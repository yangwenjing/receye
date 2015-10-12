package com.iris.eyeiris;

/**import opencv  **/
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
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


public class MainActivity extends Activity {
    Button btnProcess;
    Bitmap srcBitmap;
    Bitmap grayBitmap;
    Bitmap MaskBitmap;
    ImageView imgLena;
    TextView OpCVversion;
    Mat grayMat = null;
    Mat cannyMat = null;
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

        procCannyCheck(); //边缘检测
        procSrc2CircleSrc(cannyMat); //hough检测
        getSubImg();//先实现mask和图片定位, 纹理提取和归一化
        procHistgram();





    }

    /**
     * TODO: 计算直方图
     */
    public void procHistgram() {



    }

    public void getSubImg() {
        try {
            Mat mask = new Mat(grayMat.rows(), grayMat.cols(), grayMat.type(), new Scalar(0,0,0));
            int outerRadius = (int)(radius*2);

            Imgproc.circle(mask, this.center, outerRadius, new Scalar(255, 0, 0), Core.FILLED);
            Imgproc.circle(mask, this.center, this.radius, new Scalar(0, 0, 0), Core.FILLED);

            Mat dist = new Mat();
            grayMat.copyTo(dist, mask);
            Rect roi = new Rect(new Point(this.center.x-outerRadius, this.center.y-outerRadius),
                    new Point(this.center.x+outerRadius, this.center.y+outerRadius));

            Mat dist2 = new Mat(dist, roi);

            Bitmap newGrayBitmap = Bitmap.createBitmap(dist2.width(), dist2.height(), Config.RGB_565);
            Utils.matToBitmap(dist2, newGrayBitmap);

            grayBitmap = newGrayBitmap;
        }catch (Exception e) {
            Log.e(TAG, "直方图计算出错");
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
                cannyThreld = 20,
                roundTimes = 20;
        Imgproc.HoughCircles(mat, circles, Imgproc.HOUGH_GRADIENT, 1,
                mindist, //mindist
                cannyThreld, //canny
                 roundTimes, //迭代次数
                (int)(mat.size().height*0.1),
                (int)(mat.size().height*0.2));

        //
        Log.i(TAG, "circles" + circles.cols() +"," +circles.rows());
        Log.i(TAG, "circles" + circles.size().height +"," +circles.size().width);
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

            Imgproc.circle(mat, center, (int) circle[2] + 50, new
                    Scalar(255, 0, 255), 4);
        }

        Utils.matToBitmap(mat,
                grayBitmap);

        Log.i(TAG, "Hough circles sucess...: mindist "+ mindist +" ,canny_threld "+cannyThreld +
                " ,roundTimes" +roundTimes);

    }

    public void procCannyCheck() {
        if(grayMat == null)
            procSrc2Gray();

        /**
         * 参数1， 低于threhold的点不作为边缘
         * 参数2，高于threhold的点不作为边缘
         */

        Mat edges = new Mat();
        int thre1 = 1, thre2 = 80;
        Imgproc.Canny(grayMat, edges, thre1, thre2);

        Utils.matToBitmap(edges,
                grayBitmap);

        cannyMat = edges; //保存边缘检测的结果
        Log.i(TAG, "边缘检测完成: thred1 +"+ thre1+",thred2"+thre2);

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
