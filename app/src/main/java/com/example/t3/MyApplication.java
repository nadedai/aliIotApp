package com.example.t3;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManage;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.aep.sdk.framework.sdk.SDKManager;

public class MyApplication extends MultiDexApplication {
    String TAG = "alios";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SDKManager.init(this);
    }

    //返回
    public static Context getContext(){
        return context;
    }
    void test(){
        IoTCredentialManage ioTCredentialManage = IoTCredentialManageImpl.getInstance(this);
        if(ioTCredentialManage!=null){
            Log.i(TAG, "test: ioTCredentialManage"+ioTCredentialManage.getIoTCredential());
        }
    }
    void token(){
        IoTCredentialManage ioTCredentialManage = IoTCredentialManageImpl.getInstance(this);
        Log.i(TAG, "rIoTCredentialManage" +ioTCredentialManage);
        if (ioTCredentialManage != null) {
            ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
                @Override
                public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
                    Log.i(TAG, "refresh IoTCredentailData success :" + ioTCredentialData.toString());
                }

                @Override
                public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {
                    Log.i(TAG, "refresh IoTCredentailData failed ");
                    if (ioTCredentialManageError != null) {
                        Log.i(TAG, "error code is:" + ioTCredentialManageError.errorCode);
                    }
                }
            });
        }
    }
}
