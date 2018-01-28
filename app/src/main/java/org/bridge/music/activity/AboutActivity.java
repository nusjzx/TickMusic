package org.bridge.music.activity;

import android.os.Bundle;

import org.bridge.music.R;
import org.bridge.music.fragment.AboutFragment;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (!checkServiceAlive()) {
            return;
        }

        getFragmentManager().beginTransaction().replace(R.id.ll_fragment_container, new AboutFragment()).commit();
    }


}
