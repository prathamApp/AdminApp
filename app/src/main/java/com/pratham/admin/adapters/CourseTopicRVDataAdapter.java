package com.pratham.admin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.modalclasses.CourseTopicItem;
import com.pratham.admin.modalclasses.DashboardItem;

import java.util.List;

public class CourseTopicRVDataAdapter extends RecyclerView.Adapter<CourseTopicRVDataAdapter.CTRVItemHolder> {

    private List<CourseTopicItem> ItemList;

    public CourseTopicRVDataAdapter(List<CourseTopicItem> carItemList) {
        this.ItemList = carItemList;
    }

    class CTRVItemHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private TextView Course = null;
        private TextView Topic = null;

        public CTRVItemHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            if (itemView != null) {
                Course = (TextView) itemView.findViewById(R.id.tv_Course);
                Topic = (TextView) itemView.findViewById(R.id.tv_Topics);
            }
        }

        public TextView getCourseText() {
            return Course;
        }

        public TextView getTopicText() {
            return Topic;
        }
    }

    @Override
    public CTRVItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get LayoutInflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the RecyclerView item layout xml.
        View carItemView = layoutInflater.inflate(R.layout.course_topic_completion_items, parent, false);
        // Create and return our custom Car Recycler View Item Holder object.
        CTRVItemHolder ret = new CTRVItemHolder(carItemView);
        return ret;
    }

    @Override
    public void onBindViewHolder(CTRVItemHolder holder, int position) {
        if (ItemList != null) {
            CourseTopicItem Items = ItemList.get(position);
            if (Items != null) {
                holder.getCourseText().setText(Items.getCourse());
                holder.getTopicText().setText(Items.getTopic());
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