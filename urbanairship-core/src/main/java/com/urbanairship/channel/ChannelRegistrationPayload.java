/* Copyright Airship and Contributors */

package com.urbanairship.channel;

import com.urbanairship.json.JsonException;
import com.urbanairship.json.JsonMap;
import com.urbanairship.json.JsonSerializable;
import com.urbanairship.json.JsonValue;
import com.urbanairship.push.PushProvider;
import com.urbanairship.util.UAStringUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringDef;

/**
 * Model object encapsulating the data relevant to a creation or updates processed by ChannelApiClient.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class ChannelRegistrationPayload implements JsonSerializable {

    @StringDef({ AMAZON_DEVICE_TYPE, ANDROID_DEVICE_TYPE })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DeviceType {}

    @NonNull
    public static final String ANDROID_DEVICE_TYPE = "android";

    @NonNull
    public static final String AMAZON_DEVICE_TYPE = "amazon";

    static final String CHANNEL_KEY = "channel";
    static final String DEVICE_TYPE_KEY = "device_type";
    static final String OPT_IN_KEY = "opt_in";
    static final String BACKGROUND_ENABLED_KEY = "background";
    static final String PUSH_ADDRESS_KEY = "push_address";
    static final String SET_TAGS_KEY = "set_tags";
    static final String TAGS_KEY = "tags";
    static final String IDENTITY_HINTS_KEY = "identity_hints";
    static final String USER_ID_KEY = "user_id";
    static final String TIMEZONE_KEY = "timezone";
    static final String LANGUAGE_KEY = "locale_language";
    static final String COUNTRY_KEY = "locale_country";
    static final String LOCATION_SETTINGS_KEY = "location_settings";
    static final String APP_VERSION_KEY = "app_version";
    static final String SDK_VERSION_KEY = "sdk_version";
    static final String DEVICE_MODEL_KEY = "device_model";
    static final String API_VERSION_KEY = "android_api_version";
    static final String CARRIER_KEY = "carrier";
    static final String ACCENGAGE_DEVICE_ID = "accengage_device_id";
    static final String NAMED_USER_ID_KEY = "named_user_id";

    static final String ANDROID_EXTRAS_KEY = "android";
    static final String ANDROID_DELIVERY_TYPE = "delivery_type";

    public final boolean optIn;
    public final boolean backgroundEnabled;
    public final String deviceType;
    public final String pushAddress;
    public final boolean setTags;
    public final Set<String> tags;
    public final String userId;
    public final String timezone;
    public final String language;
    public final String country;
    public final Boolean locationSettings;
    public final String appVersion;
    public final String sdkVersion;
    public final String deviceModel;
    public final Integer apiVersion;
    public final String carrier;
    public final String accengageDeviceId;
    public final String deliveryType;
    public final String namedUserId;

    /**
     * Builds the ChannelRegistrationPayload
     */
    public static class Builder {

        private boolean optIn;
        private boolean backgroundEnabled;
        private String deviceType;
        private String pushAddress;
        private boolean setTags;
        private Set<String> tags;
        private String userId;
        private String timezone;
        private String language;
        private String country;
        private Boolean locationSettings;
        private String appVersion;
        private String sdkVersion;
        private String deviceModel;
        private Integer apiVersion;
        private String carrier;
        private String accengageDeviceId;
        private String deliveryType;
        private String namedUserId;

        /**
         * Default ChannelRegistrationPayload.Builder constructor
         */
        public Builder() {}

        /**
         * ChannelRegistrationPayload.Builder constructor that draws from an existing payload.
         *
         * @param payload The payload.
         */
        public Builder(@NonNull ChannelRegistrationPayload payload) {
            this.optIn = payload.optIn;
            this.backgroundEnabled = payload.backgroundEnabled;
            this.deviceType = payload.deviceType;
            this.pushAddress = payload.pushAddress;
            this.setTags = payload.setTags;
            this.tags = payload.tags;
            this.userId = payload.userId;
            this.timezone = payload.timezone;
            this.language = payload.language;
            this.country = payload.country;
            this.locationSettings = payload.locationSettings;
            this.appVersion = payload.appVersion;
            this.sdkVersion = payload.sdkVersion;
            this.deviceModel = payload.deviceModel;
            this.apiVersion = payload.apiVersion;
            this.carrier = payload.carrier;
            this.accengageDeviceId = payload.accengageDeviceId;
            this.deliveryType = payload.deliveryType;
            this.namedUserId = payload.namedUserId;
        }

        /**
         * Set the optIn value
         *
         * @param optIn A boolean value indicating if optIn is true or false.
         * @return The builder with optIn value set
         */
        @NonNull
        public Builder setOptIn(boolean optIn) {
            this.optIn = optIn;
            return this;
        }

        /**
         * Set the background enabled value.
         *
         * @param enabled enabled A boolean value indicating whether background push is enabled.
         * @return The builder with the background push enabled value set.
         */
        @NonNull
        public Builder setBackgroundEnabled(boolean enabled) {
            this.backgroundEnabled = enabled;
            return this;
        }

        /**
         * Set the device type
         *
         * @param deviceType A string value
         * @return The builder with device type set
         */
        @NonNull
        public Builder setDeviceType(@Nullable @DeviceType String deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        /**
         * Set the named user ID.
         *
         * @param namedUserId A named user ID.
         * @return The builder instance.
         */
        @NonNull
        public Builder setNamedUserId(@Nullable String namedUserId) {
            this.namedUserId = namedUserId;
            return this;
        }

        /**
         * Set the device timezone
         *
         * @param timezone A string value of the timezone ID
         * @return The builder with timezone ID set
         */
        @NonNull
        public Builder setTimezone(@Nullable String timezone) {
            this.timezone = timezone;
            return this;
        }

        /**
         * Set the device language
         *
         * @param language A string value of the language ID
         * @return The builder with language ID set
         */
        @NonNull
        public Builder setLanguage(@Nullable String language) {
            this.language = language;
            return this;
        }

        /**
         * Set the device country
         *
         * @param country A string value of the country ID
         * @return The builder with country ID set
         */
        @NonNull
        public Builder setCountry(@Nullable String country) {
            this.country = country;
            return this;
        }

        /**
         * Set the push address
         *
         * @param registrationId A string value
         * @return The builder with push address set
         */
        @NonNull
        public Builder setPushAddress(@Nullable String registrationId) {
            this.pushAddress = registrationId;
            return this;
        }

        /**
         * Set tags
         *
         * @param channelTagRegistrationEnabled A boolean value indicating whether tags are enabled on the device.
         * @param tags A set of tags
         * @return The builder with channelTagRegistrationEnabled and tags set
         */
        @NonNull
        public Builder setTags(boolean channelTagRegistrationEnabled, @Nullable Set<String> tags) {
            this.setTags = channelTagRegistrationEnabled;
            this.tags = tags;
            return this;
        }

        /**
         * Set the userId
         *
         * @param userId A string value
         * @return The builder with userId value set
         */
        @NonNull
        public Builder setUserId(@Nullable String userId) {
            this.userId = UAStringUtil.isEmpty(userId) ? null : userId;
            return this;
        }

        /**
         * Set the location settings
         *
         * @param locationSettings The location settings
         * @return The builder.
         */
        @NonNull
        public Builder setLocationSettings(@Nullable Boolean locationSettings) {
            this.locationSettings = locationSettings;
            return this;
        }

        /**
         * Set the app version
         *
         * @param appVersion The app version
         * @return The builder.
         */
        @NonNull
        public Builder setAppVersion(@Nullable String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        /**
         * Set the SDK version
         *
         * @param sdkVersion The SDK version
         * @return The builder.
         */
        @NonNull
        public Builder setSdkVersion(@Nullable String sdkVersion) {
            this.sdkVersion = sdkVersion;
            return this;
        }

        /**
         * Set the device model
         *
         * @param deviceModel The device model
         * @return The builder.
         */
        @NonNull
        public Builder setDeviceModel(@Nullable String deviceModel) {
            this.deviceModel = deviceModel;
            return this;
        }

        /**
         * Set the API version
         *
         * @param apiVersion The API version.
         * @return The builder.
         */
        @NonNull
        public Builder setApiVersion(@Nullable Integer apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        /**
         * Set the carrier
         *
         * @param carrier The carrier
         * @return The builder.
         */
        @NonNull
        public Builder setCarrier(@Nullable String carrier) {
            this.carrier = carrier;
            return this;
        }

        /**
         * Set the Accengage Device ID
         *
         * @param accengageDeviceId The Accengage Device ID
         * @return The builder.
         */
        @NonNull
        public Builder setAccengageDeviceId(@Nullable String accengageDeviceId) {
            this.accengageDeviceId = accengageDeviceId;
            return this;
        }

        /**
         * Set the delivery type.
         *
         * @param deliveryType The delivery type.
         * @return The builder.
         */
        @NonNull
        public Builder setDeliveryType(@Nullable @PushProvider.DeliveryType String deliveryType) {
            this.deliveryType = deliveryType;
            return this;
        }

        @NonNull
        public ChannelRegistrationPayload build() {
            return new ChannelRegistrationPayload(this);
        }

    }

    private ChannelRegistrationPayload(Builder builder) {
        this.optIn = builder.optIn;
        this.backgroundEnabled = builder.backgroundEnabled;
        this.deviceType = builder.deviceType;
        this.pushAddress = builder.pushAddress;
        this.setTags = builder.setTags;
        this.tags = builder.setTags ? builder.tags : null;
        this.userId = builder.userId;
        this.timezone = builder.timezone;
        this.language = builder.language;
        this.country = builder.country;
        this.locationSettings = builder.locationSettings;
        this.appVersion = builder.appVersion;
        this.sdkVersion = builder.sdkVersion;
        this.deviceModel = builder.deviceModel;
        this.apiVersion = builder.apiVersion;
        this.carrier = builder.carrier;
        this.accengageDeviceId = builder.accengageDeviceId;
        this.deliveryType = builder.deliveryType;
        this.namedUserId = builder.namedUserId;
    }

    @NonNull
    public ChannelRegistrationPayload minimizedPayload(@Nullable ChannelRegistrationPayload last) {
        if (last == null) {
            return this;
        }

        Builder builder = new Builder(this);
        builder.setUserId(null);
        builder.setAccengageDeviceId(null);

        if (last.setTags && setTags) {
            if (last.tags != null && last.tags.equals(tags)) {
                builder.setTags(false, null);
            }
        }

        // Only remove attributes if named user Id is null or is the same as the last payload
        if (namedUserId == null || UAStringUtil.equals(last.namedUserId, namedUserId)) {
            if (UAStringUtil.equals(last.country, country)) {
                builder.setCountry(null);
            }

            if (UAStringUtil.equals(last.language, language)) {
                builder.setLanguage(null);
            }

            if (UAStringUtil.equals(last.timezone, timezone)) {
                builder.setTimezone(null);
            }

            if (last.locationSettings != null && last.locationSettings.equals(locationSettings)) {
                builder.setLocationSettings(null);
            }

            if (UAStringUtil.equals(last.appVersion, appVersion)) {
                builder.setAppVersion(null);
            }

            if (UAStringUtil.equals(last.sdkVersion, sdkVersion)) {
                builder.setSdkVersion(null);
            }

            if (UAStringUtil.equals(last.deviceModel, deviceModel)) {
                builder.setDeviceModel(null);
            }

            if (UAStringUtil.equals(last.carrier, carrier)) {
                builder.setCarrier(null);
            }

            if (last.apiVersion != null && last.apiVersion.equals(apiVersion)) {
                builder.setApiVersion(null);
            }
        }

        return builder.build();
    }

    @NonNull
    @Override
    public JsonValue toJsonValue() {
        // Channel Payload
        JsonMap.Builder channel = JsonMap.newBuilder()
                                         .put(DEVICE_TYPE_KEY, deviceType)
                                         .put(SET_TAGS_KEY, setTags)
                                         .put(OPT_IN_KEY, optIn)
                                         .put(PUSH_ADDRESS_KEY, pushAddress)
                                         .put(BACKGROUND_ENABLED_KEY, backgroundEnabled)
                                         .put(TIMEZONE_KEY, timezone)
                                         .put(LANGUAGE_KEY, language)
                                         .put(COUNTRY_KEY, country)
                                         .put(APP_VERSION_KEY, appVersion)
                                         .put(SDK_VERSION_KEY, sdkVersion)
                                         .put(DEVICE_MODEL_KEY, deviceModel)
                                         .put(CARRIER_KEY, carrier)
                                         .put(NAMED_USER_ID_KEY, namedUserId);

        if (ANDROID_DEVICE_TYPE.equals(deviceType) && deliveryType != null) {
            channel.put(ANDROID_EXTRAS_KEY, JsonMap.newBuilder()
                                                   .put(ANDROID_DELIVERY_TYPE, deliveryType)
                                                   .build());
        }

        if (locationSettings != null) {
            channel.put(LOCATION_SETTINGS_KEY, locationSettings);
        }

        if (apiVersion != null) {
            channel.put(API_VERSION_KEY, apiVersion);
        }

        // If setTags is TRUE, then include the tags
        if (setTags && tags != null) {
            channel.put(TAGS_KEY, JsonValue.wrapOpt(tags).getList());
        }

        // Identity hints
        JsonMap.Builder identityHints = JsonMap.newBuilder()
                                               .put(USER_ID_KEY, userId)
                                               .put(ACCENGAGE_DEVICE_ID, accengageDeviceId);

        // Full payload
        JsonMap.Builder data = JsonMap.newBuilder()
                                      .put(CHANNEL_KEY, channel.build());

        JsonMap identityHintsMap = identityHints.build();
        if (!identityHintsMap.isEmpty()) {
            data.put(IDENTITY_HINTS_KEY, identityHintsMap);
        }

        return data.build().toJsonValue();
    }

    /**
     * The ChannelRegistrationPayload as a JSON formatted string
     *
     * @return The JSON formatted payload as a string
     */
    @NonNull
    @Override
    public String toString() {
        return this.toJsonValue().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelRegistrationPayload that = (ChannelRegistrationPayload) o;

        if (optIn != that.optIn) return false;
        if (backgroundEnabled != that.backgroundEnabled) return false;
        if (setTags != that.setTags) return false;
        if (deviceType != null ? !deviceType.equals(that.deviceType) : that.deviceType != null)
            return false;
        if (pushAddress != null ? !pushAddress.equals(that.pushAddress) : that.pushAddress != null)
            return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (timezone != null ? !timezone.equals(that.timezone) : that.timezone != null)
            return false;
        if (language != null ? !language.equals(that.language) : that.language != null)
            return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (locationSettings != null ? !locationSettings.equals(that.locationSettings) : that.locationSettings != null)
            return false;
        if (appVersion != null ? !appVersion.equals(that.appVersion) : that.appVersion != null)
            return false;
        if (sdkVersion != null ? !sdkVersion.equals(that.sdkVersion) : that.sdkVersion != null)
            return false;
        if (deviceModel != null ? !deviceModel.equals(that.deviceModel) : that.deviceModel != null)
            return false;
        if (apiVersion != null ? !apiVersion.equals(that.apiVersion) : that.apiVersion != null)
            return false;
        if (carrier != null ? !carrier.equals(that.carrier) : that.carrier != null) return false;
        if (accengageDeviceId != null ? !accengageDeviceId.equals(that.accengageDeviceId) : that.accengageDeviceId != null)
            return false;
        if (namedUserId != null ? !namedUserId.equals(that.namedUserId) : that.namedUserId != null)
            return false;
        return deliveryType != null ? deliveryType.equals(that.deliveryType) : that.deliveryType == null;
    }

    @Override
    public int hashCode() {
        int result = (optIn ? 1 : 0);
        result = 31 * result + (backgroundEnabled ? 1 : 0);
        result = 31 * result + (deviceType != null ? deviceType.hashCode() : 0);
        result = 31 * result + (pushAddress != null ? pushAddress.hashCode() : 0);
        result = 31 * result + (setTags ? 1 : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (locationSettings != null ? locationSettings.hashCode() : 0);
        result = 31 * result + (appVersion != null ? appVersion.hashCode() : 0);
        result = 31 * result + (sdkVersion != null ? sdkVersion.hashCode() : 0);
        result = 31 * result + (deviceModel != null ? deviceModel.hashCode() : 0);
        result = 31 * result + (apiVersion != null ? apiVersion.hashCode() : 0);
        result = 31 * result + (carrier != null ? carrier.hashCode() : 0);
        result = 31 * result + (accengageDeviceId != null ? accengageDeviceId.hashCode() : 0);
        result = 31 * result + (namedUserId != null ? namedUserId.hashCode() : 0);
        result = 31 * result + (deliveryType != null ? deliveryType.hashCode() : 0);
        return result;
    }

    /**
     * Creates a ChannelRegistrationPayload from JSON object
     *
     * @param value The JSON object to create the ChannelRegistrationPayload from
     * @return The payload as a ChannelRegistrationPayload
     */
    static ChannelRegistrationPayload fromJson(JsonValue value) throws JsonException {
        JsonMap jsonMap = value.optMap();
        JsonMap channelJson = jsonMap.opt(CHANNEL_KEY).optMap();
        JsonMap identityHints = jsonMap.opt(IDENTITY_HINTS_KEY).optMap();

        if (channelJson.isEmpty() && identityHints.isEmpty()) {
            throw new JsonException("Invalid channel payload: " + value);
        }

        Set<String> tags = new HashSet<>();
        for (JsonValue tag : channelJson.opt(TAGS_KEY).optList()) {
            if (tag.isString()) {
                tags.add(tag.getString());
            } else {
                throw new JsonException("Invalid tag: " + tag);
            }
        }

        Boolean locationSettings = null;
        Integer apiVersion = null;

        if (channelJson.containsKey(LOCATION_SETTINGS_KEY)) {
            locationSettings = channelJson.opt(LOCATION_SETTINGS_KEY).getBoolean(false);
        }

        if (channelJson.containsKey(API_VERSION_KEY)) {
            apiVersion = channelJson.opt(API_VERSION_KEY).getInt(-1);
        }

        String deliveryType = channelJson.opt(ANDROID_EXTRAS_KEY).optMap().opt(ANDROID_DELIVERY_TYPE).getString();

        return new Builder().setOptIn(channelJson.opt(OPT_IN_KEY).getBoolean(false))
                            .setBackgroundEnabled(channelJson.opt(BACKGROUND_ENABLED_KEY).getBoolean(false))
                            .setDeviceType(channelJson.opt(DEVICE_TYPE_KEY).getString())
                            .setPushAddress(channelJson.opt(PUSH_ADDRESS_KEY).getString())
                            .setLanguage(channelJson.opt(LANGUAGE_KEY).getString())
                            .setCountry(channelJson.opt(COUNTRY_KEY).getString())
                            .setTimezone(channelJson.opt(TIMEZONE_KEY).getString())
                            .setTags(channelJson.opt(SET_TAGS_KEY).getBoolean(false), tags)
                            .setUserId(identityHints.opt(USER_ID_KEY).getString())
                            .setAccengageDeviceId(identityHints.opt(ACCENGAGE_DEVICE_ID).getString())
                            .setLocationSettings(locationSettings)
                            .setAppVersion(channelJson.opt(APP_VERSION_KEY).getString())
                            .setSdkVersion(channelJson.opt(SDK_VERSION_KEY).getString())
                            .setDeviceModel(channelJson.opt(DEVICE_MODEL_KEY).getString())
                            .setApiVersion(apiVersion)
                            .setCarrier(channelJson.opt(CARRIER_KEY).getString())
                            .setDeliveryType(deliveryType)
                            .setNamedUserId(channelJson.opt(NAMED_USER_ID_KEY).getString())
                            .build();
    }

}
