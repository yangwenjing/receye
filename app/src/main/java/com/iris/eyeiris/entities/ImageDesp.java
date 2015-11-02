package com.iris.eyeiris.entities;

import android.graphics.Bitmap;

/**
 * Created by ywj on 15/11/2.
 */
public class ImageDesp {
    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    private Bitmap image;
    private String descript;


}
