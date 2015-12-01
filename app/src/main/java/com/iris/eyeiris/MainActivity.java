package com.iris.eyeiris;

/**import opencv  **/
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iris.eyeiris.adapters.ImagesDespAdapter;
import com.iris.eyeiris.entities.ImageDesp;
import com.iris.eyeiris.handlers.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    Button btnProcess;
    Bitmap srcBitmap;
    ImageView imgLena;
    Mat grayMat = null;

    List<ImageDesp> imageResults;

    private static final String TAG = "MainActivity";

    private static boolean flag = true;
    private static boolean isFirst = true;                      // Grey


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();


        loadImages();

        btnProcess.setOnClickListener(new ProcessClickListener());
    }

    private void loadImages() {
        GridView lv = (GridView) findViewById(R.id.results);

        lv.setAdapter(new ImagesDespAdapter(this, R.layout.image_desc_view, imageResults));
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
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lena);

        imageResults = new ArrayList<ImageDesp>();

        imageResults.add(new ImageDesp(srcBitmap, "原图")); //加入原图
        Log.i(TAG, "initUI sucess...");

    }

    private class ProcessClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(isFirst)
            {
                process();
                isFirst = false;
            }
        }

    }


    private void process() {
        try {

            grayMat = Utility.procSrc2Gray(srcBitmap);
            Bitmap bitmap = Bitmap.createBitmap(grayMat.width(), grayMat.height(), Config.RGB_565);

            Utils.matToBitmap(grayMat, bitmap);
            imageResults.add(new ImageDesp(bitmap, "灰度图"));

            /**
             * 锐化
             */

            Mat shapMat = Utility.procShapen(grayMat);
            Bitmap bitmap2 = Bitmap.createBitmap(shapMat.width(), shapMat.height(), Config.RGB_565);
            Utils.matToBitmap(shapMat, bitmap2);
            imageResults.add(new ImageDesp(bitmap2, "锐化图片"));

            Mat threMat = Utility.procThreshold(grayMat, 0.5);
            Bitmap bitmap3 = Bitmap.createBitmap(shapMat.width(), shapMat.height(), Config.RGB_565);
            Utils.matToBitmap(threMat, bitmap3);
            imageResults.add(new ImageDesp(bitmap3, "二值化图像"));


            Mat threMat2 = Utility.procThreshold(grayMat, 1);
            Bitmap bitmap4 = Bitmap.createBitmap(shapMat.width(), shapMat.height(), Config.RGB_565);
            Utils.matToBitmap(threMat2, bitmap4);
            imageResults.add(new ImageDesp(bitmap4, "二值化图像2"));


            Mat threMat3 = Utility.procAdaptiveThreshold(shapMat);
            Bitmap threBitmap = Bitmap.createBitmap(shapMat.width(), shapMat.height(), Config.RGB_565);
            Utils.matToBitmap(threMat3, threBitmap);
            imageResults.add(new ImageDesp(threBitmap, "自适应二值化图像"));


            Mat hist = Utility.procHistogram(grayMat);
            Bitmap histBitmap = Bitmap.createBitmap(hist.width(), hist.height(), Config.RGB_565);
            Utils.matToBitmap(hist, histBitmap);

            ImageView imageView = (ImageView) findViewById(R.id.histogram);

            imageView.setImageBitmap(histBitmap);


            Drawable drawable = new BitmapDrawable(histBitmap);





//
//            Mat edges = Utility.procCannyCheck(threMat, 0.3, 20, false);
//            Bitmap bitmap5 = Bitmap.createBitmap(edges.width(), edges.height(), Config.RGB_565);
//            Utils.matToBitmap(edges, bitmap5);
//            imageResults.add(new ImageDesp(bitmap5, "边缘检测图像"));
//
//
//
//            Mat edges2 = Utility.procCannyCheck(threMat, 0.5, 1, false);
//            Bitmap bitmap6 = Bitmap.createBitmap(edges.width(), edges.height(), Config.RGB_565);
//            Utils.matToBitmap(edges2, bitmap6);
//            imageResults.add(new ImageDesp(bitmap6, "边缘检测图像"));
//
//            Mat circles = Utility.procSrc2CircleSrc(edges);
//            Mat subMat1 = Utility.getSubImg(grayMat, circles);
//            Bitmap bitmap7 = Bitmap.createBitmap(subMat1.width(), subMat1.height(), Config.RGB_565);
//            Utils.matToBitmap(subMat1, bitmap7);
//            imageResults.add(new ImageDesp(bitmap7, "Hough圆形检测"));
//
//
//
//            circles = Utility.procSrc2CircleSrc(edges2);
//            Mat subMat2 = Utility.getSubImg(grayMat, circles);
//            Bitmap bitmap8 = Bitmap.createBitmap(subMat2.width(), subMat2.height(), Config.RGB_565);
//            Utils.matToBitmap(subMat2, bitmap8);
//            imageResults.add(new ImageDesp(bitmap8, "Hough圆形检测,边缘检测"));
//
//
//            Mat polarMat = Utility.procLinearPolar(subMat2);
//
//            Bitmap bitmap9 = Bitmap.createBitmap(polarMat.width(), polarMat.height(), Config.RGB_565);
//            Utils.matToBitmap(polarMat, bitmap9);
//            imageResults.add(new ImageDesp(bitmap9, "图像展开"));
//
//
//            List<Mat> gaborMatList = Utility.procGabor(polarMat, 2*Math.PI, Math.PI*(0.5+0.125), CvType.CV_32F);
//
////            for(Mat mat: gaborMatList){
//                Bitmap bitmap10 = Bitmap.createBitmap(gaborMatList.get(1).width(),
//                        gaborMatList.get(1).height(),
//                        Config.RGB_565);
//                Utils.matToBitmap(gaborMatList.get(3), bitmap10);
//                imageResults.add(new ImageDesp(bitmap10, "Gabor"));
////            }



//
//            Mat logPolarMat = Utility.procLogPolar(grayMat);
//
//            Bitmap bitmap10 = Bitmap.createBitmap(logPolarMat.width(), logPolarMat.height(), Config.RGB_565);
//            Utils.matToBitmap(logPolarMat, bitmap10);
//            imageResults.add(new ImageDesp(bitmap10, "LogPolar图像展开"));

//            Mat descript = Utility.procSurfFeature2d(subMat2);
//            Bitmap bitmap9 = Bitmap.createBitmap(descript.width(),
//                    descript.height(), Config.RGB_565);
//            Utils.matToBitmap(descript, bitmap9);
//            imageResults.add(new ImageDesp(bitmap9, "Hough圆形检测,边缘检测"));

            /**
             * 截取目标区域
             */


            Log.i(TAG, "输出图像" + imageResults.size());

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.i(TAG, "Gabor整体处理完成");
            loadImages();
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
