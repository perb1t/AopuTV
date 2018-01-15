package com.hzrobot.aoputv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hzrobot.aoputv.R;
import com.hzrobot.aoputv.model.video.Video;

import java.util.List;

/**
 * Created by shijiwei on 2018/1/12.
 *
 * @VERSION 1.0
 */

public class VideoListAdapter extends BaseAdapter {

    private Context ctx;
    private List<Video> mVideoSet;

    public VideoListAdapter(Context ctx, List<Video> mVideoSet) {
        this.ctx = ctx;
        this.mVideoSet = mVideoSet;
    }

    @Override
    public int getCount() {
        return mVideoSet == null ? 0 : mVideoSet.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoSet == null ? null : mVideoSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_video_list, null);
            holder.tvVideoName = convertView.findViewById(R.id.tv_video_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvVideoName.setText(mVideoSet.get(position).getName());
        if (mVideoSet.get(position).isPlaying()) {
            holder.tvVideoName.setTextColor(Color.parseColor("#39A7F5"));
        }else {
            holder.tvVideoName.setTextColor(Color.WHITE);
        }
        return convertView;
    }

    class ViewHolder {

        TextView tvVideoName;
    }
}
