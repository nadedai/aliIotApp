package com.aliyun.iot.aep.sdk.init;

import android.app.Application;
import android.text.TextUtils;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientImpl;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Env;
import com.aliyun.iot.aep.sdk.framework.sdk.SDKConfigure;
import com.aliyun.iot.aep.sdk.framework.sdk.SimpleSDKDelegateImp;
import com.aliyun.iot.aep.sdk.log.ALog;

import org.json.JSONObject;

import java.util.Map;

/**
 * 用于初始化 API 通道 SDK
 */
@SuppressWarnings("unused")
public final class APIGatewaySDKDelegate extends SimpleSDKDelegateImp {

    static final private String TAG = "APIGatewaySDKDelegate";

    private final static String KEY_ENV = "env";
    private final static String KEY_AUTH_CODE = "securityIndex";
    private final static String KEY_REGION = "region";
    private final static String KEY_LANGUAGE = "language";

    private final static String DEFAULT_ENV = "RELEASE";
    private final static String DEFAULT_AUTH_CODE = "114d";
    private final static String DEFAULT_REGION = "china";
    private final static String DEFAULT_HOST = "api.link.aliyun.com";
    private final static String DEFAULT_LANGUAGE = "zh-CN";

    /* API: ISDKDelegate */

    @Override
    public int init(Application app, SDKConfigure sdkConfigure, Map<String, String> args) {

        int ret = 0;

        // 初始化无线保镖
        try {
            ret = SecurityInit.Initialize(app);
        } catch (JAQException ex) {
            ALog.e(TAG, "security-sdk-initialize-failed", ex);
            ret = ex.getErrorCode();
        } catch (Exception ex) {
            ALog.e(TAG, "security-sdk-initialize-failed", ex);
            ret = -1;
        }

        // 缺省值
        Env apiEnv = Env.RELEASE;
        String env = "RELEASE";
        String region = DEFAULT_REGION;
        String authCode = DEFAULT_AUTH_CODE;
        String language = DEFAULT_LANGUAGE;
        String host = DEFAULT_HOST;
        boolean languageSepcified = false;

        // 读取环境变量
        {
            String e = args.get(KEY_ENV);
            if ("PRE".equalsIgnoreCase(e)) {
                apiEnv = Env.PRE;
                env = "PRE";
            } else if ("TEST".equalsIgnoreCase(e)) {
                apiEnv = Env.RELEASE;
                env = "TEST";
            }

            String re = args.get(KEY_REGION);
            if ("singapore".equalsIgnoreCase(re)) {
                region = "singapore";
            }

            String ac = args.get(KEY_AUTH_CODE);
            if (!TextUtils.isEmpty(ac)) {
                authCode = ac;
            }

            String lan = args.get(KEY_LANGUAGE);
            if (!TextUtils.isEmpty(lan)) {
                languageSepcified = true;
                language = lan;
            }
        }

        // 按照需要，读取opts
        do {

            JSONObject opts = sdkConfigure.opts;
            if (null == opts) {
                break;
            }

            JSONObject re = opts.optJSONObject(region);
            if(null == re){
                break;
            }

            JSONObject hosts = re.optJSONObject("hosts");
            if (null == hosts) {
                break;
            }

            String h = hosts.optString(env.toLowerCase());
            if (!TextUtils.isEmpty(h)) {
                host = h;
            }

            String lan = re.optString("language");
            if(!languageSepcified
                    && !TextUtils.isEmpty(lan)){
                language = lan;
            }

        } while (false);

        // 初始化 IoTAPIClient
        IoTAPIClientImpl.InitializeConfig config = new IoTAPIClientImpl.InitializeConfig();
        config.host = host;
        config.apiEnv = apiEnv;
        config.authCode = authCode;

        IoTAPIClientImpl impl = IoTAPIClientImpl.getInstance();
        impl.init(app, config);
        impl.setLanguage(language);

        return ret;
    }
}