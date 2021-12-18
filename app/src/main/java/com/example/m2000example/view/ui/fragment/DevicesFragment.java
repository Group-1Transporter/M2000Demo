package com.example.m2000example.view.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.m2000example.BaseFragment;
import com.example.m2000example.R;
import com.example.m2000example.callback.QueryResponse;
import com.example.m2000example.databinding.FragmentDevicesBinding;
import com.example.m2000example.utils.Constants;
import com.example.m2000example.utils.TextUtil;
import com.example.m2000example.utils.Utils;
import com.example.m2000example.view.adapter.DeviceAdapter;
import com.example.m2000example.view.adapter.callback.ItemClick;

import java.util.ArrayList;
import java.util.Collections;

/*
 * show list of BLE devices
 */

public class DevicesFragment extends BaseFragment {
    public static final String TAG = "DevicesFragment";

    private FragmentDevicesBinding binding;
    //private FragementBatteryStatusBinding binding;

    private Constants.ScanState scanState = Constants.ScanState.NONE;
    private static final long LE_SCAN_PERIOD = 10000;
    private final Handler leScanStopHandler = new Handler();
    private final BluetoothAdapter.LeScanCallback leScanCallback;
    private final BroadcastReceiver discoveryBroadcastReceiver;
    private final IntentFilter discoveryIntentFilter;

    private Menu menu;
    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<BluetoothDevice> listItems = new ArrayList<>();

    private DeviceAdapter adapter;


