package org.bridge.music.activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.bridge.music.R;
import org.bridge.music.fragment.SettingFragment;
import org.bridge.music.utils.binding.Bind;

/**
 * 设置Activity
 *
 * @author Bridge
 */
public class SettingActivity extends BaseActivity {


    @Bind(R.id.ll_load_fail)
    private LinearLayout llLoadFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.bridge.music.R.layout.activity_setting);

        if (!checkServiceAlive()) {
            return;
        }

        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setPlayService(getPlayService());
        getFragmentManager()
                .beginTransaction()
                .replace(org.bridge.music.R.id.ll_fragment_container, settingFragment)
                .commit();
    }


}
