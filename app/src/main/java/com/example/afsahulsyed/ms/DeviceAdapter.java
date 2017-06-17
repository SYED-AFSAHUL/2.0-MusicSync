package com.example.afsahulsyed.ms;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by afsahulsyed on 11/6/17.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyHolder> {

    final String TAG = "sMess";
    private List<WifiP2pDevice> deviceList;

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);

        void OnItemLongClick(View view, int position);
    }

    public DeviceAdapter.OnItemClickListener mOnItemClickListener;

    public void SetOnItemClickListener(DeviceAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public DeviceAdapter(List<WifiP2pDevice> deviceList) {
        super();
        this.deviceList = deviceList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_layout, parent, false);
        return new DeviceAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        //Log.d(TAG, holder + "  -----" + holder.tTitle + "--------" + musicList);
        holder.tDeviceName.setText(deviceList.get(position).deviceName);
        holder.tDeviceAdd.setText(deviceList.get(position).deviceAddress);

        if (mOnItemClickListener != null) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(holder.itemView, position);
                }

            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.OnItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }

    }
    public void addData(int position, WifiP2pDevice map) {
    }

    public void removeData(int position) {
        deviceList.remove(position);
        notifyItemRemoved(position);
    }
    public void RefreshView(){
        for(int i=0;i<getItemCount();i++)
            removeData(i);
        for(int i=0;i<getItemCount();i++)
            addData(i,deviceList.get(i));
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tDeviceName;
        public TextView tDeviceAdd;

        public MyHolder(View View) {
            super(View);
            tDeviceName = (TextView) View.findViewById(R.id.device_name);
            tDeviceAdd = (TextView) View.findViewById(R.id.device_mac_add);
        }
    }
}
