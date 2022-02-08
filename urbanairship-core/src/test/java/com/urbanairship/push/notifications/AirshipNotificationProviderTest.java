/* Copyright Airship and Contributors */

package com.urbanairship.push.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.AirshipExecutors;
import com.urbanairship.BaseTestCase;
import com.urbanairship.ShadowAirshipExecutorsLegacy;
import com.urbanairship.ShadowAirshipExecutorsPaused;
import com.urbanairship.TestApplication;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushMessage;
import com.urbanairship.shadow.ShadowNotificationManagerExtension;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(
    sdk = 28,
    shadows = { ShadowNotificationManagerExtension.class, ShadowAirshipExecutorsLegacy.class },
    application = TestApplication.class
)
@LooperMode(LooperMode.Mode.LEGACY)
public class AirshipNotificationProviderTest extends BaseTestCase {

    private AirshipNotificationProvider provider;
    private Context context = UAirship.getApplicationContext();
    private AirshipConfigOptions configOptions;
    private PushMessage pushMessage;

    @Before
    public void setup() {
        configOptions = new AirshipConfigOptions.Builder()
                .setDevelopmentAppKey("appKey")
                .setDevelopmentAppSecret("appSecret")
                .setProductionAppSecret("appSecret")
                .setProductionAppKey("appKey")
                .setNotificationIcon(10)
                .setNotificationAccentColor(20)
                .setNotificationChannel("test_channel")
                .build();

        createChannel("test_channel");

        provider = new AirshipNotificationProvider(context, configOptions);

        Bundle extras = new Bundle();
        extras.putString(PushMessage.EXTRA_ALERT, "Test Push Alert!");
        extras.putString(PushMessage.EXTRA_PUSH_ID, "0a2027a0-1766-11e4-9db0-90e2ba287ae5");
        extras.putString(PushMessage.EXTRA_NOTIFICATION_TAG, "some-tag");

        pushMessage = new PushMessage(extras);
    }

    /**
     * Test empty alert should result in a CANCEL status.
     */
    @Test
    public void testBuildNotificationNull() {
        Bundle empty = new Bundle();
        PushMessage emptyPushMessage = new PushMessage(empty);

        NotificationArguments arguments = provider.onCreateNotificationArguments(context, emptyPushMessage);
        NotificationResult result = provider.onCreateNotification(context, arguments);
        assertEquals(NotificationResult.CANCEL, result.getStatus());
    }

    /**
     * Test creating a notification.
     */
    @Test
    public void testBuildNotification() {
        NotificationArguments arguments = provider.onCreateNotificationArguments(context, pushMessage);
        NotificationResult result = provider.onCreateNotification(context, arguments);

        assertEquals(NotificationResult.OK, result.getStatus());
        assertNotNull(result.getNotification());
    }

    /**
     * Test the defaults.
     */
    @Test
    public void testDefaults() {
        assertEquals(10, provider.getSmallIcon());
        assertEquals(20, provider.getDefaultAccentColor());
        assertEquals("test_channel", provider.getDefaultNotificationChannelId());
    }

    /**
     * Test arguments when the channel is empty.
     */
    @Test
    public void testArgumentsDefaultChannel() {
        provider.setDefaultNotificationChannelId("does not exist");
        NotificationArguments arguments = provider.onCreateNotificationArguments(context, pushMessage);

        assertEquals(NotificationProvider.DEFAULT_NOTIFICATION_CHANNEL, arguments.getNotificationChannelId());
    }

    /**
     * Test the channel will fallback to the SDK default channel if the specified channel does not exist.
     */
    @Test
    public void testArgumentsFallbackChannel() {
        NotificationArguments arguments = provider.onCreateNotificationArguments(context, pushMessage);

        assertEquals("test_channel", arguments.getNotificationChannelId());
        assertEquals(pushMessage, arguments.getMessage());
        assertEquals(AirshipNotificationProvider.TAG_NOTIFICATION_ID, arguments.getNotificationId());
        assertEquals("some-tag", arguments.getNotificationTag());
        assertFalse(arguments.getRequiresLongRunningTask());
    }

    /**
     * Test arguments when the channel is defined on the push message.
     */
    @Test
    public void testArgumentsWithChannel() {
        Bundle extras = new Bundle();
        extras.putString(PushMessage.EXTRA_ALERT, "Test Push Alert!");
        extras.putString(PushMessage.EXTRA_PUSH_ID, "0a2027a0-1766-11e4-9db0-90e2ba287ae5");
        extras.putString(PushMessage.EXTRA_NOTIFICATION_CHANNEL, "cool-channel");
        extras.putString(PushMessage.EXTRA_NOTIFICATION_TAG, "some-tag");

        createChannel("cool-channel");

        pushMessage = new PushMessage(extras);

        NotificationArguments arguments = provider.onCreateNotificationArguments(context, pushMessage);
        assertEquals("cool-channel", arguments.getNotificationChannelId());
        assertEquals(pushMessage, arguments.getMessage());
        assertEquals(AirshipNotificationProvider.TAG_NOTIFICATION_ID, arguments.getNotificationId());
        assertEquals("some-tag", arguments.getNotificationTag());
        assertFalse(arguments.getRequiresLongRunningTask());
    }

    /**
     * Test overriding the small icon from a push.
     */
    @Test
    public void testOverrideSmallIcon() {
        PushMessage mockPush = mock(PushMessage.class);
        when(mockPush.getAlert()).thenReturn("alert");
        when(mockPush.getIcon(eq(context), anyInt())).thenReturn(100);

        NotificationArguments arguments = provider.onCreateNotificationArguments(context, mockPush);
        NotificationResult result = provider.onCreateNotification(context, arguments);

        assertEquals(NotificationResult.OK, result.getStatus());
        assertEquals(100, result.getNotification().getSmallIcon().getResId());
    }

    private void createChannel(@NonNull String channelId) {
        NotificationManager manager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(new NotificationChannel(channelId, "test", NotificationManager.IMPORTANCE_DEFAULT));
    }

}
