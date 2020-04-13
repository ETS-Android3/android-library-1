package com.urbanairship.messagecenter;

import android.content.Context;

import com.urbanairship.PreferenceDataStore;
import com.urbanairship.channel.AirshipChannel;
import com.urbanairship.modules.Module;
import com.urbanairship.modules.messagecenter.MessageCenterModuleFactory;
import com.urbanairship.push.PushManager;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

/**
 * Message Center module loader factory implementation.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class MessageCenterModuleFactoryImpl implements MessageCenterModuleFactory {

    @Override
    public Module build(@NonNull Context context, @NonNull PreferenceDataStore dataStore, @NonNull AirshipChannel airshipChannel, @NonNull PushManager pushManager) {
        MessageCenter messageCenter = new MessageCenter(context, dataStore, airshipChannel, pushManager);
        return Module.singleComponent(messageCenter, R.xml.ua_message_center_actions);
    }

}