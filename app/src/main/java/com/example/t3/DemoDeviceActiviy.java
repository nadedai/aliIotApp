package com.example.t3;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;


import com.cunoraz.gifview.library.GifView;
import com.example.t3.controlers.MyAliosKit;

import com.example.t3.models.SwitchBean;
import com.github.zagum.switchicon.SwitchIconView;
import com.robinhood.ticker.TickerView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DemoDeviceActiviy extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DemoDeviceActiviy";
    private String iotId;
    private PanelDevice panelDevice;
    private Handler mHandler, uiHandler;
    private int delayMillis =1000;
    private int number;
    private TickerView tickerView;
    private TextView timeView;
    private GifView gifView;
    private SwitchIconView switchButton;
    private boolean canUpdateButton = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_device_layout);
        tickerView = findViewById(R.id.tickerView);
        switchButton = findViewById(R.id.switchIcon);
        timeView = findViewById(R.id.time);
        switchButton.setOnClickListener(this);
        tickerView.setCharacterLists();

        init();
//        initGifView();
    }

    private void initGifView() {

//        gifView.setGifResource(R.drawable.fengche2);//本地图片
//        gifView.setScaleY(2);
//        int width = gifView.getWidth();
//        int height = gifView.getHeight();
//        int screenWidth = getScreenSize().width();
//        int screenHeight = getScreenSize().height();
//
//        if (width > 0 && height > 0) {
//            float wScale = (float) screenWidth / width;
//            float hScale = (float) screenHeight / height;
//            if (wScale < 1 || hScale < 1) {
//                // 如果图片的宽或高大于屏幕的宽或高，则图片会自动缩小至全屏
//            } else if (wScale <= hScale) {
//                // 宽度全屏
//                gifView.setScaleX(wScale);
//                gifView.setScaleY(wScale);
//            } else {
//                // 高度全屏
//                gifView.setScaleX(hScale);
//                gifView.setScaleY(hScale);
//            }

        //位置设置
//        gifView.setX(150);
//        gifView.setY(300);

    }

    @SuppressLint("HandlerLeak")
    void init(){
        iotId = getIntent().getStringExtra("iotId");
        mHandler = new Handler();
        uiHandler = new Handler(){
            //handleMessage为处理消息的方法
            @SuppressLint("SimpleDateFormat")
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                tickerView.setText(String.valueOf(msg.arg1));
                timeView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                        format(new Date(Long.parseLong(msg.obj.toString()))));

                //如果按钮在使用中则不更新 且 按钮处理后的本次数据失效
                if(canUpdateButton){
                    if(!isBtnProcess){
                        if(switchButton.isIconEnabled() != (1 == msg.arg2))
                            Toast.makeText(DemoDeviceActiviy.this, "设备可能未连接互联网", Toast.LENGTH_SHORT).show();

                        switchButton.setIconEnabled(1 == msg.arg2);
                    }
                    isBtnProcess = false;
                }
            }
        };

        //初始化物
        if(iotId != null && !iotId.equals("")){
            initPanel(iotId);
        }
        //更新UI
        delayUpdate(delayMillis);
    }

    private void delayUpdate(int delayMillis){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                 MyAliosKit.getProperties(panelDevice, new MyAliosKit.Propertielistener() {
                    @Override
                    public void success(SwitchBean sb) {
                        Log.i(TAG, "success: "+ sb.getCounterTime());
                        //tickerView.setText(String.valueOf(sb.getCounter()));
                        Message msg = Message.obtain();
                        msg.obj = sb.getCounterTime();
                        msg.arg1 =  sb.getCounter();
                        msg.arg2 = sb.getPowerSwitch();
                        uiHandler.sendMessage(msg);
                    }

                    @Override
                    public void fail() {

                    }
                });
                mHandler.postDelayed(this, delayMillis);
            }
        };
        mHandler.post(runnable);
    }

    private void initPanel(String iodID){
        panelDevice = new PanelDevice(iodID);
        panelDevice.init(this, new IPanelCallback() {
            @Override
            public void onComplete(boolean b, Object o) {
                Log.i(TAG,"物初始化"+o);
            }
        });
    }

    private void panel(){
        panelDevice.getStatus(new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, Object o) {
                Log.i(TAG,"状态"+o);
            }
        });
//

//

    }

    //按钮触发处理
    private boolean isBtnProcess;
    @Override
    public void onClick(View view) {
        //"{\"iotId\": \"hFBKHvhXIAm6pY0ojEBg000100\",\"items\": {\"Counter\": 999}}"
        canUpdateButton = false;
        if(switchButton.isIconEnabled()){
            switchButton.setIconEnabled(false);
            MyAliosKit.set(panelDevice,iotId,0);
        }
        else{
            switchButton.setIconEnabled(true);
            MyAliosKit.set(panelDevice,iotId,1);
        }
        canUpdateButton = true;
        isBtnProcess = true;
    }
}
