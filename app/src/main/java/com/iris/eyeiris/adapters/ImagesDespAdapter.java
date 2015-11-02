package com.iris.eyeiris.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.iris.eyeiris.entities.ImageDesp;

/**
 * Created by ywj on 15/11/2.
 */
public class ImagesDespAdapter extends ArrayAdapter<ImageDesp> {
    private int mResource;

    public ImagesDespAdapter(Context context, int resource) {
        super(context, resource);
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


    }
}
