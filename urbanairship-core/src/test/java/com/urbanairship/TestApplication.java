/* Copyright Airship and Contributors */

package com.urbanairship;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.ProviderInfo;

import com.urbanairship.actions.ActionRegistry;
import com.urbanairship.analytics.Analytics;
import com.urbanairship.automation.Automation;
import com.urbanairship.channel.AirshipChannel;
import com.urbanairship.channel.NamedUser;
import com.urbanairship.channel.TagGroupRegistrar;
import com.urbanairship.iam.InAppMessageManager;
import com.urbanairship.iam.LegacyInAppMessageManager;
import com.urbanairship.js.Whitelist;
import com.urbanairship.location.UALocationManager;
import com.urbanairship.messagecenter.MessageCenter;
import com.urbanairship.modules.AccengageNotificationHandler;
import com.urbanairship.push.PushManager;
import com.urbanairship.remoteconfig.RemoteConfigManager;
import com.urbanairship.remotedata.RemoteData;
import com.urbanairship.richpush.RichPushInbox;
import com.urbanairship.util.PlatformUtils;

import org.robolectric.Robolectric;
import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import androidx.test.core.app.ApplicationProvider;

public class TestApplication extends Application implements TestLifecycleApplication {

    public ActivityLifecycleCallbacks callback;
    public PreferenceDataStore preferenceDataStore;

    private TestAirshipRuntimeConfig testRuntimeConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        this.preferenceDataStore = new PreferenceDataStore(getApplicationContext());
        preferenceDataStore.executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };

        testRuntimeConfig = TestAirshipRuntimeConfig.newTestConfig();
        AirshipConfigOptions airshipConfigOptions = testRuntimeConfig.getConfigOptions();

        UAirship.application = this;
        UAirship.isFlying = true;
        UAirship.isTakingOff = true;

        UAirship.sharedAirship = new UAirship(airshipConfigOptions);
        UAirship.sharedAirship.preferenceDataStore = preferenceDataStore;


        UAirship.sharedAirship.runtimeConfig = testRuntimeConfig;

        TagGroupRegistrar tagGroupRegistrar = new TagGroupRegistrar(UAirship.sharedAirship.runtimeConfig, preferenceDataStore);

        UAirship.sharedAirship.channel = new AirshipChannel(this, preferenceDataStore, UAirship.sharedAirship.runtimeConfig, tagGroupRegistrar);

        UAirship.sharedAirship.analytics = new Analytics(this, preferenceDataStore, testRuntimeConfig, UAirship.sharedAirship.channel);

        UAirship.sharedAirship.applicationMetrics = new ApplicationMetrics(this, preferenceDataStore, new TestActivityMonitor());
        UAirship.sharedAirship.inbox = new RichPushInbox(this, preferenceDataStore, UAirship.sharedAirship.channel);
        UAirship.sharedAirship.locationManager = new UALocationManager(this, preferenceDataStore, UAirship.sharedAirship.channel, UAirship.sharedAirship.analytics);
        UAirship.sharedAirship.pushManager = new PushManager(this, preferenceDataStore, airshipConfigOptions, new TestPushProvider(), UAirship.sharedAirship.channel, UAirship.sharedAirship.analytics);
        UAirship.sharedAirship.channelCapture = new ChannelCapture(this, airshipConfigOptions, UAirship.sharedAirship.channel, preferenceDataStore, new TestActivityMonitor());
        UAirship.sharedAirship.whitelist = Whitelist.createDefaultWhitelist(airshipConfigOptions);
        UAirship.sharedAirship.actionRegistry = new ActionRegistry();
        UAirship.sharedAirship.actionRegistry.registerDefaultActions(this);
        UAirship.sharedAirship.messageCenter = new MessageCenter(this, preferenceDataStore);
        UAirship.sharedAirship.namedUser = new NamedUser(this, preferenceDataStore, tagGroupRegistrar, UAirship.sharedAirship.channel);
        UAirship.sharedAirship.automation = new Automation(this, preferenceDataStore, airshipConfigOptions, UAirship.sharedAirship.analytics, new TestActivityMonitor());
        UAirship.sharedAirship.legacyInAppMessageManager = new LegacyInAppMessageManager(this, preferenceDataStore, UAirship.sharedAirship.inAppMessageManager, UAirship.sharedAirship.analytics);
        UAirship.sharedAirship.remoteData = new RemoteData(this, preferenceDataStore, airshipConfigOptions, new TestActivityMonitor());
        UAirship.sharedAirship.inAppMessageManager = new InAppMessageManager(this, preferenceDataStore, UAirship.sharedAirship.runtimeConfig, UAirship.sharedAirship.analytics,
                UAirship.sharedAirship.remoteData, new TestActivityMonitor(), UAirship.sharedAirship.channel, tagGroupRegistrar);
        UAirship.sharedAirship.remoteConfigManager = new RemoteConfigManager(this, preferenceDataStore, UAirship.sharedAirship.remoteData);

        ProviderInfo info = new ProviderInfo();
        info.authority = UrbanAirshipProvider.getAuthorityString(this);
        Robolectric.buildContentProvider(UrbanAirshipProvider.class).create(info);
    }

    public void setPlatform(int platform) {
        testRuntimeConfig.setPlatform(PlatformUtils.parsePlatform(platform));
    }

    public static TestApplication getApplication() {
        return (TestApplication) ApplicationProvider.getApplicationContext();
    }

    public void setApplicationMetrics(ApplicationMetrics metrics) {
        UAirship.shared().applicationMetrics = metrics;
    }

    public void setNamedUser(NamedUser namedUser) {
        UAirship.shared().namedUser = namedUser;
    }

    public void setAnalytics(Analytics analytics) {
        UAirship.shared().analytics = analytics;
    }

    public void setLegacyInAppMessageManager(LegacyInAppMessageManager legacyInAppMessageManager) {
        UAirship.shared().legacyInAppMessageManager = legacyInAppMessageManager;
    }

    public void setInAppMessageManager(InAppMessageManager inAppMessageManager) {
        UAirship.shared().inAppMessageManager = inAppMessageManager;
    }

    public void setOptions(AirshipConfigOptions options) {
        UAirship.shared().airshipConfigOptions = options;
    }


    public void setAccengageNotificationHandler(AccengageNotificationHandler notificationHandler) {
        UAirship.shared().accengageNotificationHandler = notificationHandler;
    }

    @Override
    public void afterTest(Method method) {
    }

    @Override
    public void beforeTest(Method method) {
    }

    @Override
    public void prepareTest(Object test) {

    }

    @Override
    @SuppressLint("NewApi")
    public void registerActivityLifecycleCallbacks(
            Application.ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
        this.callback = callback;
    }

    public void setChannel(AirshipChannel channel) {
        UAirship.shared().channel = channel;
    }

    public void setPushManager(PushManager pushManager) {
        UAirship.shared().pushManager = pushManager;
    }

    public void setLocationManager(UALocationManager locationManager) {
        UAirship.shared().locationManager = locationManager;
    }

    public void setInbox(RichPushInbox inbox) {
        UAirship.shared().inbox = inbox;
    }

    public void setMessageCenter(MessageCenter messageCenter) {
        UAirship.sharedAirship.messageCenter = messageCenter;
    }

    public void setAutomation(Automation automation) {
        UAirship.shared().automation = automation;
    }

    public void setChannelCapture(ChannelCapture channelCapture) {
        UAirship.shared().channelCapture = channelCapture;
    }

}
