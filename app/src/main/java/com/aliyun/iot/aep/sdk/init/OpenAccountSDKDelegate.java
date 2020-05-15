package com.aliyun.iot.aep.sdk.init;

import android.app.Application;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.openaccount.ui.LayoutMapping;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;
import com.alibaba.sdk.android.openaccount.ui.ui.MobileCountrySelectorActivity;
import com.aliyun.iot.aep.oa.OAUIInitHelper;
import com.aliyun.iot.aep.oa.page.data.ALiYunAuthConfigData;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientImpl;
import com.aliyun.iot.aep.sdk.apiclient.adapter.APIGatewayHttpAdapterImpl;
import com.aliyun.iot.aep.sdk.apiclient.hook.IoTAuthProvider;
import com.aliyun.iot.aep.sdk.credential.IoTCredentialProviderImpl;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.framework.sdk.SDKConfigure;
import com.aliyun.iot.aep.sdk.framework.sdk.SimpleSDKDelegateImp;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wuwang on 2017/10/30.
 */

public final class OpenAccountSDKDelegate extends SimpleSDKDelegateImp {
    static final private String TAG = "OpenAccountSDKDelegate";

    public static final String ENV_KEY_API_CLIENT_API_ENV = "KEY_API_CLIENT_API_ENV";

    /* API: ISDKDelegate */

