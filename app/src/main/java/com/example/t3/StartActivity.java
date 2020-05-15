package com.example.t3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.alink.apiclient.utils.StringUtils;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.example.t3.fragments.HomeFragment;

/**
 * 程序入口
 */
public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);
        Log.i(TAG,"start" + StringUtils.isEmptyString(getIntent().getStringExtra("auth error")));
        if(LoginBusiness.isLogin() && StringUtils.isEmptyString(getIntent().getStringExtra("auth error"))) {
            Log.i(TAG,"已存在登陆信息");
            start();
        }
        else login();
    }

    /**
     * 调用api登陆
     */
    public void login(){
        LoginBusiness.login(new ILoginCallback() {
            @Override
            public void onLoginSuccess() {
                Log.i(TAG,"登录成功");
                start();
            }
            @Override
            public void onLoginFailed(int code, String error) {
                Log.i(TAG,"登录失败");
                Toast.makeText(StartActivity.this,
                        "登录失败，请验证信息是否正确",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void start(){
        Intent intent=new Intent(StartActivity.this, MainFragmentActivity.class);
        startActivity(intent);
    }
//    private void initView() {
//        // find view
//        mViewPager = findViewById(R.id.fragment_vp);
//        mTabRadioGroup = findViewById(R.id.tabs_rg);
//        // init fragment
//        List<Fragment> mFragments = new ArrayList<>(2);
//        mFragments.add(HomeFragment.newInstance());
//        mFragments.add(MyCountFragment.newInstance());
//        // init view pager
//        FragmentPagerAdapter mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
//        mViewPager.setAdapter(mAdapter);
//        // register listener
//        mViewPager.addOnPageChangeListener(mPageChangeListener);
//        mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mViewPager.removeOnPageChangeListener(mPageChangeListener);
//    }
//
//    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            RadioButton radioButton = (RadioButton) mTabRadioGroup.getChildAt(position);
//            radioButton.setChecked(true);
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//
//        }
//    };
//
//    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            for (int i = 0; i < group.getChildCount(); i++) {
//                if (group.getChildAt(i).getId() == checkedId) {
//                    mViewPager.setCurrentItem(i);
//                    return;
//                }
//            }
//        }
//    };

}

