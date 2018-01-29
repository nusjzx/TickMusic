package org.bridge.music.network;

import com.orhanobut.logger.Logger;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 日志
 *
 * @author Bridge
 */
public class HttpLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        Logger.d("HttpLogInfo", message);
    }
}