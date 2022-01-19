/* Copyright Airship and Contributors */

package com.urbanairship.automation;

import androidx.annotation.NonNull;

import com.urbanairship.UAirship;
import com.urbanairship.app.ActivityMonitor;
import com.urbanairship.app.ApplicationListener;
import com.urbanairship.app.SimpleApplicationListener;
import com.urbanairship.json.JsonSerializable;
import com.urbanairship.json.JsonValue;
import com.urbanairship.reactive.Function;
import com.urbanairship.reactive.Observable;
import com.urbanairship.reactive.Observer;
import com.urbanairship.reactive.Schedulers;
import com.urbanairship.reactive.Subscription;
import com.urbanairship.reactive.Supplier;
import com.urbanairship.util.VersionUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Factory methods for creating compound trigger observables
 *
 * @hide
 */
class TriggerObservables {

    /**
     * Creates a state observable that sends onNext if the app is currently foregrounded,
     * and completes.
     *
     * @param monitor An instance of ActivityMonitor.
     * @return An Observable of JsonSerializable.
     */
    public static Observable<JsonSerializable> foregrounded(@NonNull final ActivityMonitor monitor) {
        return Observable.create(new Function<Observer<JsonSerializable>, Subscription>() {
            @NonNull
            @Override
            public Subscription apply(@NonNull Observer<JsonSerializable> observer) {
                if (monitor.isAppForegrounded()) {
                    observer.onNext(JsonValue.NULL);
                }
                observer.onCompleted();
                return Subscription.empty();
            }
        }).subscribeOn(Schedulers.main());
    }

    /**
     * Creates an event observable that sends onNext when a new session begins.
     *
     * @param monitor An instance of ActivityMonitor.
     * @return An Observable of JsonSerializable.
     */
    public static Observable<JsonSerializable> newSession(@NonNull final ActivityMonitor monitor, @NonNull AutomationEngine.PausedManager pausedManager) {
        final AtomicBoolean processForegroundOnResume = new AtomicBoolean(false);
        return Observable.create(new Function<Observer<JsonSerializable>, Subscription>() {
            @NonNull
            @Override
            public Subscription apply(@NonNull final Observer<JsonSerializable> observer) {
                final ApplicationListener listener = new SimpleApplicationListener() {
                    @Override
                    public void onForeground(long time) {
                        if (pausedManager.isPaused()) {
                            processForegroundOnResume.set(true);
                        } else {
                            observer.onNext(JsonValue.NULL);
                            processForegroundOnResume.set(false);
                        }
                    }

                    @Override
                    public void onBackground(long time) {
                        super.onBackground(time);
                        processForegroundOnResume.set(false);
                    }
                };

                pausedManager.addConsumer(isPaused -> {
                    if (!isPaused && processForegroundOnResume.get()) {
                        observer.onNext(JsonValue.NULL);
                        processForegroundOnResume.set(false);
                    }
                });

                monitor.addApplicationListener(listener);

                return Subscription.create(() -> monitor.removeApplicationListener(listener));
            }
        }).subscribeOn(Schedulers.main());
    }

    /**
     * Creates a state observable that sends onNext if the app version is currently updated, and then completes.
     * <p>
     * The JSON payload contains a key value pair of the device platform (android or amazon) and
     * the current app version, e.g. <code>{"android": {"version": 123}}</code>.
     *
     * @return An Observable of JsonSerializable.
     */
    public static Observable<JsonSerializable> appVersionUpdated() {
        return Observable.defer(new Supplier<Observable<JsonSerializable>>() {
            @NonNull
            @Override
            public Observable<JsonSerializable> apply() {
                if (UAirship.shared().getApplicationMetrics().getAppVersionUpdated()) {
                    return Observable.just(VersionUtils.createVersionObject());
                } else {
                    return Observable.empty();
                }
            }
        });
    }

}
