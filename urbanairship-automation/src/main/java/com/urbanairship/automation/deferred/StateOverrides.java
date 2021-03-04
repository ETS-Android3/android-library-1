/* Copyright Airship and Contributors */

package com.urbanairship.automation.deferred;

import android.content.pm.PackageInfo;

import com.urbanairship.UAirship;
import com.urbanairship.json.JsonMap;
import com.urbanairship.json.JsonSerializable;
import com.urbanairship.json.JsonValue;
import com.urbanairship.push.PushManager;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

/**
 * A model defining state overrides.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class StateOverrides implements JsonSerializable {

    private static final String STATE_APP_VERSION_KEY = "app_version";
    private static final String STATE_SDK_VERSION_KEY = "sdk_version";
    private static final String STATE_NOTIFICATION_OPT_IN_KEY = "notification_opt_in";
    private static final String STATE_LOCALE_LANGUAGE_KEY = "locale_language";
    private static final String STATE_LOCALE_COUNTRY_KEY = "locale_country";

    private final String appVersionName;
    private final String sdkVersion;
    private final boolean notificationOptIn;
    private final String localeLanguage;
    private final String localeCountry;

    /**
     * Default state overrides constructor.
     */
    @VisibleForTesting
    StateOverrides(String appVersionName, @NonNull String sdkVersion, boolean notificationOptIn, @NonNull Locale locale) {
        this.appVersionName = appVersionName;
        this.sdkVersion = sdkVersion;
        this.notificationOptIn = notificationOptIn;
        localeLanguage = locale.getLanguage();
        localeCountry = locale.getCountry();
    }

    /**
     * Creates a state overrides.
     *
     * @return An instance of StateOverrides.
     */
    @NonNull
    public static StateOverrides defaultOverrides() {
        PushManager pushManager = UAirship.shared().getPushManager();
        Locale locale = UAirship.shared().getLocale();
        PackageInfo packageInfo = UAirship.getPackageInfo();
        String versionName = packageInfo != null ? packageInfo.versionName : "";

        return new StateOverrides(versionName, UAirship.getVersion(), pushManager.isOptIn(), locale);
    }

    @NonNull
    @Override
    public JsonValue toJsonValue() {
        return JsonMap.newBuilder()
                .put(STATE_APP_VERSION_KEY, appVersionName)
                .put(STATE_SDK_VERSION_KEY, sdkVersion)
                .put(STATE_NOTIFICATION_OPT_IN_KEY, notificationOptIn)
                .put(STATE_LOCALE_LANGUAGE_KEY, localeLanguage)
                .put(STATE_LOCALE_COUNTRY_KEY, localeCountry)
                .build()
                .toJsonValue();
    }
}
