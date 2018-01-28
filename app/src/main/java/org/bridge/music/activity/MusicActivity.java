package org.bridge.music.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bridge.music.adapter.FragmentAdapter;
import org.bridge.music.application.AppCache;
import org.bridge.music.constants.Keys;
import org.bridge.music.executor.NaviMenuExecutor;
import org.bridge.music.executor.WeatherExecutor;
import org.bridge.music.fragment.LocalMusicFragment;
import org.bridge.music.fragment.PlayFragment;
import org.bridge.music.model.Music;
import org.bridge.music.service.OnPlayerEventListener;
import org.bridge.music.service.PlayService;
import org.bridge.music.utils.CoverLoader;
import org.bridge.music.utils.PermissionReq;
import org.bridge.music.utils.SystemUtils;
import org.bridge.music.utils.binding.Bind;
import org.bridge.music.constants.Extras;
import org.bridge.music.fragment.PlaylistFragment;
import org.bridge.music.utils.ToastUtils;

public class MusicActivity extends BaseActivity implements View.OnClickListener, OnPlayerEventListener,
        NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    @Bind(org.bridge.music.R.id.drawer_layout)
    private DrawerLayout drawerLayout;
    @Bind(org.bridge.music.R.id.navigation_view)
    private NavigationView navigationView;
    @Bind(org.bridge.music.R.id.iv_menu)
    private ImageView ivMenu;
    @Bind(org.bridge.music.R.id.iv_search)
    private ImageView ivSearch;
    @Bind(org.bridge.music.R.id.tv_local_music)
    private TextView tvLocalMusic;
    @Bind(org.bridge.music.R.id.tv_online_music)
    private TextView tvOnlineMusic;
    @Bind(org.bridge.music.R.id.viewpager)
    private ViewPager mViewPager;
    @Bind(org.bridge.music.R.id.fl_play_bar)
    private FrameLayout flPlayBar;
    @Bind(org.bridge.music.R.id.iv_play_bar_cover)
    private ImageView ivPlayBarCover;
    @Bind(org.bridge.music.R.id.tv_play_bar_title)
    private TextView tvPlayBarTitle;
    @Bind(org.bridge.music.R.id.tv_play_bar_artist)
    private TextView tvPlayBarArtist;
    @Bind(org.bridge.music.R.id.iv_play_bar_play)
    private ImageView ivPlayBarPlay;
    @Bind(org.bridge.music.R.id.iv_play_bar_next)
    private ImageView ivPlayBarNext;
    @Bind(org.bridge.music.R.id.pb_play_bar)
    private ProgressBar mProgressBar;
    private View vNavigationHeader;
    private LocalMusicFragment mLocalMusicFragment;
    private PlaylistFragment mPlaylistFragment;
    private PlayFragment mPlayFragment;
    private boolean isPlayFragmentShow = false;
    private MenuItem timerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.bridge.music.R.layout.activity_music);

        if (!checkServiceAlive()) {
            return;
        }

        getPlayService().setOnPlayEventListener(this);

        setupView();
        updateWeather();
        onChangeImpl(getPlayService().getPlayingMusic());
        parseIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    @Override
    protected void setListener() {
        ivMenu.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvLocalMusic.setOnClickListener(this);
        tvOnlineMusic.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
        flPlayBar.setOnClickListener(this);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupView() {
        // add navigation header
        vNavigationHeader = LayoutInflater.from(this).inflate(org.bridge.music.R.layout.navigation_header, navigationView, false);
        navigationView.addHeaderView(vNavigationHeader);

        // setup view pager
        mLocalMusicFragment = new LocalMusicFragment();
        mPlaylistFragment = new PlaylistFragment();
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mLocalMusicFragment);
        adapter.addFragment(mPlaylistFragment);
        mViewPager.setAdapter(adapter);
        tvLocalMusic.setSelected(true);
    }

    private void updateWeather() {
        PermissionReq.with(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        new WeatherExecutor(getPlayService(), vNavigationHeader).execute();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(org.bridge.music.R.string.no_permission_location);
                    }
                })
                .request();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }

    @Override
    public void onChange(Music music) {
        onChangeImpl(music);
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onChange(music);
        }
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onPlayerStart();
        }
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onPlayerPause();
        }
    }

    /**
     * 更新播放进度
     */
    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onPublish(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onBufferingUpdate(percent);
        }
    }

    @Override
    public void onTimer(long remain) {
        if (timerItem == null) {
            timerItem = navigationView.getMenu().findItem(org.bridge.music.R.id.action_timer);
        }
        String title = getString(org.bridge.music.R.string.menu_timer);
        timerItem.setTitle(remain == 0 ? title : SystemUtils.formatTime(title + "(mm:ss)", remain));
    }

    @Override
    public void onMusicListUpdate() {
        if (mLocalMusicFragment != null && mLocalMusicFragment.isAdded()) {
            mLocalMusicFragment.onMusicListUpdate();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case org.bridge.music.R.id.iv_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case org.bridge.music.R.id.iv_search:
                startActivity(new Intent(this, SearchMusicActivity.class));
                break;
            case org.bridge.music.R.id.tv_local_music:
                mViewPager.setCurrentItem(0);
                break;
            case org.bridge.music.R.id.tv_online_music:
                mViewPager.setCurrentItem(1);
                break;
            case org.bridge.music.R.id.fl_play_bar:
                showPlayingFragment();
                break;
            case org.bridge.music.R.id.iv_play_bar_play:
                play();
                break;
            case org.bridge.music.R.id.iv_play_bar_next:
                next();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setChecked(false);
            }
        }, 500);
        return NaviMenuExecutor.onNavigationItemSelected(item, this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
            tvOnlineMusic.setSelected(false);
        } else {
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void onChangeImpl(Music music) {
        if (music == null) {
            return;
        }

        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(getPlayService().isPlaying() || getPlayService().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress((int) getPlayService().getCurrentPosition());

        if (mLocalMusicFragment != null && mLocalMusicFragment.isAdded()) {
            mLocalMusicFragment.onItemPlay();
        }
    }

    private void play() {
        getPlayService().playPause();
    }

    private void next() {
        getPlayService().next();
    }

    private void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(org.bridge.music.R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, org.bridge.music.R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Keys.VIEW_PAGER_INDEX, mViewPager.getCurrentItem());
        mLocalMusicFragment.onSaveInstanceState(outState);
        mPlaylistFragment.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(savedInstanceState.getInt(Keys.VIEW_PAGER_INDEX), false);
                mLocalMusicFragment.onRestoreInstanceState(savedInstanceState);
                mPlaylistFragment.onRestoreInstanceState(savedInstanceState);
            }
        });
    }

    @Override
    protected void onDestroy() {
        PlayService service = AppCache.get().getPlayService();
        if (service != null) {
            service.setOnPlayEventListener(null);
        }
        super.onDestroy();
    }
}
