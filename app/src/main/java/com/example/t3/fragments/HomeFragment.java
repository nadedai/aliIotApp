package com.example.t3.fragments;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.alink.apiclient.utils.StringUtils;
import com.example.t3.ActivityManager;
import com.example.t3.DemoDeviceActiviy;
import com.example.t3.MyApplication;
import com.example.t3.R;
import com.example.t3.StartActivity;
import com.example.t3.controlers.MyAliosKit;
import com.example.t3.controlers.adapters.MyRVAdapter;
import com.example.t3.models.DeviceInfoBean;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FrameLayout mainMenu,popMenu;
    private RecyclerView rv;
    private MyRVAdapter myAdapter;
    private Button deleteButton,renameButton;
    private Handler mHandler;
    private int delayMillis = 500;
    private List<DeviceInfoBean> deviceList;
    private View view;
    @SuppressLint("HandlerLeak")
    public HomeFragment(){
        mHandler =new Handler(){
            //handleMessage为处理消息的方法
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                myAdapter.notifyDataSetChanged();
            }
        };
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.hometab_fragment_layout, container, false);
        mainMenu = view.findViewById(R.id.main_menu);
        popMenu = view.findViewById(R.id.pop_menu);
        renameButton = view.findViewById(R.id.rename_button);
        deleteButton = view.findViewById(R.id.delete_button);
//        mainMenu.setOnClickListener(view -> mainMenu.setVisibility(View.INVISIBLE));
        initButton();
        initAddDevice();
        initRecyclerView();
        return view;
    }

    /**
     * 初始化列表
     */
    private void initRecyclerView(){
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        myAdapter = new MyRVAdapter(getContext());
        myAdapter.setOnItemClickListener((new MyRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(getContext(), "click" + position, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getContext(), DemoDeviceActiviy.class);
                intent.putExtra("iotId", deviceList.get(position).getIotId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

                Toast.makeText(getContext(), "长按", Toast.LENGTH_SHORT).show();
                popMenu.setVisibility(View.VISIBLE);
                popMenu.bringToFront();
                popMenu.setOnClickListener(v ->{
                    popMenu.setVisibility(View.INVISIBLE);
                });

                deleteButton.setOnClickListener(v ->{
                    Toast.makeText(getContext(),"删除",Toast.LENGTH_SHORT).show();
//                    MyAliosKit.unbind();
                    MyAliosKit.reLogin(getContext());
                });

                renameButton.setOnClickListener(v ->{
                    Toast.makeText(getContext(),"修改名字",Toast.LENGTH_SHORT).show();
                    EditText editText = new EditText(Objects.requireNonNull(getActivity()));
                    new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                            .setTitle("请输入")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(editText)
                            .setPositiveButton("确定", (d,i)->{
                                String text =  editText.getText().toString();
                                if(!StringUtils.isEmptyString(text)){
                                    MyAliosKit.setDeviceNickName(text,deviceList.get(position).getIotId());
//                                    updateList();
                                    deviceList.get(position).setNickName(text);
                                    myAdapter.notifyDataSetChanged();
                            }
                            })
                            .setNegativeButton("取消", null)
                            .show();

//                    MyAliosKit.unbind();
                });
            }
        }));


        rv.setAdapter(myAdapter);
    }

    /**
     * 初始化按钮
     */
    private void initButton(){
        View mIlopMainAddBtn = view.findViewById(R.id.ilop_main_add_btn);
        mIlopMainAddBtn.setOnClickListener(v -> {
            mainMenu.setVisibility(View.VISIBLE);
            mainMenu.bringToFront();
        });
    }

    private void initAddDevice(){
        mainMenu.setOnClickListener(v -> {
            mainMenu.setVisibility(View.INVISIBLE);
        });

        view.findViewById(R.id.ilop_main_menu_add_device_btn).setOnClickListener(v ->{

            MyAliosKit.bind();
            Toast.makeText(getActivity(),"添加设备",Toast.LENGTH_SHORT).show();
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(),"button",Toast.LENGTH_SHORT).show();
//                MyAliosKit.bind();
//            }
//        });
    }



    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    /**
     * 更新设备列表
     */
    private void updateList(){
        //获取用户绑定的设备
            MyAliosKit.getListByAccount(new MyAliosKit.callBack() {
            @Override
            public void success(List<DeviceInfoBean> list) {
                if(list != null && list.size() > 0){
                    deviceList = list;
                    myAdapter.setListDevice(list);
                    mHandler.sendMessage(Message.obtain());
                }
                else{
                    updateList();
                }
            }

            @Override
            public void fail() {
                MyAliosKit.reLogin(getContext());
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
