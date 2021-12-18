package com.example.m2000example.view.adapter.viewholder;


import com.example.m2000example.databinding.DeviceListRowBinding;

public class DeviceViewHolder extends BaseViewHolder {
    private DeviceListRowBinding binding;

    public DeviceViewHolder(DeviceListRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public DeviceListRowBinding getBinding() {
        return binding;
    }
}
