package org.bridge.music.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.bridge.music.application.AppCache;
import org.bridge.music.model.Music;
import org.bridge.music.service.PlayService;
import org.bridge.music.utils.CoverLoader;
import org.bridge.music.utils.FileUtils;
import org.bridge.music.utils.binding.Bind;
import org.bridge.music.utils.binding.ViewBinder;

/**
 * 本地音乐列表适配器
 * Created by wcy on 2015/11/27.
 */
public class LocalMusicAdapter extends BaseAdapter {
    private OnMoreClickListener mListener;
    private int mPlayingPosition;

    @Override
    public int getCount() {
        return AppCache.get().getMusicList().size();
    }

    @Override
    public Object getItem(int position) {
        return AppCache.get().getMusicList().get(position);
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
        if (position == mPlayingPosition) {
            holder.vPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.vPlaying.setVisibility(View.INVISIBLE);
        }
        Music music = AppCache.get().getMusicList().get(position);
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        holder.ivCover.setImageBitmap(cover);
        holder.tvTitle.setText(music.getTitle());
        String artist = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        holder.tvArtist.setText(artist);
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMoreClick(position);
                }
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != AppCache.get().getMusicList().size() - 1;
    }

    public void updatePlayingPosition(PlayService playService) {
        if (playService.getPlayingMusic() != null && playService.getPlayingMusic().getType() == Music.Type.LOCAL) {
            mPlayingPosition = playService.getPlayingPosition();
        } else {
            mPlayingPosition = -1;
        }
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        mListener = listener;
    }

    private static class ViewHolder {
        @Bind(org.bridge.music.R.id.v_playing)
        private View vPlaying;
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
