package com.pratham.admin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.admin.R;
import com.pratham.admin.interfaces.DevicePrathamIdLisner;
import com.pratham.admin.modalclasses.DeviseList;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.MyViewHolder> {
    List<DeviseList> deviseList;
    DevicePrathamIdLisner devicePrathamIdLisner;
    Context context;
    public DeviceListAdapter(Context context,List deviseList) {
        this.deviseList = deviseList;
        devicePrathamIdLisner= (DevicePrathamIdLisner) context;
        this.context=context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      //  if(deviseList.get(position).getPratham_ID()!=null)
        holder.prathamId.setText("prathamId :"+deviseList.get(position).getPratham_ID());
       // if(deviseList.get(position).getQR_ID()!=null)
        holder.qrId.setText("qr Id :"+deviseList.get(position).getQR_ID());
        holder.deviceId.setText("deviceId :"+deviseList.get(position).getDeviceid());
        holder.serialID.setText("serialId :"+deviseList.get(position).getSerialno());
        holder.brand.setText("Model:"+deviseList.get(position).getBrand()+""+deviseList.get(position).getModel());
        holder.macAdd.setText("Mac Addr :"+deviseList.get(position).getMacAdd());
    }

    @Override
    public int getItemCount() {
        return deviseList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView prathamId;
        TextView qrId;
        TextView deviceId;
        TextView serialID;
        TextView brand;
        TextView macAdd;

        public MyViewHolder(View itemView) {
            super(itemView);
            prathamId = itemView.findViewById(R.id.prathamId);
            qrId = itemView.findViewById(R.id.QrId);
            deviceId = itemView.findViewById(R.id.deviceId);
            serialID = itemView.findViewById(R.id.serialNo);
            brand = itemView.findViewById(R.id.brand);
            macAdd = itemView.findViewById(R.id.macAdd);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  String pID=deviseList.get(getAdapterPosition()).getPratham_ID();
                  String qID=deviseList.get(getAdapterPosition()).getQR_ID();
                  if(pID!=null&&qID!=null){
                      Log.d(":::",deviseList.get(getAdapterPosition()).getPratham_ID());
                      devicePrathamIdLisner.getPrathamId(pID,qID);
                  }else {
                      Toast.makeText(context, "Pratham id or Qr id is null", Toast.LENGTH_SHORT).show();
                  }
                }
            });
        }

    }
}
