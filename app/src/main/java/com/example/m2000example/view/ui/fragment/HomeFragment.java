package com.example.m2000example.view.ui.fragment;

import android.annotation.SuppressLint;
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


import com.example.m2000example.BaseFragment;
import com.example.m2000example.callback.QueryResponse;
import com.example.m2000example.databinding.FragmentHomeBinding;
import com.example.m2000example.models.Data;
import com.example.m2000example.models.Menu;
import com.example.m2000example.models.TempData;
import com.example.m2000example.utils.Constants;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends BaseFragment implements Serializable {
    public static final String TAG = "HomeFragment";
    private ArrayList<ArrayList<Data>> dataList;
    private int dataIDLimitCount = 1;
    private List<Menu> headerList = new ArrayList<>();
    private HashMap<Menu, List<Menu>> childList = new HashMap<>();
    ArrayList<String> responceList = new ArrayList<>();
    private Menu menu;
    int count = 1;
    int position = 0;
    int increaser = 4;
    private int querySendRequestTime = 10;
    ArrayList<TempData> tempData = new ArrayList<>();
    ArrayList<String> responceTemp = new ArrayList<>();

    ArrayList<String> idsCollection = new ArrayList<>();


    private FragmentHomeBinding binding;


    private HomeFragment() {
    }

    public static HomeFragment newInstance(int containerID,String deviceAddress) {

        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.Data.CONTAINER_ID, containerID);
        args.putString(Constants.Data.DEVICE_ADDRESS, deviceAddress);
        fragment.setArguments(args);
        return fragment;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            containerID = getArguments().getInt(Constants.Data.CONTAINER_ID);
            deviceAddress = (String) getArguments().getSerializable("DEVICE_ADDRESS");
        }
        fragmentManager = requireActivity().getSupportFragmentManager();
        loadItemFile();
        heraricalData();
        Log.d(TAG, "onCreate: "+menu);
        Log.d(TAG, "onCreate: "+childList);
        Log.d(TAG, "onCreate: "+dataList);
        Log.d(TAG, "onCreate: "+headerList);
        Log.d(TAG, "onCreate: "+connected+deviceAddress);
        for(int i = 0; i<dataList.size();i+=increaser){
            String temp = "";
            for(int j = 0;j<increaser;j++){
                try{
                    temp += String.valueOf(dataList.get(i+j).get(0).getId())+",";
                }
                catch (Exception e){
                    i = dataList.size();
                }

            }
            temp = temp.substring(0, temp.length() - 1);
            idsCollection.add(temp);
        }

    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("Home:-", "onCreateView");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        sendRequest("type=3&itms=1", new QueryResponse() {
//            @Override
//            public void onSuccess(String response) {
//                Log.d(TAG, "onSuccess: "+response);
//            }
//
//            @Override
//            public void onFail(Throwable throwable) {
//
//            }
//        });

        return binding.getRoot();
    }



    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getActivity(), "HomeCreated", Toast.LENGTH_SHORT).show();
        String temp = idsCollection.get(position);
        Log.d(TAG, "onCreate: "+temp);
        Log.d(TAG, "onStart: "+connected+deviceAddress);

        String sendQury = "type=3&itms=" + temp;
