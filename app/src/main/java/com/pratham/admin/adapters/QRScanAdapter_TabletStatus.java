package com.pratham.admin.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.activities.CustomDialogQRScan;
import com.pratham.admin.activities.CustomDialogTabletStatus;
import com.pratham.admin.interfaces.QRRecyclerListener;
import com.pratham.admin.modalclasses.TabletStatus;

import java.util.List;

public class QRScanAdapter_TabletStatus extends RecyclerView.Adapter<QRScanAdapter_TabletStatus.ViewHolder> {
    private List<TabletStatus> tabletStatuses;
    QRRecyclerListener qrRecyclerListener;
    CustomDialogTabletStatus context;

    public QRScanAdapter_TabletStatus(CustomDialogTabletStatus context, List<TabletStatus> tabletStatuses) {
        this.tabletStatuses = tabletStatuses;
        this.qrRecyclerListener = (QRRecyclerListener) context;
        this.context=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_scan_row_tablet_status, parent, false);
        return new QRScanAdapter_TabletStatus.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
      //  String fname = tabletStatuses.get(holder.getAdapterPosition()).getCRL_Name();
        if (tabletStatuses.get(holder.getAdapterPosition()).getOldFlag() == true) {
            holder.parent_recycler_row.setBackgroundColor(Color.parseColor("#fdeddf"));
        } else {
            holder.parent_recycler_row.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        holder.crlID.setText(tabletStatuses.get(holder.getAdapterPosition()).getLoggedUserId());
        holder.txt_qr_id.setText(tabletStatuses.get(holder.getAdapterPosition()).getQrID());
        holder.pratham_id.setText(tabletStatuses.get(holder.getAdapterPosition()).getPrathamId());
        holder.Status.setText(tabletStatuses.get(holder.getAdapterPosition()).getStatus());
        holder.date.setText(tabletStatuses.get(holder.getAdapterPosition()).getDate());
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrRecyclerListener.delete(holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return tabletStatuses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // @BindView(R.id.checkBox_student)
        TextView crlID, txt_qr_id, pratham_id, Status,date;
        ImageView iv_delete;
        ConstraintLayout parent_recycler_row;

        public ViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this,itemView);
            crlID = itemView.findViewById(R.id.crlID);
            txt_qr_id = itemView.findViewById(R.id.txt_qr_id);
            pratham_id = itemView.findViewById(R.id.pratham_id);
            Status = itemView.findViewById(R.id.Status);
            date = itemView.findViewById(R.id.date);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            parent_recycler_row = itemView.findViewById(R.id.parent_recycler_row);
        }
    }
}
