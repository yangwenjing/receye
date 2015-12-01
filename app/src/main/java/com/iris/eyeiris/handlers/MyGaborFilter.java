package com.example.admin.testandroid;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

/**
 * Created by Admin on 2015/11/20.
 */
public class MyGaborFilter {
    public static Mat getRealGaborKernel(Size ksize, double sigma, double theta, double lambd, double gamma, double psi)
    {
        double sigma_x = sigma;
        double sigma_y = sigma/gamma;
        int nstds = 3;
        int xmin, xmax, ymin, ymax;
        double c = Math.cos(theta), s = Math.sin(theta);
        if( ksize.width > 0 )
            xmax = (int)ksize.width/2;
        else
            xmax = (int)Math.round(Math.max(Math.abs(nstds * sigma_x * c), Math.abs(nstds * sigma_y * s)));

        if( ksize.height > 0 )
            ymax = (int)ksize.height/2;
        else
            ymax = (int)Math.round(Math.max(Math.abs(nstds * sigma_x * s), Math.abs(nstds * sigma_y * c)));

        xmin = -xmax;
        ymin = -ymax;

        Mat kernel = new Mat(ymax - ymin + 1, xmax - xmin + 1, CvType.CV_64F);
        double scale = 1;
        double ex = -0.5/(sigma_x*sigma_x);
        double ey = -0.5/(sigma_y*sigma_y);
        double cscale = Math.PI*2/lambd;

        for( int y = ymin; y <= ymax; y++ )
            for( int x = xmin; x <= xmax; x++ )
            {
                double xr = x*c + y*s;
                double yr = -x*s + y*c;

                double v = scale*Math.exp(ex*xr*xr + ey*yr*yr)*Math.cos(cscale * xr + psi);

                kernel.put(ymax - y, xmax - x, v);
            }

        return kernel;
    }

    public static Mat getImaginaryGaborKernel(Size ksize, double sigma, double theta, double lambd, double gamma, double psi)
    {
        double sigma_x = sigma;
        double sigma_y = sigma/gamma;
        int nstds = 3;
        int xmin, xmax, ymin, ymax;
        double c = Math.cos(theta), s = Math.sin(theta);
        if( ksize.width > 0 )
            xmax = (int)ksize.width/2;
        else
            xmax = (int)Math.round(Math.max(Math.abs(nstds * sigma_x * c), Math.abs(nstds * sigma_y * s)));

        if( ksize.height > 0 )
            ymax = (int)ksize.height/2;
        else
            ymax = (int)Math.round(Math.max(Math.abs(nstds * sigma_x * s), Math.abs(nstds * sigma_y * c)));

        xmin = -xmax;
        ymin = -ymax;


        Mat kernel = new Mat(ymax - ymin + 1, xmax - xmin + 1, CvType.CV_64F);
        double scale = 1;
        double ex = -0.5/(sigma_x*sigma_x);
        double ey = -0.5/(sigma_y*sigma_y);
        double cscale = Math.PI*2/lambd;

        for( int y = ymin; y <= ymax; y++ )
            for( int x = xmin; x <= xmax; x++ )
            {
                double xr = x*c + y*s;
                double yr = -x*s + y*c;

                double v = scale*Math.exp(ex*xr*xr + ey*yr*yr)*Math.sin(cscale * xr + psi);

                kernel.put(ymax - y, xmax - x, v);
            }

        return kernel;
    }

    /*public static ArrayList<Mat> divideMat(Mat src, int sizeX, int sizeY)
    {
        ArrayList<Mat> dst = new ArrayList<Mat> ();
        int height = src.height();
        int width = src.width();

        int cols = (int)Math.ceil(width/sizeX);
        int rows = (int)Math.ceil(height/sizeY);
        for(int i = 1; i <= cols; i++)
        {
            for(int j = 1; j <= rows; j++)
            {
                int lux, luy;
                if(i == cols)
                    lux = width - sizeX;
                else
                    lux = (i - 1) * sizeX;
                if(j == rows)
                    luy = height - sizeY;
                else
                    luy = (j - 1) * sizeY;

                Rect theRect = new Rect(new Point(lux, luy), new Point(lux + sizeX - 1, luy + sizeY - 1));

                dst.add(new Mat(src, theRect));
            }
        }
        return dst;
    }*/

    /*public static bool[] generateCode(ArrayList<Mat> x, Mat realKernel, Mat imagKernel)
    {
        int width = x.get(0).width();
        int height = x.get(0).height();
        for (Mat tmp:x)
        {
            Mat real = new Mat(width, height, CvType.CV_64F);
            Mat imag = new Mat(width, height, CvType.CV_64F);
            Imgproc.filter2D(tmp, real, -1, realKernel);
            Imgproc.filter2D(tmp, imag, -1, imagKernel);
        }
    }*/

    public static int[] generateCode(Mat src, Mat imagKernel)
    {
        //Mat real = new Mat(src.width(), src.height(), CvType.CV_64F);
        Mat imag = new Mat(src.width(), src.height(), CvType.CV_64F);
        //.filter2D(src, real, -1, realKernel, new Point(realKernel.cols()-1, realKernel.rows()-1), 0.0);
        Imgproc.filter2D(src, imag, -1, imagKernel, new Point(0, 0), 0.0);

        int cols = (int)Math.ceil(src.width()/imagKernel.width());
        int rows = (int)Math.ceil(src.height()/imagKernel.height());

        int[] ans = new int[cols*rows];
        int k=0;
        int width = src.width();
        int height = src.height();
        for(int i = 1; i <= cols; i++)
        {
            for(int j = 1; j <= rows; j++)
            {
                int lux, luy;
                if(i == cols)
                    lux = src.width() - imagKernel.width();
                else
                    lux = (i - 1) * imagKernel.width();
                if(j == rows)
                    luy = src.height() - imagKernel.height();
                else
                    luy = (j - 1) * imagKernel.height();

                if(imag.get(luy, lux)[0] > 0)//Mat x & y
                    ans[k++] = 1;
                else ans[k++] = 0;
            }
        }
        return ans;

    }
}
