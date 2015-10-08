package com.iris.eyeiris;

/**import opencv  **/
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
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


public class MainActivity extends Activity {
    Button btnProcess;
    Bitmap srcBitmap;
    Bitmap grayBitmap;
    Bitmap MaskBitmap;
    ImageView imgLena;
    TextView OpCVversion;


    private static final String TAG = "MainActivity";

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
        OpCVversion = (TextView)findViewById(R.id.textView3);


        OpCVversion.setText("Ver: " + OpenCVLoader.OPENCV_VERSION_2_4_9);
        Log.i(TAG, "initUI sucess...");

    }

    /*将图片转为灰度图*/
    public void procSrc2Gray(){
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lena);
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        Log.i(TAG, "procSrc2Gray sucess...");
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
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        Log.i(TAG, "onResume sucess load OpenCV...");

    }
}
