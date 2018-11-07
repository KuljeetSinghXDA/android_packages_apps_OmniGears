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
package org.omnirom.omnigears.interfacesettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import org.omnirom.omnilib.preference.SystemSettingSwitchPreference;
import org.omnirom.omnilib.preference.SeekBarPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StyleSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "StyleSettings";
    private static final String OMNI_SYSUI_ROUNDED_SIZE = "sysui_rounded_size";
    private static final String OMNI_SYSUI_ROUNDED_CONTENT_PADDING = "sysui_rounded_content_padding";

    private SeekBarPreference mCornerRadius;
    private SeekBarPreference mContentPadding;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.OMNI_SETTINGS;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.style_settings);

        Resources res = null;
        Context mContext = getContext();

        try {
            res = mContext.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        float displayDensity = getResources().getDisplayMetrics().density;

        // Rounded Corner Radius
        int resourceIdRadius = res.getIdentifier("com.android.systemui:dimen/rounded_corner_radius", null, null);
        mCornerRadius = (SeekBarPreference) findPreference(OMNI_SYSUI_ROUNDED_SIZE);
        int cornerRadius = Settings.System.getInt(mContext.getContentResolver(), Settings.System.OMNI_SYSUI_ROUNDED_SIZE,
                (int) (res.getDimension(resourceIdRadius) / displayDensity));
        mCornerRadius.setValue(cornerRadius / 1);
        mCornerRadius.setOnPreferenceChangeListener(this);

        // Rounded Content Padding
        int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null, null);
        mContentPadding = (SeekBarPreference) findPreference(OMNI_SYSUI_ROUNDED_CONTENT_PADDING);
        int contentPadding = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.OMNI_SYSUI_ROUNDED_CONTENT_PADDING,
                (int) (res.getDimension(resourceIdPadding) / displayDensity));
        mContentPadding.setValue(contentPadding / 1);
        mContentPadding.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mCornerRadius) {
            int value = ((Integer) newValue) * 1;
            Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.OMNI_SYSUI_ROUNDED_SIZE, value, UserHandle.USER_CURRENT);
        } else if (preference == mContentPadding) {
            int value = ((Integer) newValue) * 1;
            Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.OMNI_SYSUI_ROUNDED_CONTENT_PADDING, value, UserHandle.USER_CURRENT);
        }
        return true;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.style_settings;
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