    private DevicesFragment() {
        leScanCallback = (device, rssi, scanRecord) -> {
            if (device != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateScan(device);
                });
            }

        };
        discoveryBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_CLASSIC && getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateScan(device));
                    }
                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                    scanState = Constants.ScanState.DISCOVERY_FINISHED; // don't cancel again
                    stopScan();
                }
            }
        };
        discoveryIntentFilter = new IntentFilter();
        discoveryIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    }

    public static DevicesFragment newInstance(int containerID) {

        DevicesFragment fragment = new DevicesFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Data.CONTAINER_ID, containerID);
      //  args.putString(Constants.Data.DEVICE_ADDRESS, deviceAddress);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            containerID = getArguments().getInt(Constants.Data.CONTAINER_ID);
        }
        fragmentManager = requireActivity().getSupportFragmentManager();

        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        adapter = new DeviceAdapter(requireContext(), listItems, bluetoothDeviceItemClick);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //binding = FragementBatteryStatusBinding.inflate(inflater, container, false);
        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textViewMessage.setText("initializing...");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(binding.recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        binding.recyclerView.setHasFixedSize(true);

        binding.recyclerView.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
       // Log.e(TAG, "onResume");
        getActivity().registerReceiver(discoveryBroadcastReceiver, discoveryIntentFilter);
        if (bluetoothAdapter == null) {
            binding.textViewMessage.setText("Bluetooth not supported");
        } else if (!bluetoothAdapter.isEnabled()) {
            binding.textViewMessage.setText("Bluetooth is disabled");
            if (menu != null) {
                listItems.clear();
                adapter.notifyDataSetChanged();
                menu.findItem(R.id.ble_scan).setEnabled(false);
            }
        } else {
            binding.textViewMessage.setText("Use SCAN to refresh devices");
            if (menu != null)
                menu.findItem(R.id.ble_scan).setEnabled(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_devices, menu);
        this.menu = menu;
        Log.e(TAG, "onCrateOptionMenu");
        if (bluetoothAdapter == null) {
            menu.findItem(R.id.bt_settings).setEnabled(false);
            menu.findItem(R.id.ble_scan).setEnabled(false);
        } else if (!bluetoothAdapter.isEnabled()) {
            menu.findItem(R.id.ble_scan).setEnabled(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // Log.e(TAG, "onOmptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.ble_scan) {
            startScan();
            return true;
        } else if (id == R.id.ble_scan_stop) {
            stopScan();
            return true;
        } else if (id == R.id.bt_settings) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
       // Log.e(TAG, "onPause");
        stopScan();
        getActivity().unregisterReceiver(discoveryBroadcastReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // Log.e(TAG, "onDestroyView");
        menu = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ignore requestCode as there is only one in this fragment
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       // Log.e(TAG, "onRequestPermissionResult");
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new Handler(Looper.getMainLooper()).postDelayed(this::startScan, 1); // run after onResume to avoid wrong empty-text
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getText(R.string.location_denied_title));
            builder.setMessage(getText(R.string.location_denied_message));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
    }


    ItemClick<BluetoothDevice> bluetoothDeviceItemClick = (position, bluetoothDevice) -> {
        //Log.e(TAG, "onListItemClick");
        stopScan();
        deviceAddress = bluetoothDevice.getAddress();
        connect(new QueryResponse() {
            @Override
            public void onSuccess(String response) {
                initiateLoginFragment(containerID, deviceAddress);
                Log.d(TAG, "onSuccess: "+response);

            }

            @Override
            public void onFail(Throwable throwable) {
                //todo
            }
        });
    };

    @SuppressLint("StaticFieldLeak") // AsyncTask needs reference to this fragment
    private void startScan() {
        //Log.e(TAG, "startScan");
        if (scanState != Constants.ScanState.NONE)
            return;
        scanState = Constants.ScanState.LE_SCAN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                scanState = Constants.ScanState.NONE;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.location_permission_title);
                builder.setMessage(R.string.location_permission_message);
                builder.setPositiveButton(android.R.string.ok,
                        (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0));
                builder.show();
                return;
            }
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = false;
            try {
                locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ignored) {
            }
            try {
                locationEnabled |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ignored) {
            }
            if (!locationEnabled)
                scanState = Constants.ScanState.DISCOVERY;
            // Starting with Android 6.0 a bluetooth scan requires ACCESS_COARSE_LOCATION permission, but that's not all!
            // LESCAN also needs enabled 'location services', whereas DISCOVERY works without.
            // Most users think of GPS as 'location service', but it includes more, as we see here.
            // Instead of asking the user to enable something they consider unrelated,
            // we fall back to the older API that scans for bluetooth classic _and_ LE
            // sometimes the older API returns less results or slower
        }
        listItems.clear();
        adapter.notifyDataSetChanged();

        binding.textViewMessage.setText("Scanning...");
        binding.textViewMessage.setVisibility(View.VISIBLE);

        menu.findItem(R.id.ble_scan).setVisible(false);
        menu.findItem(R.id.ble_scan_stop).setVisible(true);
        if (scanState == Constants.ScanState.LE_SCAN) {
            leScanStopHandler.postDelayed(this::stopScan, LE_SCAN_PERIOD);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void[] params) {
                    bluetoothAdapter.startLeScan(null, leScanCallback);
                    return null;
                }
            }.execute(); // start async to prevent blocking UI, because startLeScan sometimes take some seconds
        } else {
            bluetoothAdapter.startDiscovery();
        }
    }

    private void updateScan(BluetoothDevice device) {

        //Log.e(TAG, "updateScan");
        if (scanState == Constants.ScanState.NONE)
            return;
        if (!listItems.contains(device)) {
            listItems.add(device);
            Collections.sort(listItems, Utils::compareTo);
            adapter.notifyDataSetChanged();
        }
    }

    private void stopScan() {
        //Log.e(TAG, "stopScan");
        if (scanState == Constants.ScanState.NONE)
            return;
        binding.textViewMessage.setText("No bluetooth devices found");
        binding.textViewMessage.setVisibility(View.GONE);
        try{
            binding.textViewMessage.setText("No bluetooth devices found");
            new Handler().postDelayed(() -> {
                    getActivity().runOnUiThread(() -> {

                            binding.textViewMessage.setVisibility(View.GONE);
                    });

            }, 10);
        }
        catch (Exception e){

        }
        if (menu != null) {
            menu.findItem(R.id.ble_scan).setVisible(true);
            menu.findItem(R.id.ble_scan_stop).setVisible(false);
        }
        switch (scanState) {
            case LE_SCAN:
                leScanStopHandler.removeCallbacks(this::stopScan);
                bluetoothAdapter.stopLeScan(leScanCallback);
                break;
            case DISCOVERY:
                bluetoothAdapter.cancelDiscovery();
                break;
            default:
                // already canceled
        }
        scanState = Constants.ScanState.NONE;

    }

    private void receive(byte[] data) {
        String msg = new String(data);
        // don't show CR as ^M if directly before LF
        msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);

        Log.d(TAG, "receive: " + msg);

    }

    private void initiateLoginFragment(int containerID, String deviceAddress) {
        Log.d(TAG, "RuhilinitiateLoginFragment"+connected+deviceAddress);
        LoginFragment fragment = LoginFragment.newInstance(containerID, deviceAddress);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerID, fragment, LoginFragment.TAG);
        fragmentTransaction.commit();

    }

}
