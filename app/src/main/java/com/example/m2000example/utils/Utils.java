package com.example.m2000example.utils;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class Utils {
    private static final String TAG = "Utils";

    /**
     * sort by name, then address. sort named devices first
     */
    public static int compareTo(BluetoothDevice a, BluetoothDevice b) {
        Log.e(TAG, "CompareTo");
        boolean aValid = a.getName() != null && !a.getName().isEmpty();
        boolean bValid = b.getName() != null && !b.getName().isEmpty();
        if (aValid && bValid) {
            int ret = a.getName().compareTo(b.getName());
            if (ret != 0) return ret;
            return a.getAddress().compareTo(b.getAddress());
        }
        if (aValid) return -1;
        if (bValid) return +1;
        return a.getAddress().compareTo(b.getAddress());
    }
}
