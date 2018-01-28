package org.bridge.music.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import org.bridge.music.R;
import org.bridge.music.utils.PackageUtils;
import org.bridge.music.utils.binding.Bind;

public class AboutActivity extends BaseActivity {

    @Bind(R.id.version)
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        version.setText(String.format(getString(R.string.version_about),
                PackageUtils.getVersionName(getApplicationContext())));

    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app, getString(R.string.app_name)));
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


}
