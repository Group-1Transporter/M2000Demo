package com.example.m2000example.view.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import com.example.m2000example.BaseFragment;
import com.example.m2000example.callback.QueryResponse;
import com.example.m2000example.databinding.FragmentLoginBinding;
import com.example.m2000example.models.Data;
import com.example.m2000example.utils.Constants;
import com.example.m2000example.utils.TextUtil;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class LoginFragment extends BaseFragment {
    public static final String TAG = "LoginFragment";

    private FragmentLoginBinding binding;

    private int position = 0;
    private ArrayList<ArrayList<Data>> dataList;
    private int dataIDLimitCount = 1;
    private int querySendRequestTime = 10;

    public static LoginFragment newInstance(int containerID, String deviceAddress) {

        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Data.CONTAINER_ID, containerID);
        args.putString(Constants.Data.DEVICE_ADDRESS, deviceAddress);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();


        if (getArguments() != null) {
            containerID = getArguments().getInt(Constants.Data.CONTAINER_ID);
            deviceAddress = getArguments().getString(Constants.Data.DEVICE_ADDRESS);
        }
        fragmentManager = requireActivity().getSupportFragmentManager();

//        menuItemViewModel = new ViewModelProvider(this).get(MenuItemViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
       // ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSignIn.setOnClickListener(this::onClick);
        /*binding.buttonLogOut.setOnClickListener(this::onClick);*/

    }

    public void onClick(View v) {
        if (v.getId() == binding.buttonSignIn.getId()) {

            String userName = binding.editTextUserName.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            sendRequest("type=1&usr=" + userName + "&pwd=" + password, new QueryResponse() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "onSuccess: "+response);
                    initiateHomeFragment(containerID,deviceAddress);
                }

                @Override
                public void onFail(Throwable throwable) {
                    //todo
                }
            });


        }
    }



    private void initiateHomeFragment(int containerID,String deviceAddress) {

        HomeFragment fragment = HomeFragment.newInstance(containerID,deviceAddress);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerID, fragment, HomeFragment.TAG);
        fragmentTransaction.commit();
    }
        private void initiateDeviceFragment(int containerID) {
        DevicesFragment fragment = DevicesFragment.newInstance(containerID);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerID, fragment, DevicesFragment.TAG);
        fragmentTransaction.commit();

    }

}
