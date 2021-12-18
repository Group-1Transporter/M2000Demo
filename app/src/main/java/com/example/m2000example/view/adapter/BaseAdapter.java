package com.example.m2000example.view.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.example.m2000example.view.adapter.viewholder.BaseViewHolder;


public abstract class BaseAdapter<T extends BaseViewHolder> extends RecyclerView.Adapter<T> {
    private static final String TAG = "BaseAdapter";

    private Context context;

    public BaseAdapter(Context context) {
        this.context = context;
    }

}