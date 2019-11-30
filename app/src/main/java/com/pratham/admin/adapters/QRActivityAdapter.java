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
import com.pratham.admin.interfaces.QRRecyclerListener;
import com.pratham.admin.modalclasses.TabTrack;

import java.util.List;

public class QRActivityAdapter extends RecyclerView.Adapter<QRActivityAdapter.ViewHolder> {
    private List<TabTrack> tabTracks;
    QRRecyclerListener qrRecyclerListener;
    Context context;

    public QRActivityAdapter(CustomDialogQRScan context, List<TabTrack> tabTracks) {
        this.tabTracks = tabTracks;
        this.qrRecyclerListener = (QRRecyclerListener) context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_activity_row, parent, false);
        return new QRActivityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String crl_name = tabTracks.get(holder.getAdapterPosition()).getCRL_Name();
        if (tabTracks.get(holder.getAdapterPosition()).getOldFlag() == true) {
            holder.parent_recycler_row.setBackgroundColor(Color.parseColor("#fdeddf"));
        } else {
            holder.parent_recycler_row.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        holder.crl_name.setText(crl_name);
        holder.txt_crl_id.setText(tabTracks.get(holder.getAdapterPosition()).getCRL_ID());
        holder.txt_qr_id.setText(tabTracks.get(holder.getAdapterPosition()).getQR_ID());
        holder.txt_date.setText(tabTracks.get(holder.getAdapterPosition()).getDate());
        holder.txt_state.setText(tabTracks.get(holder.getAdapterPosition()).getState());
        holder.txt_pratham_id.setText(tabTracks.get(holder.getAdapterPosition()).getPratham_ID());
        /*   holder.txt_loggedIn_crl.setText(tabTracks.get(holder.getAdapterPosition()).getLoggedIn_CRL());*/
        holder.txt_sr_no.setText(tabTracks.get(holder.getAdapterPosition()).getSerial_NO());
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrRecyclerListener.delete(holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return tabTracks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // @BindView(R.id.checkBox_student)
        TextView crl_name, txt_qr_id, txt_crl_id, txt_state, txt_pratham_id, txt_date, txt_sr_no;
        ImageView iv_delete;
        ConstraintLayout parent_recycler_row;

        public ViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this,itemView);
            crl_name = itemView.findViewById(R.id.crl_name);
            txt_qr_id = itemView.findViewById(R.id.txt_qr_id);
            txt_crl_id = itemView.findViewById(R.id.txt_crl_id);
            txt_state = itemView.findViewById(R.id.txt_state);
            txt_pratham_id = itemView.findViewById(R.id.txt_pratham_id);
            txt_date = itemView.findViewById(R.id.txt_date);
            /* txt_loggedIn_crl = itemView.findViewById(R.id.txt_loggedIn_crl);*/
            txt_sr_no = itemView.findViewById(R.id.txt_sr_no);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            parent_recycler_row = itemView.findViewById(R.id.parent_recycler_row);
        }
    }
}
