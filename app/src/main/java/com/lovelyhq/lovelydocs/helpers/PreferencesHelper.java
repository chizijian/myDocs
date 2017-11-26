package com.lovelyhq.lovelydocs.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferencesHelper {
    private static final String PREFERENCES_NAME = "lovely_docs";
    private static final String PREF_IS_FIRST_RUN = "is_first_run";
    private static final String PREF_IS_LARGE_SCREEN = "is_large_screen";
    private static final String PREF_IS_USER_PREMIUM = "is_user_premium";
    private static final String PREF_MOBILE_ENABLED = "mobile_enabled";
    private static final String PREF_UPDATES_ENABLED = "updates_enabled";
    private Context context;
    private boolean isFirstRun;
    private boolean isLargeScreen;
    private boolean isUserPremium;
    private boolean mobileEnabled;
    private boolean updatesEnabled;

    public PreferencesHelper(Context context) {
        this.context = context;
        getAllPreferences();
    }

    private void getAllPreferences() {
        SharedPreferences shPrefs = this.context.getSharedPreferences(PREFERENCES_NAME, 0);
        this.mobileEnabled = shPrefs.getBoolean(PREF_MOBILE_ENABLED, false);
        this.updatesEnabled = shPrefs.getBoolean(PREF_UPDATES_ENABLED, false);
        this.isLargeScreen = shPrefs.getBoolean(PREF_IS_LARGE_SCREEN, false);
        this.isUserPremium = shPrefs.getBoolean(PREF_IS_USER_PREMIUM, false);
        this.isFirstRun = shPrefs.getBoolean(PREF_IS_FIRST_RUN, true);
    }

    public void clearLegacyPreferences() {
        PreferenceManager.getDefaultSharedPreferences(this.context).edit().clear().apply();
    }

    public boolean isFirstRun() {
        return this.isFirstRun;
    }

    public void setFirstRun(boolean isFirstRun) {
        Editor editor = this.context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        editor.putBoolean(PREF_IS_FIRST_RUN, isFirstRun);
        if (editor.commit()) {
            this.isFirstRun = isFirstRun;
        }
    }

    public boolean isMobileEnabled() {
        return this.mobileEnabled=true;
    }

    public void setMobileEnabled(boolean mobileEnabled) {
        Editor editor = this.context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        editor.putBoolean(PREF_MOBILE_ENABLED, mobileEnabled);
        if (editor.commit()) {
            this.mobileEnabled = mobileEnabled;
        }
    }

    public boolean isUpdatesEnabled() {
        return this.updatesEnabled;
    }

    public void setUpdatesEnabled(boolean updatesEnabled) {
        Editor editor = this.context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        editor.putBoolean(PREF_UPDATES_ENABLED, updatesEnabled);
        if (editor.commit()) {
            this.updatesEnabled = updatesEnabled;
        }
    }

    public boolean isLargeScreen() {
        return this.isLargeScreen;
    }

    public void setLargeScreen(boolean isLargeScreen) {
        Editor editor = this.context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        editor.putBoolean(PREF_IS_LARGE_SCREEN, isLargeScreen);
        if (editor.commit()) {
            this.isLargeScreen = isLargeScreen;
        }
    }

    public boolean isUserPremium() {
        return this.isUserPremium;
    }

    public void setUserPremium(boolean isUserPremium) {
        Editor editor = this.context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        editor.putBoolean(PREF_IS_USER_PREMIUM, isUserPremium);
        if (editor.commit()) {
            this.isUserPremium = isUserPremium;
        }
    }
}
