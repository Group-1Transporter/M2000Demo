package com.example.m2000example.utils;


import com.example.m2000example.BuildConfig;

public class Constants {

    // values have to be globally unique
    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    public static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    public static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity";

    // values have to be unique within each app
    public static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;

    public enum QueueType {Connect, ConnectError, Read, IoError}

    public enum Connected { False, Pending, True }

    public enum ScanState {NONE, LE_SCAN, DISCOVERY, DISCOVERY_FINISHED}


    private Constants() {}

    public class Data {
        public static final String CONTAINER_ID = "CONTAINER_ID";
        public static final String DEVICE_ADDRESS = "DEVICE_ADDRESS";
    }
}
