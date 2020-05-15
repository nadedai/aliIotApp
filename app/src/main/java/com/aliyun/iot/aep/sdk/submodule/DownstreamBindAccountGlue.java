package com.aliyun.iot.aep.sdk.submodule;

import android.app.Application;
import android.text.TextUtils;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.aep.sdk.framework.sdk.SDKConfigure;
import com.aliyun.iot.aep.sdk.framework.sdk.SimpleSDKDelegateImp;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.ILoginStatusChangeListener;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;

import java.util.Map;

/**
 * Created by xingwei on 2018/7/3.
 */

public class DownstreamBindAccountGlue extends SimpleSDKDelegateImp {
    static final String TAG = "DownstreamBindAccount";

    @Override
    public int init(final Application app, SDKConfigure sdkConfigure, Map<String, String> map) {
        LoginBusiness.getLoginAdapter().registerLoginListener(new ILoginStatusChangeListener() {
            @Override
            public void onLoginStatusChange() {
                bindAccount(app);
            }
        });

        MobileChannel.getInstance().registerConnectListener(true, new IMobileConnectListener() {
            @Override
            public void onConnectStateChange(MobileConnectState mobileConnectState) {
                if (MobileConnectState.CONNECTED.equals(mobileConnectState)) {
                    bindAccount(app);
                }
            }
        });

        // 2. register logout user
        ((OALoginAdapter) LoginBusiness.getLoginAdapter())
                .registerBeforeLogoutListener(new OALoginAdapter.OnBeforeLogoutListener() {
                    @Override
                    public void doAction() {
                        MobileChannel.getInstance().unBindAccount(new IMobileRequestListener() {
                            @Override
                            public void onSuccess(String s) {
                                ALog.i(TAG, "mqtt unBindAccount onSuccess ");
                            }

                            @Override
                            public void onFailure(AError aError) {
                                ALog.i(TAG, "mqtt unBindAccount onFailure aError = " + aError.getMsg());
                            }
                        });
                    }
                });
        return 0;
    }

    private void bindAccount(Application app) {
        if (!LoginBusiness.isLogin()) {
            return;
        }
        IoTCredentialManageImpl ioTCredentialManage = IoTCredentialManageImpl.getInstance(app);
        if (null == ioTCredentialManage) {
            return;
        }

        if (TextUtils.isEmpty(ioTCredentialManage.getIoTToken())) {
            ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
                @Override
                public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
                    MobileChannel.getInstance().bindAccount(ioTCredentialData.iotToken, new IMobileRequestListener() {
                        @Override
                        public void onSuccess(String s) {
                            ALog.i(TAG, "mqtt bindAccount onSuccess");

                        }

                        @Override
                        public void onFailure(AError aError) {
                            ALog.i(TAG, "mqtt bindAccount onFailure aError = " + aError.getMsg());
                        }
                    });
                }

                @Override
                public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {
                    ALog.i(TAG, "mqtt bindAccount onFailure ");

                }
            });
        } else {
            MobileChannel.getInstance().bindAccount(ioTCredentialManage.getIoTToken(), new IMobileRequestListener() {
                @Override
                public void onSuccess(String s) {
                    ALog.i(TAG, "mqtt bindAccount onSuccess ");
                }

                @Override
                public void onFailure(AError aError) {
                    ALog.i(TAG, "mqtt bindAccount onFailure aError = " + aError.getMsg());
                }
            });
        }
    }
}
