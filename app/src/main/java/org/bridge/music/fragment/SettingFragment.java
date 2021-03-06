package org.bridge.music.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

import org.bridge.music.service.EventCallback;
import org.bridge.music.service.OnPlayerEventListener;
import org.bridge.music.service.PlayService;
import org.bridge.music.utils.MusicUtils;
import org.bridge.music.utils.PrefUtils;
import org.bridge.music.utils.ToastUtils;

public  class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Preference mSoundEffect;
        private Preference mFilterSize;
        private Preference mFilterTime;

        private PlayService mPlayService;
        private ProgressDialog mProgressDialog;

        public void setPlayService(PlayService playService) {
            this.mPlayService = playService;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(org.bridge.music.R.xml.preference_setting);

            mSoundEffect = findPreference(getString(org.bridge.music.R.string.setting_key_sound_effect));
            mFilterSize = findPreference(getString(org.bridge.music.R.string.setting_key_filter_size));
            mFilterTime = findPreference(getString(org.bridge.music.R.string.setting_key_filter_time));
            mSoundEffect.setOnPreferenceClickListener(this);
            mFilterSize.setOnPreferenceChangeListener(this);
            mFilterTime.setOnPreferenceChangeListener(this);

            mFilterSize.setSummary(getSummary(PrefUtils.getFilterSize(), org.bridge.music.R.array.filter_size_entries, org.bridge.music.R.array.filter_size_entry_values));
            mFilterTime.setSummary(getSummary(PrefUtils.getFilterTime(), org.bridge.music.R.array.filter_time_entries, org.bridge.music.R.array.filter_time_entry_values));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mSoundEffect) {
                startEqualizer();
                return true;
            }
            return false;
        }

        private void startEqualizer() {
            if (MusicUtils.isAudioControlPanelAvailable(getActivity())) {
                Intent intent = new Intent();
                String packageName = getActivity().getPackageName();
                intent.setAction(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName);
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mPlayService.getAudioSessionId());

                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtils.show(org.bridge.music.R.string.device_not_support);
                }
            } else {
                ToastUtils.show(org.bridge.music.R.string.device_not_support);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mFilterSize) {
                PrefUtils.saveFilterSize((String) newValue);
                mFilterSize.setSummary(getSummary(PrefUtils.getFilterSize(), org.bridge.music.R.array.filter_size_entries, org.bridge.music.R.array.filter_size_entry_values));
                onFilterChanged();
                return true;
            } else if (preference == mFilterTime) {
                PrefUtils.saveFilterTime((String) newValue);
                mFilterTime.setSummary(getSummary(PrefUtils.getFilterTime(), org.bridge.music.R.array.filter_time_entries, org.bridge.music.R.array.filter_time_entry_values));
                onFilterChanged();
                return true;
            }
            return false;
        }

        private String getSummary(String value, int entries, int entryValues) {
            String[] entryArray = getResources().getStringArray(entries);
            String[] entryValueArray = getResources().getStringArray(entryValues);
            for (int i = 0; i < entryValueArray.length; i++) {
                String v = entryValueArray[i];
                if (TextUtils.equals(v, value)) {
                    return entryArray[i];
                }
            }
            return entryArray[0];
        }

        private void onFilterChanged() {
            showProgress();
            mPlayService.stop();
            mPlayService.updateMusicList(new EventCallback<Void>() {
                @Override
                public void onEvent(Void aVoid) {
                    cancelProgress();
                    OnPlayerEventListener listener = mPlayService.getOnPlayEventListener();
                    if (listener != null) {
                        listener.onChange(mPlayService.getPlayingMusic());
                    }
                }
            });
        }

        private void showProgress() {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("正在扫描音乐");
            }
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }

        private void cancelProgress() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.cancel();
            }
        }
    }