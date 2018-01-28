package org.bridge.music.executor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import org.bridge.music.activity.AboutActivity;
import org.bridge.music.application.AppCache;
import org.bridge.music.service.PlayService;
import org.bridge.music.service.QuitTimer;
import org.bridge.music.activity.MusicActivity;
import org.bridge.music.activity.SettingActivity;
import org.bridge.music.utils.Preferences;
import org.bridge.music.utils.ToastUtils;

/**
 * 导航菜单执行器
 * Created by hzwangchenyan on 2016/1/14.
 */
public class NaviMenuExecutor {

    public static boolean onNavigationItemSelected(MenuItem item, MusicActivity activity) {
        switch (item.getItemId()) {
            case org.bridge.music.R.id.action_setting:
                startActivity(activity, SettingActivity.class);
                return true;
            case org.bridge.music.R.id.action_night:
                nightMode(activity);
                break;
            case org.bridge.music.R.id.action_timer:
                timerDialog(activity);
                return true;
            case org.bridge.music.R.id.action_exit:
                exit(activity);
                return true;
            case org.bridge.music.R.id.action_about:
                startActivity(activity, AboutActivity.class);
                return true;
        }
        return false;
    }

    private static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    private static void nightMode(final MusicActivity activity) {
        Preferences.saveNightMode(!Preferences.isNightMode());
        activity.recreate();
    }

    private static void timerDialog(final MusicActivity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(org.bridge.music.R.string.menu_timer)
                .setItems(activity.getResources().getStringArray(org.bridge.music.R.array.timer_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int[] times = activity.getResources().getIntArray(org.bridge.music.R.array.timer_int);
                        startTimer(activity, times[which]);
                    }
                })
                .show();
    }

    private static void startTimer(Context context, int minute) {
        QuitTimer.getInstance().start(minute * 60 * 1000);
        if (minute > 0) {
            ToastUtils.show(context.getString(org.bridge.music.R.string.timer_set, String.valueOf(minute)));
        } else {
            ToastUtils.show(org.bridge.music.R.string.timer_cancel);
        }
    }

    private static void exit(MusicActivity activity) {
        activity.finish();
        PlayService service = AppCache.get().getPlayService();
        if (service != null) {
            service.quit();
        }
    }
}
