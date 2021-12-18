package com.example.m2000example;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.m2000example.Services.SerialService;
import com.example.m2000example.callback.QueryResponse;
import com.example.m2000example.callback.SerialListener;
import com.example.m2000example.utils.Constants;
import com.example.m2000example.utils.SerialSocket;
import com.example.m2000example.utils.TextUtil;
import com.example.m2000example.view.ui.fragment.DevicesFragment;


public class BaseFragment extends Fragment implements SerialListener, ServiceConnection {
    private static final String TAG = "BaseFragment";
    private StringBuffer stringBufferResponse = new StringBuffer();
    private static SerialService serialService;
    protected static Constants.Connected connected = Constants.Connected.False;

    private QueryResponse queryResponse;


    protected FragmentManager fragmentManager;
    protected int containerID;

    protected String deviceAddress;
    protected ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: "+connected);
        progressDialog = new ProgressDialog(getContext());


    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        if (serialService != null)
            serialService.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("TERMINAL", "onAttach");
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        try {
            getActivity().unbindService(this);
        } catch (Exception ignored) {
        }
        super.onDetach();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.e("TERMINAL", "onDestroy");
        if (connected != Constants.Connected.False)
            disconnect();
        //service stoped
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {

        Log.d(TAG, "onServiceConnected");
        serialService = ((SerialService.SerialBinder) binder).getService();
        serialService.attach(this);
        getActivity().runOnUiThread(() -> connect(queryResponse));
    }

    protected void connect(QueryResponse queryResponse) {
        if (connected != Constants.Connected.True){
            Log.d(TAG, "connect");

            try {
                this.queryResponse = queryResponse;
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
                connected = Constants.Connected.Pending;
                SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
                serialService.connect(socket);

                progressDialog.setMessage("Connecting...");
                progressDialog.setCancelable(false);
                progressDialog.show();

            } catch (Exception e) {
                onSerialConnectError(e);
            }

        }

    }

    protected void sendRequest(String query, QueryResponse queryResponse) {
        Log.d(TAG, "sendRequest: " + query);
        if (connected != Constants.Connected.True) {
            //onSerialConnect();
            // initiateDeviceFragment(containerID);
            //Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return ;
        }
        this.queryResponse = queryResponse;

        try {
            byte[] data = (query + TextUtil.newline_crlf).getBytes();
            Log.d(TAG, "sendRequest: "+data.toString());
            serialService.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
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

        //disconnect();

    }

//    @Override
//    public void onSerialRead(byte[] data) {
//        String response = new String(data);
//        Log.d(TAG, "onSerialRead: " + response);
//
//        if (queryResponse != null) {
//            queryResponse.onSuccess(response);
//        }
//
//    }


//        @Override
//        public void onSerialRead(byte[] data) {
//            String response = new String(data);
//            Log.d(TAG, "onSerialRead: " + response);
//
//            if (queryResponse != null) {
//                if(response.contains(";\r\n")||response.contains("1;6\r\n")){
//                    stringBufferResponse.append(response);
//                    String value = new String(stringBufferResponse.toString());
//                    stringBufferResponse=new StringBuffer();
//                    queryResponse.onSuccess(value);
//                }
//                else{
//                    stringBufferResponse.append(response);
//                }
//
//            }
//
//        }

            @Override
        public void onSerialRead(byte[] data) {
            String response = new String(data);
            Log.d(TAG, "onSerialRead: " + response);

            if (queryResponse != null) {
                if(response.contains(";\r\n")||response.contains("\r\n")){
                    stringBufferResponse.append(response);
                    String value = new String(stringBufferResponse.toString());
                    stringBufferResponse=new StringBuffer();
                    queryResponse.onSuccess(value);
                }
                else{
                    stringBufferResponse.append(response);
                }

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

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected: ");
        disconnect();
    }

    public void disconnect() {
        connected = Constants.Connected.False;
        serialService.disconnect();
    }



}
