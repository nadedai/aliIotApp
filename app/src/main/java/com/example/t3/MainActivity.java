package com.example.t3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String TAG = "alios";
    private MyApplication application;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        application = (MyApplication) getApplication();
        if (LoginBusiness.isLogin()) {
//            application.test();
//            listByAccount();
//            bind();
            list();
            panel();

        } else {
            login();
        }
    }

    void login(){
        Log.i(TAG,"登陆");
        LoginBusiness.login(new ILoginCallback() {
            @Override
            public void onLoginSuccess() {
                Log.i(TAG,"登录成功");
                application.token();
                application.test();
            }
            @Override
            public void onLoginFailed(int code, String error) {
                Log.i(TAG,"登录失败");
            }
        });
    }

    private void panel(){
        //MobileChannel.getInstance().bindAccount(LoginBusiness.getIoTToken());
        PanelDevice panelDevice = new PanelDevice("hFBKHvhXIAm6pY0ojEBg000100");
        panelDevice.init(this, new IPanelCallback() {
            @Override
            public void onComplete(boolean b, Object o) {
                Log.i(TAG,"物初始化完成"+o);
            }
        });

        panelDevice.getStatus(new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, Object o) {
                Log.i(TAG,"状态"+o);
            }
        });
//
        panelDevice.getProperties(new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, Object o) {
                Log.i(TAG, "获取物属性: "+o);
            }
        });
//
        panelDevice.setProperties("{\"iotId\": \"hFBKHvhXIAm6pY0ojEBg000100\",\"items\": {\"Counter\": 999}}", new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, Object o) {
                Log.i(TAG, "设置物属性: "+o);
            }
        });
    }

    private void listByAccount() {
        //.setPath("/kit/debug/ping") 测试请求
        // /cloud/thing/properties/get 获取物的属性
        // /thing/detailInfo/queryProductInfo 获取根据id 或 key 获取产品详细信息
        //  .addParam("productKey", "a1QUNOYvIkN")
        // 构建请求
        //String req = "/thing/detailInfo/queryProductInfo";
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", "a1QUNOYvIkN");
        maps.put("deviceName", "app_01_test");
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/thing/device/info/get")
                .setApiVersion("1.1.2")
                .setScheme(Scheme.HTTPS);
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

    private void bind() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", "a1QUNOYvIkN");
        maps.put("deviceName", "app_01_test");
        maps.put("token", "A331BD820424E3AB2C9CEFECA457375A");
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

    private void list() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo", 1);
        maps.put("pageSize", 10);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/listBindingByAccount")
                .setApiVersion("1.0.2")
                .setScheme(Scheme.HTTPS)
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
}
