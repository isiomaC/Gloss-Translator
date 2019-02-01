package com.chuck.artranslate.utils;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;

public abstract class MyBroadcastReceiver extends BroadcastReceiver {

    private String mBroadcastId;
    private String[] mBroadcastIds;
    private boolean registered = false;

    public MyBroadcastReceiver(String broadcastId) {
        super();
        mBroadcastId = broadcastId;
    }

    public MyBroadcastReceiver(String[] broadcastIds) {
        super();
        mBroadcastIds = broadcastIds;
    }

    private void register() {

        // This registers mMessageReceiver to receive messages.
        if (!registered) {

            IntentFilter filter = new IntentFilter();

            if (mBroadcastId != null) {
                filter.addAction(mBroadcastId);
            } else if (mBroadcastIds != null) {
                for (String broadcastId : mBroadcastIds) {
                    filter.addAction(broadcastId);
                }
            }

            LocalBroadcastManager.getInstance(App.getInstance().getApplicationContext()).registerReceiver(this, filter);

            registered = true;

        }
    }

    private void unregister() {
        // Unregister since the activity is not visible
        if (registered) {
            LocalBroadcastManager.getInstance(App.getInstance().getApplicationContext())
                    .unregisterReceiver(this);
            registered = false;
        }
    }

    public static void sendBroadcast(@NonNull String broadcastId, @Nullable Bundle message) {
        Intent intent = new Intent(broadcastId);
        if (message != null) {
            intent.putExtras(message);
        }
        send(intent);
    }

    public static void sendBroadcast(@NonNull String broadcastId, @NonNull String key, @NonNull String message) {
        Intent intent = new Intent(broadcastId);
        intent.putExtra(key, message);
        send(intent);
    }

    public static void sendBroadcast(@NonNull String broadcastId, @NonNull String key, int message) {
        Intent intent = new Intent(broadcastId);
        intent.putExtra(key, message);
        send(intent);
    }
    public static void sendBroadcast(@NonNull String broadcastId, @NonNull String key, Serializable message) {
        Intent intent = new Intent(broadcastId);
        intent.putExtra(key, message);
        send(intent);
    }

    public static void sendBroadcast(@NonNull String broadcastId) {
        send(new Intent(broadcastId));
    }

    private static void send(Intent intent) {
        LocalBroadcastManager.getInstance(
                App.getInstance().getApplicationContext())
                .sendBroadcast(intent);
    }

    public static void register(MyBroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver != null) {
            broadcastReceiver.register();
        }
    }

    public static void unregister(MyBroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver != null) {
            broadcastReceiver.unregister();
        }
    }
}
