package com.example.m2000example.view.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;


import com.example.m2000example.databinding.DeviceListRowBinding;
import com.example.m2000example.view.adapter.BaseAdapter;
import com.example.m2000example.view.adapter.callback.ItemClick;
import com.example.m2000example.view.adapter.viewholder.DeviceViewHolder;


import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeviceAdapter extends BaseAdapter<DeviceViewHolder> {

    private Context context;
    private List<BluetoothDevice> devices;
    private ItemClick<BluetoothDevice> deviceItemClick;

    public DeviceAdapter(Context context, List<BluetoothDevice> devices, ItemClick<BluetoothDevice> deviceItemClick) {
        super(context);
        this.context = context;
        this.devices = devices;
        this.deviceItemClick = deviceItemClick;
    }

    @NotNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int viewType) {
        return new DeviceViewHolder(DeviceListRowBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DeviceViewHolder holder, int position) {
        BluetoothDevice Device = devices.get(position);
        holder.getBinding().textViewTitle.setText(Device.getName());
        holder.getBinding().textViewMacAddress.setText(Device.getAddress());

        holder.getBinding().getRoot().setOnClickListener(v -> {
            deviceItemClick.onClick(position, Device);
        });

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

}
