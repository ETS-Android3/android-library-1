package com.urbanairship.config;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.BaseTestCase;
import com.urbanairship.PreferenceDataStore;
import com.urbanairship.TestApplication;
import com.urbanairship.remoteconfig.RemoteAirshipConfig;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class RemoteAirshipUrlConfigProviderTest extends BaseTestCase {
    private PreferenceDataStore dataStore;

    @Before
    public void setup() {
        this.dataStore = TestApplication.getApplication().preferenceDataStore;
    }

    @Test
    public void testDefaultUrls() {
        AirshipConfigOptions configOptions = AirshipConfigOptions.newBuilder().build();
        RemoteAirshipUrlConfigProvider provider = new RemoteAirshipUrlConfigProvider(configOptions, dataStore);

        AirshipUrlConfig urlConfig = provider.getConfig();

        assertEquals(configOptions.deviceUrl, urlConfig.deviceUrl().build().toString());
        assertEquals(configOptions.remoteDataUrl, urlConfig.remoteDataUrl().build().toString());
        assertEquals(configOptions.analyticsUrl, urlConfig.analyticsUrl().build().toString());
        assertEquals(configOptions.walletUrl, urlConfig.walletUrl().build().toString());
    }

    @Test
    public void tesDefaultUrlsDisableFallback() {
        AirshipConfigOptions configOptions = AirshipConfigOptions.newBuilder().build();
        RemoteAirshipUrlConfigProvider provider = new RemoteAirshipUrlConfigProvider(configOptions, dataStore);
        provider.disableFallbackUrls();

        AirshipUrlConfig urlConfig = provider.getConfig();

        assertNull(urlConfig.deviceUrl().build());
        assertNull(urlConfig.analyticsUrl().build());
        assertNull(urlConfig.walletUrl().build());

        // Remote-data should fallback so we can retrieve the other URLs
        assertEquals(configOptions.remoteDataUrl, urlConfig.remoteDataUrl().build().toString());
    }

    @Test
    public void testUpdatingUrls() {
        RemoteAirshipConfig remoteConfig = new RemoteAirshipConfig("http://remote", "http://device", "http://wallet", "http://analytics");
        AirshipConfigOptions configOptions = AirshipConfigOptions.newBuilder().build();

        RemoteAirshipUrlConfigProvider provider = new RemoteAirshipUrlConfigProvider(configOptions, dataStore);
        provider.onRemoteConfigUpdated(remoteConfig);

        AirshipUrlConfig urlConfig = provider.getConfig();

        assertEquals("http://device", urlConfig.deviceUrl().build().toString());
        assertEquals("http://remote", urlConfig.remoteDataUrl().build().toString());
        assertEquals("http://analytics", urlConfig.analyticsUrl().build().toString());
        assertEquals("http://wallet", urlConfig.walletUrl().build().toString());
    }

    @Test
    public void testEmptyRemoteConfigFallbacksToOptions() {
        AirshipConfigOptions configOptions = AirshipConfigOptions.newBuilder().build();
        RemoteAirshipUrlConfigProvider provider = new RemoteAirshipUrlConfigProvider(configOptions, dataStore);

        provider.onRemoteConfigUpdated(new RemoteAirshipConfig(null, null, null, null));
        AirshipUrlConfig urlConfig = provider.getConfig();

        assertEquals(configOptions.deviceUrl, urlConfig.deviceUrl().build().toString());
        assertEquals(configOptions.remoteDataUrl, urlConfig.remoteDataUrl().build().toString());
        assertEquals(configOptions.analyticsUrl, urlConfig.analyticsUrl().build().toString());
        assertEquals(configOptions.walletUrl, urlConfig.walletUrl().build().toString());
    }

    @Test
    public void testRequireInitialRemoteConfig() {
        AirshipConfigOptions configOptions = AirshipConfigOptions.newBuilder()
                                                                 .setRequireInitialRemoteConfigEnabled(true)
                                                                 .build();

        RemoteAirshipUrlConfigProvider provider = new RemoteAirshipUrlConfigProvider(configOptions, dataStore);

        assertNull(provider.getConfig().deviceUrl().build());
        assertNull(provider.getConfig().analyticsUrl().build());
        assertNull(provider.getConfig().walletUrl().build());
        assertEquals(configOptions.remoteDataUrl, provider.getConfig().remoteDataUrl().build().toString());

        RemoteAirshipConfig remoteConfig = new RemoteAirshipConfig("http://remote", "http://device", "http://wallet", "http://analytics");
        provider.onRemoteConfigUpdated(remoteConfig);

        assertEquals("http://device", provider.getConfig().deviceUrl().build().toString());
        assertEquals("http://remote", provider.getConfig().remoteDataUrl().build().toString());
        assertEquals("http://analytics", provider.getConfig().analyticsUrl().build().toString());
        assertEquals("http://wallet", provider.getConfig().walletUrl().build().toString());
    }

    @Test
    public void testCacheRemoteAirshipConfig() {
        RemoteAirshipConfig remoteConfig = new RemoteAirshipConfig("http://remote", "http://device", "http://wallet", "http://analytics");
        AirshipConfigOptions configOptions = AirshipConfigOptions.newBuilder().build();

        RemoteAirshipUrlConfigProvider provider = new RemoteAirshipUrlConfigProvider(configOptions, dataStore);
        provider.onRemoteConfigUpdated(remoteConfig);

        // Recreate it
        provider = new RemoteAirshipUrlConfigProvider(configOptions, dataStore);
        AirshipUrlConfig urlConfig = provider.getConfig();

        assertEquals("http://device", urlConfig.deviceUrl().build().toString());
        assertEquals("http://remote", urlConfig.remoteDataUrl().build().toString());
        assertEquals("http://analytics", urlConfig.analyticsUrl().build().toString());
        assertEquals("http://wallet", urlConfig.walletUrl().build().toString());
    }

}
