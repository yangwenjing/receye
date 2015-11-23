package com.example.admin.testandroid;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

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
}
