package com.example.m2000example;

import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.m2000example.databinding.ActivityMainBinding;
import com.example.m2000example.view.ui.fragment.DevicesFragment;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class MainActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener  {

    private ActivityMainBinding binding;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSPP bluetoothSPP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //getActionBar().hide();
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        statusCheck();


        getSupportFragmentManager().addOnBackStackChangedListener(this);
        bluetoothSPP = new BluetoothSPP(MainActivity.this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (savedInstanceState == null) {
            initiateDevicesFragment();
        } else {
            onBackStackChanged();
        }
    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("please turn on your location")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void initiateDevicesFragment() {

        DevicesFragment fragment = DevicesFragment.newInstance(binding.fragment.getId());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(binding.fragment.getId(), fragment, DevicesFragment.TAG);
        fragmentTransaction.commit();

    }

}