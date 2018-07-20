package com.pratham.admin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.modalclasses.DashboardItem;

import java.util.List;

public class DashRVDataAdapter extends RecyclerView.Adapter<DashRVDataAdapter.DashRVItemHolder> {

    private List<DashboardItem> ItemList;

    public DashRVDataAdapter(List<DashboardItem> carItemList) {
        this.ItemList = carItemList;
    }

    class DashRVItemHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private TextView TitleText = null;
        private ImageView ImageView = null;

        public DashRVItemHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            if (itemView != null) {
                TitleText = (TextView) itemView.findViewById(R.id.card_view_image_title);
                ImageView = (ImageView) itemView.findViewById(R.id.card_view_image);
            }
        }

        public TextView getTitleText() {
            return TitleText;
        }

        public ImageView getImageView() {
            return ImageView;
        }
    }

    @Override
    public DashRVItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get LayoutInflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the RecyclerView item layout xml.
        View carItemView = layoutInflater.inflate(R.layout.dashboard_items, parent, false);
        // Create and return our custom Car Recycler View Item Holder object.
        DashRVItemHolder ret = new DashRVItemHolder(carItemView);
        return ret;
    }

    @Override
    public void onBindViewHolder(DashRVItemHolder holder, int position) {
        if (ItemList != null) {
            // Get car item dto in list.
            DashboardItem carItem = ItemList.get(position);
            if (carItem != null) {
                // Set car item title.
                holder.getTitleText().setText(carItem.getName());
                // Set car image resource id.
                holder.getImageView().setImageResource(carItem.getImageId());
            }
        }
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if (ItemList != null) {
            ret = ItemList.size();
        }
        return ret;
    }
}