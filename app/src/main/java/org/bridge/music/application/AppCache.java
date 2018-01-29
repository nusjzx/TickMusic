package org.bridge.music.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import com.amap.api.location.AMapLocalWeatherLive;

import java.util.ArrayList;
import java.util.List;

import org.bridge.music.executor.DownloadMusicInfo;
import org.bridge.music.model.Music;
import org.bridge.music.model.SongListInfo;
import org.bridge.music.service.PlayService;
import org.bridge.music.utils.CoverLoader;
import org.bridge.music.utils.PrefUtils;
import org.bridge.music.utils.ScreenUtils;
import org.bridge.music.utils.ToastUtils;

/**
 * Created by hzwangchenyan on 2016/11/23.
 */
public class AppCache {
    private Context mContext;
    private PlayService mPlayService;
    // 本地歌曲列表
    private final List<Music> mMusicList = new ArrayList<>();
    // 歌单列表
    private final List<SongListInfo> mSongListInfos = new ArrayList<>();
    private final List<Activity> mActivityStack = new ArrayList<>();
    private final LongSparseArray<DownloadMusicInfo> mDownloadList = new LongSparseArray<>();
    private AMapLocalWeatherLive mAMapLocalWeatherLive;

    private AppCache() {
    }

    private static class SingletonHolder {
        private static AppCache instance = new AppCache();
    }

    public static AppCache get() {
        return SingletonHolder.instance;
    }

    public void init(Application application) {
        mContext = application.getApplicationContext();
        ToastUtils.init(mContext);
        PrefUtils.init(mContext);
        ScreenUtils.init(mContext);
        CrashHandler.getInstance().init();
        CoverLoader.getInstance().init(mContext);
        application.registerActivityLifecycleCallbacks(new ActivityLifecycle());
    }

    public Context getContext() {
        return mContext;
    }

    public PlayService getPlayService() {
        return mPlayService;
    }

    public void setPlayService(PlayService service) {
        mPlayService = service;
    }

    public List<Music> getMusicList() {
        return mMusicList;
    }

    public List<SongListInfo> getSongListInfos() {
        return mSongListInfos;
    }

    public void clearStack() {
        List<Activity> activityStack = mActivityStack;
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activityStack.clear();
    }

    public LongSparseArray<DownloadMusicInfo> getDownloadList() {
        return mDownloadList;
    }

    public AMapLocalWeatherLive getAMapLocalWeatherLive() {
        return mAMapLocalWeatherLive;
    }

    public void setAMapLocalWeatherLive(AMapLocalWeatherLive aMapLocalWeatherLive) {
        mAMapLocalWeatherLive = aMapLocalWeatherLive;
    }

    private class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
        private static final String TAG = "Activity";

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i(TAG, "onCreate: " + activity.getClass().getSimpleName());
            mActivityStack.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(TAG, "onDestroy: " + activity.getClass().getSimpleName());
            mActivityStack.remove(activity);
        }
    }
}
