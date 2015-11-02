package com.iris.eyeiris.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iris.eyeiris.R;
import com.iris.eyeiris.entities.ImageDesp;

/**
 * Created by ywj on 15/11/2.
 */
public class ImagesDespAdapter extends ArrayAdapter<ImageDesp> {
    private int mResource;
    private LayoutInflater mInflater;

    public ImagesDespAdapter(Context context, int resource) {
        super(context, resource);
        this.mResource = resource;
        mInflater = LayoutInflater.from(context);

    }

    /**
     * 从模版创建图片
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mInflater.inflate(mResource, null);
        }

        ImageDesp imgDesp = getItem(position); //获取图片描述对象

        ImageView imgV = (ImageView)convertView.findViewById(R.id.image);
        imgV.setImageBitmap(imgDesp.getImage());

        TextView tv = (TextView)convertView.findViewById(R.id.description);
        tv.setText(imgDesp.getDescript());

        return convertView;
    }
}
