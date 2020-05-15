package com.example.t3.controlers.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t3.R;
import com.example.t3.models.DeviceInfoBean;

import java.util.ArrayList;
import java.util.List;

public class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.MyTVHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private static final String TAG = "MyRVAdapter";
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    // 设备
    private List<DeviceInfoBean> listDevice;

    public MyRVAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setListDevice(List<DeviceInfoBean> list){
        listDevice = list;
    }
    @NonNull
    @Override
    public MyTVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyTVHolder(mLayoutInflater.inflate(R.layout.device_panel_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyTVHolder holder, int position) {
        
        if (mOnItemClickListener != null) {
            // 添加点击事件
            holder.itemView.setOnClickListener(v -> {
//                int pos = holder.getLayoutPosition();
                mOnItemClickListener.onItemClick(holder.itemView, position);
            });

            // 添加长按事件
            holder.itemView.setOnLongClickListener(v -> {
                mOnItemClickListener.onItemLongClick(holder.itemView, position);
                return true;
            });
        }
        bindData(holder, position);

//        Glide.with(mContext)
//                .load(device.getCategoryImage())
//                .into(mImageView);
    }


    private void bindData(MyTVHolder holder, int position){
        DeviceInfoBean device = listDevice.get(position);
        Log.i(TAG, "bindData: "+ device.getNickName());
        if(!TextUtils.isEmpty(device.getNickName())){
            holder.mNameTv.setText(device.getNickName());
        }
        else if (!TextUtils.isEmpty(device.getProductName())) {
            holder.mNameTv.setText(device.getProductName());
        } else {
            holder.mNameTv.setText(device.getDeviceName());
        }

        if ("VIRTUAL".equalsIgnoreCase(device.getThingType()) || "VIRTUAL_SHADOW".equalsIgnoreCase(device.getThingType())) {
            holder.mTypeTv.setText("虚拟");
        } else {
            holder.mTypeTv.setText(device.getNetType());
        }
        if (3 == device.getStatus()) {
            holder.mStatusTv.setText("离线");
            holder.mStatusTv.setAlpha(0.8f);
        } else {
            holder.mStatusTv.setText("在线");
            holder.mStatusTv.setTextColor(Color.parseColor("#B3EE3A"));
        }

        if(device.getProductName().equals("开关控制")){
            holder.mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.swich));
        }
        else{
            holder.mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.product));
        }
    }



    @Override
    public int getItemCount() {
        return listDevice == null ? 0 : listDevice.size();
    }


    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView mNameTv;
        TextView mTypeTv;
        TextView mStatusTv;
        ImageView mImageView;

        MyTVHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.device_panel_name);
            mTypeTv = itemView.findViewById(R.id.device_panel_type);
            mStatusTv = itemView.findViewById(R.id.device_panel_status);
            mImageView = itemView.findViewById(R.id.iv_image);
        }
    }
}
