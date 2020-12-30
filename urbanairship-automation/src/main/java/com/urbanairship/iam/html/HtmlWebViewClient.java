/* Copyright Airship and Contributors */

package com.urbanairship.iam.html;

import android.net.Uri;
import android.webkit.WebView;

import com.urbanairship.Logger;
import com.urbanairship.iam.InAppMessage;
import com.urbanairship.javascript.JavaScriptEnvironment;
import com.urbanairship.javascript.NativeBridge;
import com.urbanairship.json.JsonException;
import com.urbanairship.json.JsonMap;
import com.urbanairship.json.JsonValue;
import com.urbanairship.webkit.AirshipWebViewClient;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

/**
 * A version of the {@link AirshipWebViewClient} for HTML in-app messages, which adds a command
 * for dismissing the message with resolution info represented as URL-encoded JSON.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public abstract class HtmlWebViewClient extends AirshipWebViewClient {

    /**
     * Close command to handle close method in the Javascript Interface.
     */
    private static final String DISMISS_COMMAND = "dismiss";

    private final InAppMessage inAppMessage;

    /**
     * Default constructor.
     */
    public HtmlWebViewClient(@NonNull InAppMessage message) {
        super();
        this.inAppMessage = message;
    }

    @VisibleForTesting
    protected HtmlWebViewClient(@NonNull NativeBridge nativeBridge, @NonNull InAppMessage message) {
        super(nativeBridge);
        this.inAppMessage = message;
    }

    /**
     * Called when the dismiss command is invoked from the native bridge. Override to
     * customize the handling of this event.
     *
     * @param argument The argument data passed in the dismiss call.
     */
    public abstract void onMessageDismissed(@NonNull JsonValue argument);

    @Override
    protected void onAirshipCommand(@NonNull WebView webView, @NonNull String command, @NonNull Uri uri) {
        if (!command.equals(DISMISS_COMMAND)) {
            return;
        }

        String path = uri.getEncodedPath();
        if (path != null) {
            String[] components = path.split("/");
            if (components.length > 1) {
                try {
                    JsonValue value = JsonValue.parseString(Uri.decode(components[1]));
                    onMessageDismissed(value);
                } catch (JsonException e) {
                    Logger.error("Unable to decode message resolution from JSON.", e);
                }
            } else {
                Logger.error("Unable to decode message resolution, invalid path");
            }
        } else {
            Logger.error("Unable to decode message resolution, missing path");
        }
    }

    /**
     * @hide
     */
    @CallSuper
    @NonNull
    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    protected JavaScriptEnvironment.Builder extendJavascriptEnvironment(@NonNull JavaScriptEnvironment.Builder builder, @NonNull WebView webView) {
        JsonMap extras = inAppMessage.getExtras();

        return super.extendJavascriptEnvironment(builder, webView)
                    .addGetter("getMessageExtras", extras);
    }

}
