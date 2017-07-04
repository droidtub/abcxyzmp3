package com.musicdownloader.mp3downloader;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Hanh Nguyen on 7/2/2017.
 */

public class Mp3Application extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler())
                .setNotificationReceivedHandler(new OneSignalNotificationReceivedHandler())
                .init();
    }

    class OneSignalNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey;

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

            String url = (PreferenceManager.getDefaultSharedPreferences(Mp3Application.this)).getString(getString(R.string.update_link_key), "");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    class OneSignalNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String customKey;

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
        }
    }
}
