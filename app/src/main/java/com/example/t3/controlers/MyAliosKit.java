package com.example.t3.controlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.example.t3.ActivityManager;
import com.example.t3.MyApplication;
import com.example.t3.StartActivity;
import com.example.t3.models.DeviceInfoBean;
import com.example.t3.models.SwitchBean;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 工具类
 * 对阿里云物联网平台操作的各种方法实现
 */
public class MyAliosKit {
    private static final String TAG = "MyAliosKit";

    //存储用户绑定的设备
//    private static List<DeviceInfoBean> list;

    public interface callBack{
        void success(List<DeviceInfoBean> list);
        void fail();
    }
    public static void reLogin(Context context){
        Intent intent=new Intent(context, StartActivity.class);
        intent.putExtra("auth error", "auth error");
        ActivityManager.getInstance().exit();
        Log.i(TAG, "reLogin: ");
        context.startActivity(intent);
    }

    /**
     * 获取用户绑定的所有设备
     */
    public static void getListByAccount(callBack c) {
        Map<String, Object> maps = new HashMap<>();
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/listBindingByAccount")
                .setScheme(Scheme.HTTPS)
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.d(TAG, "onFailure");
                if ("request auth error.".equals(e.getMessage())) {
                    c.fail();
                }
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                if (code != 200) {
                    Log.e(TAG, "onResponse.listByAccount: " + response.getMessage());
                    c.fail();
                    return;
                }
                Object data = response.getData();
                Log.i(TAG, "onResponse.listByAccount: " + response.getCode()+ data);
                if (data == null) {
                    return;
                }
                if (!(data instanceof JSONObject)) {
                    Log.e(TAG, "onResponse: josn");
                    return;
                }
                try {
                    JSONObject jsonObject = (JSONObject) data;
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    c.success(JSON.parseArray(jsonArray.toString(), DeviceInfoBean.class));
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: ",e );
                    e.printStackTrace();
                }
            }
        });
    }

    public interface Propertielistener{
        void success(SwitchBean sb);
        void fail();
    }
    /**
     * 获得物的属性
     * @param panelDevice
     */
    public static void getProperties(PanelDevice panelDevice, Propertielistener pl){

        panelDevice.getProperties(new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, Object o) {
                Log.i(TAG, "获取物属性: "+o);
                try {
                    JSONObject jsonObject = new JSONObject(o.toString());
                    SwitchBean sb = new SwitchBean();
                    JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                    JSONObject jsonObject_counter = jsonObject_data.getJSONObject("Counter");
                    sb.setCounter(jsonObject_counter.getInt("value"));
                    sb.setCounterTime(jsonObject_counter.getString("time"));
                    JSONObject jsonObject_powerSwitch = jsonObject_data.getJSONObject("PowerSwitch");
                    sb.setPowerSwitch(jsonObject_powerSwitch.getInt("value"));
                    sb.setPowerSwitchTime(jsonObject_powerSwitch.getString("time"));
                    pl.success(sb);
                } catch (JSONException e) {
                    Log.i(TAG, "onComplete: error"+e);
                    e.printStackTrace();
                }
            }
        });
    }

    public static void bind() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", "a19MWJ7YIz6");
        maps.put("deviceName", "switch");
        maps.put("token", "8CA53D79C4689788BA6D20A63E82C2AB");
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/awss/enrollee/user/bind")
                .setApiVersion("1.0.3")
                .setParams(maps)
                .setAuthType("iotAuth");

        Log.i(TAG,"执行\"/awss/subdevice/bind\"");
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.i(TAG,"失败"+e.getMessage());
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                Log.i(TAG,"回应啦 code:"+code);
                final String localizeMsg = response.getLocalizedMsg();
                if (code != 200) {
                    return;
                }
                Object data = response.getData();
                if (data == null) {
                    return;
                }
                Log.i(TAG,"data:"+data);
            }
        });
    }

    /**
     * 解绑
     */
    public static void unbind(String iotId) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/unbindAccountAndDev")
                .setApiVersion("1.0.2")
                .setParams(maps)
                .setAuthType("iotAuth");

        Log.i(TAG,"执行unbind");
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.i(TAG,"失败"+e.getMessage());
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                Log.i(TAG,"回应 code:"+code);
                final String localizeMsg = response.getLocalizedMsg();
                if (code != 200) {
                    return;
                }
                Object data = response.getData();
                if (data == null) {
                    return;
                }
                Log.i(TAG,"data:"+data);
            }
        });
    }

    /**
     * 设置设备名称
     */
    public static void setDeviceNickName(String name, String iotId) {
        Log.i(TAG,"name"+name+"iotId"+iotId);
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
        maps.put("nickName", name);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/setDeviceNickName")
                .setApiVersion("1.0.2")
                .setParams(maps)
                .setAuthType("iotAuth");

        Log.i(TAG,"执行setDeviceNickName");
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.i(TAG,"失败"+e.getMessage());
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                Log.i(TAG,"回应 code:"+code);
                if (code != 200) {
                    return;
                }
                Object data = response.getData();
                if (data == null) {
                    return;
                }
                Log.i(TAG,"data:"+data);
            }
        });
    }
    /**
     * 设置物的属性
     * @param panelDevice
     * @param iotId
     * @param value
     */
    public static void set(PanelDevice panelDevice,String iotId, int value){
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONObject jsonObjectKey = new com.alibaba.fastjson.JSONObject();
        jsonObjectKey.put("PowerSwitch",value);
        jsonObject.put("iotId",iotId);
        jsonObject.put("items",jsonObjectKey);
        panelDevice.setProperties(jsonObject.toString(), new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, Object o) {
                Log.i(TAG, "设置物属性: "+o);
            }
        });
    }
}