//        binding.dataShowTv.append(sendQury);
//        binding.dataShowTv.append("\n");


        StringBuilder dataCollecter = new StringBuilder();
        int i =0;
        Log.d(TAG, "onStart: "+connected);
        while(i<headerList.size()){
            String idParent = String.valueOf(headerList.get(i).id);
            sendRequest("type=3&itms="+idParent, new QueryResponse() {
                @Override
                public void onSuccess(String response) {
                    responceList.add(response);
                }

                @Override
                public void onFail(Throwable throwable) {

                }
            });

            int j =0;
            while(j<headerList.get(i).childMenus.size()){

                String id = String.valueOf(headerList.get(i).childMenus.get(j).id);

                sendRequest("type=3&itms="+id, new QueryResponse() {
                    @Override
                    public void onSuccess(String response) {
                        responceList.add(response);
                    }

                    @Override
                    public void onFail(Throwable throwable) {

                    }
                });
                Log.d(TAG, "onStart: "+connected);


                j++;
            }
            i++;
        }



        /*sendRequest(sendQury, new QueryResponse() {

            @Override
            public void onSuccess(String response) {

                    Log.d(TAG, "onSuccess: "+response);
                    Log.d(TAG, "onSuccess: "+connected+deviceAddress);

                    dataCollecter.append(response);
                  
//                    if (response.contains("\r\n")){
                        getActivity().runOnUiThread(() -> {

                            binding.dataShowTv.append(dataCollecter);
                            Log.d(TAG, "onSuccess: "+connected);
                            responceTemp.add("Ids"+sendQury+"             Responce"+dataCollecter);

                            binding.dataShowTv.append("\n--------------");
                        });
                        Log.d(TAG, "onSuccess: "+response);
                        new Handler().postDelayed(() -> getActivity().runOnUiThread(() ->sendMethodCaller(position) ), querySendRequestTime);

                    //}



            }

            @Override
            public void onFail(Throwable throwable) {

            }
        });*/

        Log.d(TAG, "onStartResponceList: "+responceList);
    }



    private void sendCaller(String id){
        sendRequest("type=3&itms="+id, new QueryResponse() {
            @Override
            public void onSuccess(String response) {
                responceList.add(response);
            }

            @Override
            public void onFail(Throwable throwable) {

            }
        });

    }

    private void sendMethodCaller(int Posotion){
        if(deviceAddress==null){
            deviceAddress = getArguments().getSerializable("DEVICE_ADDRESS").toString();
        }
        String ids = "";
        try{
             ids = idsCollection.get(Posotion);
             position = Posotion;
        }
        catch (Exception e){

        }
        String sendQury = "type=3&itms=" + ids;
        binding.dataShowTv.append(sendQury);
        binding.dataShowTv.append("\n");
        StringBuilder dataCollecter = new StringBuilder();
        sendRequest(sendQury, new QueryResponse() {
            @Override
            public void onSuccess(String response) {

                    dataCollecter.append(response);
                    Log.d(TAG, "onSuccess: "+response);

                    if (response.contains("\r\n")){
                        position++;
                        getActivity().runOnUiThread(() -> {

                            binding.dataShowTv.append(dataCollecter);
                            Log.d(TAG, "onSuccess: "+connected);
                            responceTemp.add("Ids"+sendQury+"             Responce"+dataCollecter);
                            binding.dataShowTv.append("\n----------------------------"+"\n");
                        });
                        Log.d(TAG, "onSuccess: "+position);
//                        if (position==483||position==958){
//                            new Handler().postDelayed(()->getActivity().runOnUiThread(()->{
//                                Toast.makeText(getActivity(), "Ruhil", Toast.LENGTH_SHORT).show();
//                                new Handler().postDelayed(()->getActivity().runOnUiThread(()->sendMethodCaller(position)),querySendRequestTime);
//                            }),20000);
//                        }
//                        else{
//                            new Handler().postDelayed(()->getActivity().runOnUiThread(()->sendMethodCaller(position)),querySendRequestTime);
//                        }
                        new Handler().postDelayed(()->getActivity().runOnUiThread(()->sendMethodCaller(position)),querySendRequestTime);


                    }


            }

            @Override
            public void onFail(Throwable throwable) {

            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadItemFile() {


        try {
            progressDialog.setMessage("Loading Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            InputStream inputStream = getActivity().getAssets().open("item.png");
            byte[] itemByte = IOUtils.toByteArray(inputStream);

            dataList = new ArrayList<>();
            ArrayList<Data> tempDataList = new ArrayList<>();

            for (int i = 0; i < itemByte.length; i = i + 3) {
                Data data = new Data();

                int msb = Byte.toUnsignedInt(itemByte[i + 1]);
                int lsb = Byte.toUnsignedInt(itemByte[i + 2]);

                int result = lsb + (msb << 8);

                data.setId(result);

                String flags = Integer.toBinaryString(Byte.toUnsignedInt(itemByte[i]));

                while (flags.length() < 8) {
                    flags = '0' + flags;
                }

                data.setFirstLevelFlags(flags);
                //data.setTitle();

                tempDataList.add(data);

                if (tempDataList.size() == dataIDLimitCount) {
                    dataList.add(tempDataList);
                    tempDataList = new ArrayList<>();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    private void heraricalData(){
        for (Integer i = 0; i < dataList.size(); i++) {
            char[] ch = dataList.get(i).get(0).getFirstLevelFlags().toCharArray();

            boolean isHidden = ch[3] == '1';

            if (menu == null) {
                menu = new Menu(dataList.get(i).get(0).getId(), null, true, true, null);
                menu.parentMenu = menu;
            } else {
                ch = dataList.get(i).get(0).getFirstLevelFlags().toCharArray();
                Menu childMenu = new Menu(dataList.get(i).get(0).getId(), null, true, true, menu);
                menu.childMenus.add(
                        childMenu
                );

                if (ch[0] == '1') {
                    i = getChild(i + 1, childMenu);
                    Log.d(TAG, "prepareMenuData: " + i);
                }
            }

        }


        for (int i = 0; i < menu.childMenus.size(); i++) {
            headerList.add(menu.childMenus.get(i));

            menu.childMenus.get(i).childMenus.removeIf(childMenu -> childMenu.isHidden);
            childList.put(menu.childMenus.get(i), menu.childMenus.get(i).childMenus);
        }
        /*for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }*/
        for(Map.Entry<Menu, List<Menu>> entry : childList.entrySet()){

        }


    }

    private int getChild(Integer i, Menu childMenu) {

        for (; i < dataList.size(); i++) {

            char[] ch = dataList.get(i).get(0).getFirstLevelFlags().toCharArray();
            Menu menu = new Menu(dataList.get(i).get(0).getId(), null, ch[3] == '1', ch[0] == '1', childMenu);

            childMenu.childMenus.add(
                    menu
            );

            Log.d(TAG, childMenu.id + ":" + childMenu.menuName + ">>>" + menu.id + ":" + menu.menuName);


            if (ch[0] == '1') {

                i = getChild(i + 1, menu);
            }

            if (ch[6] == '1') {
                Log.d(TAG, "<==============================>");
                break;
            }
        }

        return i;
    }


}
