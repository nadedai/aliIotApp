package com.aliyun.iot.aep.sdk.init;

import android.app.Application;
import android.text.TextUtils;

import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.iot.aep.sdk.apiclient.adapter.APIGatewayHttpAdapterImpl;
import com.aliyun.iot.aep.sdk.framework.sdk.SDKConfigure;
import com.aliyun.iot.aep.sdk.framework.sdk.SimpleSDKDelegateImp;
import com.aliyun.iot.aep.sdk.log.ALog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by wuwang on 2017/10/30.
 */

public final class DownstreamConnectorSDKDelegate extends SimpleSDKDelegateImp {

    private final static String KEY_ENV = "env";
    private final static String KEY_AUTH_CODE = "securityIndex";
    private final static String KEY_CHANNEL_HOST = "channelHost";
    private final static String KEY_AUTO_HOST = "autoSelectChannelHost";
    private final static String KEY_CHECK_ROOT_CRT = "isCheckChannelRootCrt";

    public final static String ENV_KEY_MQTT_HOST = "ENV_KEY_MQTT_HOST";
    public final static String ENV_KEY_MQTT_CHECK_ROOT_CRT = "ENV_KEY_MQTT_CHECK_ROOT_CRT";
    public final static String ENV_KEY_MQTT_AUTO_HOST = "ENV_KEY_MQTT_AUTO_HOST";

    static final private String TAG = "DownstreamConnectorSDKDelegate";

    /* API: ISDKDelegate */

    @Override
    public int init(final Application app, SDKConfigure configure, final Map<String, String> args) {
        int ret = 0;
        // 读取环境变量
        String env = args == null ? "RELEASE" : args.get(KEY_ENV);
        String authCode = args == null ? "114d" : args.get(KEY_AUTH_CODE);
        String appKey = APIGatewayHttpAdapterImpl.getAppKey(app, authCode);

        MobileConnectConfig config = new MobileConnectConfig();
        config.appkey = appKey;
        config.securityGuardAuthcode = authCode;

        JSONObject opts = configure.opts;
        if (TextUtils.isEmpty(env)) {
            env = "RELEASE";
        }
        JSONObject json = null;
        try {
            if (null != opts) {
                json = opts.getJSONObject(env.toLowerCase());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String channelHost = "";
        boolean autoHost = false;
        boolean isCheckRootCrt = false;
        if (null != json) {
            try {
                channelHost = json.getString(KEY_CHANNEL_HOST);
                autoHost = !"false".equalsIgnoreCase(json.getString(KEY_AUTO_HOST));
                isCheckRootCrt = !"false".equalsIgnoreCase(json.getString(KEY_CHECK_ROOT_CRT));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // 三元组，指定apiclient host
        config.autoSelectChannelHost = autoHost;
        config.channelHost = channelHost;
        config.isCheckChannelRootCrt = isCheckRootCrt;

        // 注意:长连接的初始化只能在主进程中执行，否则会导致互踢问题
        MobileChannel.getInstance().startConnect(app, config, new IMobileConnectListener() {
            @Override
            public void onConnectStateChange(MobileConnectState state) {
                ALog.d(TAG, "onConnectStateChange(), state = " + state.toString());
                args.put("KEY_TRACE_ID", MobileChannel.getInstance().getClientId());
            }
        });

        ALog.d(TAG, "initialized");
        return ret;
    }
}
