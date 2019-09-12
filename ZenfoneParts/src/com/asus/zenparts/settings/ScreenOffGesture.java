/*
 * Copyright (C) 2014 Slimroms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.asus.zenparts.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.asus.zenparts.utils.AppHelper;
import com.asus.zenparts.utils.ActionConstants;
import com.asus.zenparts.utils.DeviceUtils;
import com.asus.zenparts.utils.DeviceUtils.FilteredDeviceFeaturesArray;

import com.asus.zenparts.KernelControl;
import com.asus.zenparts.R;
import com.asus.zenparts.utils.ShortcutPickerHelper;

public class ScreenOffGesture extends PreferenceFragment implements
        OnPreferenceChangeListener, OnPreferenceClickListener,
        ShortcutPickerHelper.OnPickListener {

    private static final String SETTINGS_METADATA_NAME = "com.android.settings";

    public static final String GESTURE_SETTINGS = "screen_off_gesture_settings";

    public static final String PREF_GESTURE_ENABLE = "enable_gestures";
    
    public static final String[] PREF_GESTURES = new String[] {
        "gesture_up",
        "gesture_c",
        "gesture_v",
        "gesture_s",
        "gesture_z",
        "gesture_w",
        "gesture_e"
    };

    private static final int DLG_SHOW_ACTION_DIALOG  = 0;
    private static final int DLG_RESET_TO_DEFAULT    = 1;

    private static final int MENU_RESET = Menu.FIRST;

    private Preference mGestureC;
    private Preference mGestureDoubleSwipe;
    private Preference mGestureArrowUp;
    private Preference mGestureArrowDown;
    private Preference mGestureArrowLeft;
    private Preference mGestureArrowRight;
    private Preference mGestureSwipeUp;
    private SwitchPreference mEnableGestures;

    private boolean mCheckPreferences;
    private static SharedPreferences mScreenOffGestureSharedPreferences;

    private ShortcutPickerHelper mPicker;
    private String mPendingSettingsKey;
    private static FilteredDeviceFeaturesArray sFinalActionDialogArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPicker = new ShortcutPickerHelper(getActivity(), this);

        mScreenOffGestureSharedPreferences = getActivity().getSharedPreferences(
                GESTURE_SETTINGS, Activity.MODE_PRIVATE);

        // Before we start filter out unsupported options on the
        // ListPreference values and entries
        PackageManager pm = getActivity().getPackageManager();
        Resources settingsResources = null;
        try {
            settingsResources = pm.getResourcesForApplication(SETTINGS_METADATA_NAME);
        } catch (Exception e) {
            return;
        }
        sFinalActionDialogArray = new FilteredDeviceFeaturesArray();
        sFinalActionDialogArray = DeviceUtils.filterUnsupportedDeviceFeatures(getActivity(),
                settingsResources.getStringArray(
                        settingsResources.getIdentifier(SETTINGS_METADATA_NAME
                        + ":array/shortcut_action_screen_off_values", null, null)),
                settingsResources.getStringArray(
                        settingsResources.getIdentifier(SETTINGS_METADATA_NAME
                        + ":array/shortcut_action_screen_off_entries", null, null)));

        // Attach final settings screen.
        reloadSettings();

        setHasOptionsMenu(true);
    }

    private PreferenceScreen reloadSettings() {
        mCheckPreferences = false;
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.screen_off_gesture);
        prefs = getPreferenceScreen();

        mEnableGestures = (SwitchPreference) prefs.findPreference(PREF_GESTURE_ENABLE);

        mGestureC = (Preference) prefs.findPreference(PREF_GESTURES[1]);
        mGestureDoubleSwipe = (Preference) prefs.findPreference(PREF_GESTURES[6]);
        mGestureArrowUp = (Preference) prefs.findPreference(PREF_GESTURES[5]);
        mGestureArrowDown = (Preference) prefs.findPreference(PREF_GESTURES[2]);
        mGestureArrowLeft = (Preference) prefs.findPreference(PREF_GESTURES[3]);
        mGestureArrowRight = (Preference) prefs.findPreference(PREF_GESTURES[4]);
        mGestureSwipeUp = (Preference) prefs.findPreference(PREF_GESTURES[0]);

        setupOrUpdatePreference(mGestureC, mScreenOffGestureSharedPreferences
                .getString(PREF_GESTURES[1], ActionConstants.ACTION_NULL));
        setupOrUpdatePreference(mGestureDoubleSwipe, mScreenOffGestureSharedPreferences
                .getString(PREF_GESTURES[6], ActionConstants.ACTION_NULL));
        setupOrUpdatePreference(mGestureArrowUp, mScreenOffGestureSharedPreferences
                    .getString(PREF_GESTURES[5], ActionConstants.ACTION_NULL));
        setupOrUpdatePreference(mGestureArrowDown, mScreenOffGestureSharedPreferences
                .getString(PREF_GESTURES[2], ActionConstants.ACTION_NULL));
        setupOrUpdatePreference(mGestureArrowLeft, mScreenOffGestureSharedPreferences
                .getString(PREF_GESTURES[3], ActionConstants.ACTION_NULL));
        setupOrUpdatePreference(mGestureArrowRight, mScreenOffGestureSharedPreferences
                .getString(PREF_GESTURES[4], ActionConstants.ACTION_NULL));
        setupOrUpdatePreference(mGestureSwipeUp, mScreenOffGestureSharedPreferences
                    .getString(PREF_GESTURES[0], ActionConstants.ACTION_NULL));

        boolean enableGestures =
                mScreenOffGestureSharedPreferences.getBoolean(PREF_GESTURE_ENABLE, true);
        mEnableGestures.setChecked(enableGestures);
        mEnableGestures.setOnPreferenceChangeListener(this);

        checkGestureState(enableGestures);
        
        mCheckPreferences = true;
        return prefs;
    }

    public static void checkGestureState(boolean enabled) {
        if (enabled) {
            for (int i=0; i < PREF_GESTURES.length; i++) {
                String curr_gesture = mScreenOffGestureSharedPreferences.getString(PREF_GESTURES[i], "");
                if (curr_gesture.equals(ActionConstants.ACTION_NULL) || curr_gesture.isEmpty()) {
                    KernelControl.enableGesture(i, false);
                } else {
                    KernelControl.enableGesture(i, true);
                }
            }
        } else {
            for (int i=0; i < PREF_GESTURES.length; i++) {
                KernelControl.enableGesture(i, false);
            }
        }
    }

    private void setupOrUpdatePreference(Preference preference, String action) {
        if (preference == null || action == null) {
            return;
        }

        if (action.startsWith("**")) {
            preference.setSummary(getDescription(action));
        } else {
            preference.setSummary(AppHelper.getFriendlyNameForUri(
                    getActivity(), getActivity().getPackageManager(), action));
        }
        preference.setOnPreferenceClickListener(this);
    }

    private String getDescription(String action) {
        if (sFinalActionDialogArray == null || action == null) {
            return null;
        }
        int i = 0;
        for (String actionValue : sFinalActionDialogArray.values) {
            if (action.equals(actionValue)) {
                return sFinalActionDialogArray.entries[i];
            }
            i++;
        }
        return null;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String settingsKey = null;
        int dialogTitle = 0;
	if (preference == mGestureC) {
            settingsKey = PREF_GESTURES[1];
            dialogTitle = R.string.gesture_c_title;
        } else if (preference == mGestureDoubleSwipe) {
            settingsKey = PREF_GESTURES[6];
            dialogTitle = R.string.gesture_e_title;
        } else if (preference == mGestureArrowUp) {
            settingsKey = PREF_GESTURES[5];
            dialogTitle = R.string.gesture_w_title;
        } else if (preference == mGestureArrowDown) {
            settingsKey = PREF_GESTURES[2];
            dialogTitle = R.string.gesture_v_title;
        } else if (preference == mGestureArrowLeft) {
            settingsKey = PREF_GESTURES[3];
            dialogTitle = R.string.gesture_s_title;
        } else if (preference == mGestureArrowRight) {
            settingsKey = PREF_GESTURES[4];
            dialogTitle = R.string.gesture_z_title;
        } else if (preference == mGestureSwipeUp) {
            settingsKey = PREF_GESTURES[0];
            dialogTitle = R.string.gesture_up_title;
        }
        if (settingsKey != null) {
            showDialogInner(DLG_SHOW_ACTION_DIALOG, settingsKey, dialogTitle);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!mCheckPreferences) {
            return false;
        }
        if (preference == mEnableGestures) {
            mScreenOffGestureSharedPreferences.edit()
                    .putBoolean(PREF_GESTURE_ENABLE, (Boolean) newValue).commit();
            return true;
        }
        return false;
    }

    // Reset all entries to default.
    private void resetToDefault() {
        SharedPreferences.Editor editor = mScreenOffGestureSharedPreferences.edit();
        mScreenOffGestureSharedPreferences.edit()
                .putBoolean(PREF_GESTURE_ENABLE, true).commit();
        editor.putString(PREF_GESTURES[1],
                ActionConstants.ACTION_CAMERA).commit();
        editor.putString(PREF_GESTURES[6],
                ActionConstants.ACTION_MEDIA_PLAY_PAUSE).commit();
        editor.putString(PREF_GESTURES[5],
                ActionConstants.ACTION_TORCH).commit();
        editor.putString(PREF_GESTURES[2],
                ActionConstants.ACTION_VIB_SILENT).commit();
        editor.putString(PREF_GESTURES[3],
                ActionConstants.ACTION_MEDIA_PREVIOUS).commit();
        editor.putString(PREF_GESTURES[4],
                ActionConstants.ACTION_MEDIA_NEXT).commit();
		editor.putString(PREF_GESTURES[0],
                ActionConstants.ACTION_WAKE_DEVICE).commit();
        editor.commit();
        reloadSettings();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void shortcutPicked(String action,
                String description, Bitmap bmp, boolean isApplication) {
        if (mPendingSettingsKey == null || action == null) {
            return;
        }
        mScreenOffGestureSharedPreferences.edit().putString(mPendingSettingsKey, action).commit();
        reloadSettings();
        mPendingSettingsKey = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ShortcutPickerHelper.REQUEST_PICK_SHORTCUT
                    || requestCode == ShortcutPickerHelper.REQUEST_PICK_APPLICATION
                    || requestCode == ShortcutPickerHelper.REQUEST_CREATE_SHORTCUT) {
                mPicker.onActivityResult(requestCode, resultCode, data);

            }
        } else {
            mPendingSettingsKey = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                    showDialogInner(DLG_RESET_TO_DEFAULT, null, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    private void showDialogInner(int id, String settingsKey, int dialogTitle) {
        DialogFragment newFragment =
                MyAlertDialogFragment.newInstance(id, settingsKey, dialogTitle);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(
                int id, String settingsKey, int dialogTitle) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            args.putString("settingsKey", settingsKey);
            args.putInt("dialogTitle", dialogTitle);
            frag.setArguments(args);
            return frag;
        }

        ScreenOffGesture getOwner() {
            return (ScreenOffGesture) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            final String settingsKey = getArguments().getString("settingsKey");
            int dialogTitle = getArguments().getInt("dialogTitle");
            switch (id) {
                case DLG_SHOW_ACTION_DIALOG:
                    if (sFinalActionDialogArray == null) {
                        return null;
                    }
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(dialogTitle)
                    .setNegativeButton(R.string.cancel, null)
                    .setItems(getOwner().sFinalActionDialogArray.entries,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if (getOwner().sFinalActionDialogArray.values[item]
                                    .equals(ActionConstants.ACTION_APP)) {
                                if (getOwner().mPicker != null) {
                                    getOwner().mPendingSettingsKey = settingsKey;
                                    getOwner().mPicker.pickShortcut(getOwner().getId());
                                }
                            } else {
                                getOwner().mScreenOffGestureSharedPreferences.edit()
                                        .putString(settingsKey,
                                        getOwner().sFinalActionDialogArray.values[item]).commit();
                                getOwner().reloadSettings();
                            }
                        }
                    })
                    .create();
                case DLG_RESET_TO_DEFAULT:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getOwner().resetToDefault();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
        }
    }

}
