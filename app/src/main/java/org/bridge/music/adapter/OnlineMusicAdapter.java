package org.bridge.music.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.bridge.music.model.OnlineMusic;
import org.bridge.music.utils.FileUtils;
import org.bridge.music.utils.binding.Bind;
import org.bridge.music.utils.binding.ViewBinder;

import java.util.List;

/**
 * 在线音乐列表适配器
 * Created by wcy on 2015/12/22.
 */
public class OnlineMusicAdapter extends BaseAdapter {
    private List<OnlineMusic> mData;
    private OnMoreClickListener mListener;

    public OnlineMusicAdapter(List<OnlineMusic> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(org.bridge.music.R.layout.view_holder_music, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OnlineMusic onlineMusic = mData.get(position);
        Glide.with(parent.getContext())
                .load(onlineMusic.getPic_small())
                .placeholder(org.bridge.music.R.drawable.default_cover)
                .error(org.bridge.music.R.drawable.default_cover)
                .into(holder.ivCover);
        holder.tvTitle.setText(onlineMusic.getTitle());
        String artist = FileUtils.getArtistAndAlbum(onlineMusic.getArtist_name(), onlineMusic.getAlbum_title());
        holder.tvArtist.setText(artist);
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMoreClick(position);
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != mData.size() - 1;
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        mListener = listener;
    }

    private static class ViewHolder {
        @Bind(org.bridge.music.R.id.iv_cover)
        private ImageView ivCover;
        @Bind(org.bridge.music.R.id.tv_title)
        private TextView tvTitle;
        @Bind(org.bridge.music.R.id.tv_artist)
        private TextView tvArtist;
        @Bind(org.bridge.music.R.id.iv_more)
        private ImageView ivMore;
        @Bind(org.bridge.music.R.id.v_divider)
        private View vDivider;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
