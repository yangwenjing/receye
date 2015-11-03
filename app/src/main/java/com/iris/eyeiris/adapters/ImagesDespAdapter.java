package com.iris.eyeiris.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iris.eyeiris.R;
import com.iris.eyeiris.entities.ImageDesp;

import java.util.List;

/**
 * Created by ywj on 15/11/2.
 */
public class ImagesDespAdapter extends BaseAdapter {
    private Context context;
    private int mResource;
    private LayoutInflater mInflater;
    private List list;

    public ImagesDespAdapter(Context context, int resource, List<ImageDesp> list)
     {
         this.context = context;
         mInflater = LayoutInflater.from(context);
         this.mResource = resource;
         this.list = list;
     }

    @Override
    public int getCount() {
        return this.list!=null? this.list.size(): 0;
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
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

        ImageDesp imgDesp = (ImageDesp)getItem(position); //获取图片描述对象

        ImageView imgV = (ImageView)convertView.findViewById(R.id.image);
        imgV.setImageBitmap(imgDesp.getImage());

        TextView tv = (TextView)convertView.findViewById(R.id.desc);
        tv.setText(imgDesp.getDescript());

        return convertView;
    }
}
