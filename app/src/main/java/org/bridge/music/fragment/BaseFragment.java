package org.bridge.music.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.bridge.music.application.AppCache;
import org.bridge.music.service.PlayService;
import org.bridge.music.utils.PermissionReq;
import org.bridge.music.utils.binding.ViewBinder;

/**
 * 基类<br>
 * Created by wcy on 2015/11/26.
 */
public abstract class BaseFragment extends Fragment {
    protected Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewBinder.bind(this, getView());
    }

    @Override
    public void onStart() {
        super.onStart();
        setListener();
    }

    protected void setListener() {
    }

    protected PlayService getPlayService() {
        PlayService playService = AppCache.get().getPlayService();
        if (playService == null) {
            throw new NullPointerException("play service is null");
        }
        return playService;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
