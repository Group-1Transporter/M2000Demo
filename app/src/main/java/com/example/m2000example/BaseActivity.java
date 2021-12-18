package com.example.m2000example;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m2000example.Services.SerialService;
import com.example.m2000example.callback.QueryResponse;
import com.example.m2000example.callback.SerialListener;
import com.example.m2000example.utils.Constants;
import com.example.m2000example.utils.SerialSocket;
import com.example.m2000example.utils.TextUtil;

public class BaseActivity extends AppCompatActivity implements SerialListener {
    private static final String TAG = "BaseActivity";

    private static SerialService serialService;
    private static Constants.Connected connected = Constants.Connected.False;

    private ProgressDialog progressDialog;

    protected String deviceAddress;

    private QueryResponse queryResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (serialService != null) {
            serialService.attach(this);
        } else {
            startService(new Intent(getApplicationContext(), SerialService.class));
        }
    }

    @Override
    public void onStop() {
        if (serialService != null && !isChangingConfigurations())
            serialService.detach();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (connected != Constants.Connected.False)
            disconnect();
        stopService(new Intent(this, SerialService.class));
        super.onDestroy();
    }


    protected void GPSStatusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("EXICON Want your phone location so please turn on your location")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void connect(QueryResponse queryResponse) {
        Log.d(TAG, "connect");
        try {
            this.queryResponse = queryResponse;
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            connected = Constants.Connected.Pending;
            SerialSocket socket = new SerialSocket(getApplicationContext(), device);
            serialService.connect(socket);

            progressDialog.setMessage("Connecting...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    protected void sendRequest(String query, String progressMessage, QueryResponse queryResponse) {
        Log.d(TAG, "sendRequest: " + query);
        if (connected != Constants.Connected.True) {
            Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        this.queryResponse = queryResponse;

        progressDialog.setMessage(progressMessage);
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            byte[] data = (query + TextUtil.newline_crlf).getBytes();
            serialService.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    protected void disconnect() {
        connected = Constants.Connected.False;
        serialService.disconnect();
    }


    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        connected = Constants.Connected.True;

        progressDialog.dismiss();

        if (queryResponse != null) {
            queryResponse.onSuccess("connected");
        }
    }

    @Override
    public void onSerialConnectError(Exception e) {

        progressDialog.dismiss();
        if (queryResponse != null) {
            queryResponse.onFail(e);
        }

        disconnect();

    }

    @Override
    public void onSerialRead(byte[] data) {
        String response = new String(data);
        Log.d(TAG, "onSerialRead: " + response);
        if (queryResponse != null) {
            queryResponse.onSuccess(response);
        }
    }

    @Override
    public void onSerialIoError(Exception e) {
        Log.e(TAG, "onSerialIoError: ", e);

        if (queryResponse != null) {
            queryResponse.onFail(e);
        }

        disconnect();
    }

}