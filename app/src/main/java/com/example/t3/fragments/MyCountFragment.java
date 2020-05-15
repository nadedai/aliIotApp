package com.example.t3.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.example.t3.ActivityManager;
import com.example.t3.R;
import com.example.t3.StartActivity;

public class MyCountFragment extends Fragment {

    private View exitView;
    private TextView usernameView;

    public MyCountFragment() {
        // Required empty public constructor
    }

    public static MyCountFragment newInstance() {
        return new MyCountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myaccounttab_fragment_layout, container, false);
        exitView = view.findViewById(R.id.exit);
        usernameView = view.findViewById(R.id.my_username_textview);
        initExit();
        initUserInfo();
        return view;
    }

    private void initUserInfo(){
        if (LoginBusiness.isLogin()) {
            UserInfo userInfo = LoginBusiness.getUserInfo();
            String userName = "";
            if (userInfo != null) {
                if (!TextUtils.isEmpty(userInfo.userNick) && !"null".equalsIgnoreCase(userInfo.userNick)) {
                    userName = userInfo.userNick;
                } else if (!TextUtils.isEmpty(userInfo.userPhone)) {
                    userName = userInfo.userPhone;
                } else {
                    userName = "未获取到用户名";
                }
            }
            usernameView.setText(userName);
        }
    }

    private void initExit(){
        exitView.setOnClickListener(v ->{
            LoginBusiness.logout(new ILogoutCallback() {
                @Override
                public void onLogoutSuccess() {
                    Toast.makeText(getContext(),"登出成功",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getContext(), StartActivity.class);
                    ActivityManager.getInstance().exit();
                    startActivity(intent);
                }

                @Override
                public void onLogoutFailed(int code, String error) {
                    Toast.makeText(getContext(),"登出失败",Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
