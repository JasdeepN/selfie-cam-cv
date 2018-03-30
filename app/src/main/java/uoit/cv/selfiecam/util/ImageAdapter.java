package uoit.cv.selfiecam.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.opencv.features2d.BOWImgDescriptorExtractor;

import java.util.List;
import java.util.Map;

import uoit.cv.selfiecam.R;
import uoit.cv.selfiecam.data.DataContent;

/**
 * Created by jasdeep on 2018-03-29.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Bitmap> mThumbIds;


    // Constructor
    public ImageAdapter(Context c, List<Bitmap> data){
        mContext = c;
        mThumbIds = data;
    }

    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(mThumbIds.get(position));
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(320, 160));
        return imageView;
    }   }