    @Override
    public int init(Application app, SDKConfigure configure, Map<String, String> args) {
        String env = args == null ? "TEST" : args.get("env");
        String securityIndex = args == null ? "114d" : args.get("securityIndex");
        String region = "China";
        if (null != args) {
            String r = args.get("region");
            if (!TextUtils.isEmpty(r)) {
                region = r;
            }
        }
        String appKey = APIGatewayHttpAdapterImpl.getAppKey(app, securityIndex);

        //获取SDK的参数配置
        JSONObject opts = configure.opts;

        //1--- 初始化默认OALoginAdapter（对ILoginAdapter的实现）
        OALoginAdapter loginAdapter = new OALoginAdapter(app);
        if (args != null && region.equalsIgnoreCase("Singapore")) {
            if (opts != null && opts.has("sgp_host")) {
                try {//设置新加坡Host
                    loginAdapter.setDefaultOAHost(opts.getString("sgp_host"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        loginAdapter.init(env, securityIndex, new OALoginAdapter.OALoginAdapterInitResultCallback() {
            @Override
            public void onInitSuccess() {
                Log.i(TAG, "onInitSuccess");
            }

            @Override
            public void onInitFailed(int i, String s) {
                Log.i(TAG, "onInitFailed");
            }
        });
        loginAdapter.setIsDebuggable(true);


        //2--- 初始化统一登录接口SDK，其中loginAdapter必须要求实现ILoginAdapter，并且已经完成初始化
        LoginBusiness.init(app, loginAdapter, true, env);

        //3--- 注入默认UI(支持阿里云账号)
//        boolean supportALiYun = false;
//        ALiYunAuthConfigData configData = null;
//        try {
//            if (opts != null) {
//                supportALiYun = opts.has("supportAliYun") && "true".equalsIgnoreCase(opts.getString("supportAliYun"));
//                configData = new ALiYunAuthConfigData();
//                configData.oauth_consumer_key_test = opts.getString("oauth_consumer_key_test");
//                configData.oauth_consumer_key_online = opts.getString("oauth_consumer_key_online");
//                configData.oauth_consumer_secret_test = opts.getString("oauth_consumer_secret_test");
//                configData.oauth_consumer_secret_online = opts.getString("oauth_consumer_secret_online");
//            }
//        } catch (Exception e) {
//        }
//        OAUIInitHelper.initConfig(loginAdapter, supportALiYun, configData);


        //4--- 注入自定义登录页面样式(在sdk_config.json中配置)
//        try {
//            if (!supportALiYun) {//非阿里云账号才可以自定义UI
//                injectCustomUIConfig(app, loginAdapter, opts);
//            }
//        } catch (Exception e) {
//            ALog.i(TAG, "Inject CustomUIConfig failed:" + e.toString());
//        }

        //5--- 设置是否禁止横屏
        try {
            if (opts != null && opts.has("disable_screen_protrait")) {
                boolean disableScreenProtrait = "true".equalsIgnoreCase(opts.getString("disable_screen_protrait"));
                if (disableScreenProtrait) {
                    OAUIInitHelper.disableScreenLandscape();
                }
            }
        } catch (Exception e) {

        }

        //6--- 是否禁止显示国家码按钮
        try {
            if (opts != null && opts.has("disable_foreign_mobile_number")) {
                boolean disableForeignMobileNumber = "true".equalsIgnoreCase(opts.getString("disable_foreign_mobile_number"));
                if (disableForeignMobileNumber) {
                    OAUIInitHelper.disableForeignMobileNumbers();
                }
            }
        } catch (Exception e) {

        }

        //7--- 初始化身份认证SDK
        IoTCredentialManageImpl.init(appKey);

        //8--- 初始化自动填充IoTtoken模块
        IoTAuthProvider provider = new IoTCredentialProviderImpl(IoTCredentialManageImpl.getInstance(app));
        IoTAPIClientImpl.getInstance().registerIoTAuthProvider("iotAuth", provider);

        try {//配置阿里云账号日常地址
            String aliyunDailyCreateIotTokenHost = "";
            if (opts != null) {
                aliyunDailyCreateIotTokenHost = opts.getString("aliyun_daily_create_iottoken_host");
            }
            if (!TextUtils.isEmpty(aliyunDailyCreateIotTokenHost)) {
                IoTCredentialManageImpl.DefaultDailyALiYunCreateIotTokenRequestHost = aliyunDailyCreateIotTokenHost;
            }
        } catch (Exception e) {

        }

        return 0;
    }

    private static void injectCustomUIConfig(Application app, OALoginAdapter loginAdapter, JSONObject customUIData) throws Exception {
        JSONArray array = customUIData.getJSONArray("ui_config");
        if (array == null || array.length() == 0) {
            return;
        }
        String activity, layout;
        boolean isSelectMobileCountry;

        for (int i = 0; i < array.length(); i++) {
            activity = ((JSONObject) array.get(i)).getString("activity");
            layout = ((JSONObject) array.get(i)).getString("layout");
            isSelectMobileCountry = false;
            if (((JSONObject) array.get(i)).has("is_select_mobile_country")) {
                isSelectMobileCountry = "true".equalsIgnoreCase(((JSONObject) array.get(i)).getString("is_select_mobile_country"));
            }
            Class activityClazz = Class.forName(activity);
            if (LoginActivity.class.isAssignableFrom(activityClazz)) {//如果是LoginActivity的子类，那么需要重置启动入口
                loginAdapter.setDefaultLoginClass(activityClazz);
                //注入启动参数
                JSONObject params = ((JSONObject) array.get(i)).getJSONObject("params");
                Map<String, String> loginActivityParams = new HashMap<>();
                if (params != null) {
                    Iterator it = params.keys();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        loginActivityParams.put(key, params.getString(key));
                    }
                }
                if (!loginActivityParams.isEmpty()) {
                    loginAdapter.setDefaultLoginParams(loginActivityParams);
                }
            } else if (MobileCountrySelectorActivity.class.isAssignableFrom(activityClazz) || isSelectMobileCountry) {//注入选择国家码页面
                OAUIInitHelper.setCustomSelectCountryActivityUIConfig(activityClazz);
            }

            if (!TextUtils.isEmpty(layout)) {
                LayoutMapping.put(activityClazz, getLayoutIdFromR(app, layout));
            }
        }
    }

    private static int getLayoutIdFromR(Application app, String idName) {
        Resources resources = app.getResources();
        return resources.getIdentifier(idName, "layout", app.getPackageName());
    }
}
