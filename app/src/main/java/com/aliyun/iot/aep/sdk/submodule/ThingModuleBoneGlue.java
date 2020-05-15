package com.aliyun.iot.aep.sdk.submodule;

import android.app.Application;

import com.aliyun.alink.linksdk.tmp.extbone.BoneThing;
import com.aliyun.alink.sdk.jsbridge.BonePluginRegistry;
import com.aliyun.iot.aep.sdk.framework.sdk.SDKConfigure;
import com.aliyun.iot.aep.sdk.framework.sdk.SimpleSDKDelegateImp;

import java.util.Map;

/**
 * Created by xingwei on 2018/7/3.
 */

public class ThingModuleBoneGlue extends SimpleSDKDelegateImp {

    @Override
    public int init(Application application, SDKConfigure sdkConfigure, Map<String, String> map) {

        BonePluginRegistry.register("BoneThing", BoneThing.class);
        return 0;
    }
}
