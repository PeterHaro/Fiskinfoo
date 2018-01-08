/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fiskinfoo.no.sintef.fiskinfoo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;


public class FiskInfo extends Application {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final boolean GA_IS_DRY_RUN = false;
    private static final String GLOBAL_PROPERTY_ID = "UA-111399706-1"; //SINTEFsit@gmail.com Apps::MyCyFapp NOT sintef ICT including digitalization interface
    private static final String TRACKING_PREF_KEY = "trackingPreference";
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app. eg: 'specific client' tracking
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: 'roll-up' tracking.
    }

    private Tracker tracker;
    //__END_ANALYTICS

    @Override
    public void onCreate() {
        super.onCreate();
        initializeGoogleAnalytics();
    }


    private void initializeGoogleAnalytics() {
        GoogleAnalytics.getInstance(this).setDryRun(GA_IS_DRY_RUN);
        // Set the log level to verbose if dryRun.
        // DEFAULT is set to DRY RUN (only logging will happen)
//        GoogleAnalytics.getInstance(this).getLogger()
//                .setLogLevel(GA_IS_DRY_RUN || BuildConfig.DEBUG ?
//                        Logger.LogLevel.VERBOSE : Logger.LogLevel.WARNING);

        // Set the opt out flag when user updates a tracking preference.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
                        if (key.equals(TRACKING_PREF_KEY)) {
                            GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(
                                    pref.getBoolean(key, false));
                        }
                    }
                });
        getDefaultTracker();
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        String CLIENT_PROPERTY_ID = "MyCyFapp";
        // Debug builds use testing id to prevent compromising real report data.
        // You could have the same logic for the global id as well.
        if (BuildConfig.DEBUG) CLIENT_PROPERTY_ID = null;
        boolean hasClientId = (CLIENT_PROPERTY_ID != null && (!CLIENT_PROPERTY_ID.equals("")));

        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) && hasClientId ?
                    analytics.newTracker(CLIENT_PROPERTY_ID) :
                    analytics.newTracker(GLOBAL_PROPERTY_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public Boolean sendAnalyticsEvent(TrackerName trackerName, String[] subsequentParams,
                                      HitBuilders.EventBuilder event) {

        // Get tracker
        Tracker tracker = getTracker(trackerName);
        if (tracker == null) {
            return false;
        }

        if (subsequentParams != null) {
            if (subsequentParams.length >= 1 && subsequentParams[0] != null) {
                tracker.setScreenName(subsequentParams[0]);
            }
            if (subsequentParams.length >= 2 && subsequentParams[1] != null) {
                tracker.setTitle(subsequentParams[1]);
            }
        }

        tracker.send(event.build());
        tracker.setScreenName(null);
        tracker.setTitle(null);

        return true;
    }

    public Boolean sendBothAnalyticsTiming(HitBuilders.TimingBuilder event) {
        sendAnalyticsTiming(TrackerName.GLOBAL_TRACKER, event);
        sendAnalyticsTiming(TrackerName.APP_TRACKER, event);
        return true;
    }

    public Boolean sendAnalyticsTiming(TrackerName trackerName, HitBuilders.TimingBuilder event) {
        Tracker tracker = getTracker(trackerName);
        if (tracker == null) {
            return false;
        }
        tracker.send(event.build());
        return true;
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker); // DO NOT ADD THIS FILE! It is autogenerated from the json confiugration file.
            tracker.enableAutoActivityTracking(true);
            tracker.enableExceptionReporting(true);
        }
        return tracker;
    }
}
