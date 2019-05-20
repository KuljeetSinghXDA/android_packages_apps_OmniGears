/*
 *  Copyright (C) 2017-2018 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
*/

package org.omnirom.omnigears.moresettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.SearchIndexableResource;
import android.util.Log;

import com.android.internal.util.omni.PackageUtils;

import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import org.omnirom.omnilib.utils.OmniUtils;

import java.util.List;
import java.util.ArrayList;

public class MoreSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener, Indexable {
    private static final String TAG = "MoreSettings";
    private static final String KEY_SHOW_DASHBOARD_COLUMNS = "show_dashboard_columns";
    private static final String KEY_HIDE_DASHBOARD_SUMMARY = "hide_dashboard_summary";
    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";

    private SharedPreferences mAppPreferences;
    private ListPreference mFlashlightOnCall;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.OMNI_SETTINGS;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.more_settings);

        PreferenceScreen prefScreen = getPreferenceScreen();

        mFlashlightOnCall = (ListPreference) findPreference(FLASHLIGHT_ON_CALL);
        Preference FlashOnCall = findPreference("flashlight_on_call");
        if (!OmniUtils.deviceSupportsFlashLight(getActivity())) {
            prefScreen.removePreference(FlashOnCall);
        } else {
        int flashlightValue = Settings.System.getInt(getContentResolver(),
                Settings.System.FLASHLIGHT_ON_CALL, 0);
        mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
        mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
        mFlashlightOnCall.setOnPreferenceChangeListener(this);
        }

        mAppPreferences = getActivity().getSharedPreferences(SettingsActivity.APP_PREFERENCES_NAME,
                Context.MODE_PRIVATE);

        SwitchPreference showColumnsLayout = (SwitchPreference) findPreference(KEY_SHOW_DASHBOARD_COLUMNS);
        showColumnsLayout.setChecked(mAppPreferences.getInt(SettingsActivity.KEY_COLUMNS_COUNT, 1) == 2);
        showColumnsLayout.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue ) {
                    mAppPreferences.edit().putInt(SettingsActivity.KEY_COLUMNS_COUNT, 2).commit();
                } else {
                    mAppPreferences.edit().putInt(SettingsActivity.KEY_COLUMNS_COUNT, 1).commit();
                }
                return true;
            }
        });

        SwitchPreference hideColumnSummary = (SwitchPreference) findPreference(KEY_HIDE_DASHBOARD_SUMMARY);
        hideColumnSummary.setChecked(mAppPreferences.getBoolean(SettingsActivity.KEY_HIDE_SUMMARY, false));
        hideColumnSummary.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mAppPreferences.edit().putBoolean(SettingsActivity.KEY_HIDE_SUMMARY, ((Boolean) newValue)).commit();
                return true;
            }
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFlashlightOnCall) {
            int flashlightValue = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(getContentResolver(),
                    Settings.System.FLASHLIGHT_ON_CALL, flashlightValue);
            mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
            mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
            return true;
        }
        return false;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.more_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
    };
}
