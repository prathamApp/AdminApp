package com.pratham.admin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.modalclasses.NotificationData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<NotificationData> notificationList;
    Context context;

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        @BindView(R.id.nf_date) TextView nf_date;
        @BindView(R.id.nf_content) TextView nf_content;
        @BindView(R.id.nf_damageType) TextView nf_damageType;
        @BindView(R.id.iv_upper_view) View upperView;
        @BindView(R.id.iv_lower_view) View lowerView;
        @BindView(R.id.itemSeperator) View itemSeperator;

        public MyViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public NotificationAdapter(Context context, List<NotificationData> notificationList){
        this.context=context;
        this.notificationList=notificationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_row, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotificationData notification = notificationList.get(position);
        holder.nf_date.setText(notification.getReceiveDate());
        holder.nf_content.setText(notification.getFromName()+"( "+notification.getFromId()+" )");
        holder.nf_damageType.setText(notification.getDamageType());

        if(position==0){
            holder.upperView.setVisibility(View.INVISIBLE);
        } else if(position==notificationList.size()-1){
            holder.lowerView.setVisibility(View.INVISIBLE);
            holder.itemSeperator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void filterList(ArrayList<NotificationData> filterdNames) {
        this.notificationList=filterdNames;
        notifyDataSetChanged();
    }
}