package com.pratham.admin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.modalclasses.CourseTopicItem;

import java.util.List;

public class CourseTopicRVDataAdapter extends RecyclerView.Adapter<CourseTopicRVDataAdapter.CTRVItemHolder> {

    public static List<CourseTopicItem> ItemList;

    public CourseTopicRVDataAdapter(List<CourseTopicItem> carItemList) {
        this.ItemList = carItemList;
    }

    class CTRVItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context context;
        private TextView Course = null;
        private TextView Topic = null;
        private CheckBox chechBox = null;

        public CTRVItemHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            if (itemView != null) {
                Course = (TextView) itemView.findViewById(R.id.tv_Course);
                Topic = (TextView) itemView.findViewById(R.id.tv_Topics);
                chechBox = (CheckBox) itemView.findViewById(R.id.cb_select);
            }

        }

        public TextView getCourse() {
            return Course;
        }

        public void setCourse(TextView course) {
            Course = course;
        }

        public TextView getTopic() {
            return Topic;
        }

        public void setTopic(TextView topic) {
            Topic = topic;
        }

        public CheckBox getChechBox() {
            return chechBox;
        }

        public void setChechBox(CheckBox chechBox) {
            this.chechBox = chechBox;
        }

        @Override
        public void onClick(View v) {
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
    public void onBindViewHolder(final CTRVItemHolder holder, int position) {
        /*if (ItemList != null) {
            CourseTopicItem Items = ItemList.get(position);
            if (Items != null) {
                holder.getCourse().setText(Items.getCourse());
                holder.getTopic().setText(Items.getTopic());
                if (holder.getChechBox().isChecked()) {
                    holder.chechBox.setChecked(true);
                } else {
                    holder.chechBox.setChecked(false);
                }
            }
*/
        holder.chechBox.setChecked(ItemList.get(position).getSelected());
        holder.getCourse().setText(ItemList.get(position).getCourse());
        holder.getTopic().setText(ItemList.get(position).getTopic());

        holder.chechBox.setTag(position);
        holder.chechBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) holder.chechBox.getTag();
                if (ItemList.get(pos).getSelected()) {
                    ItemList.get(pos).setSelected(false);
                } else {
                    ItemList.get(pos).setSelected(true);
                }
            }
        });

/*            //item click event listener
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.chechBox.isChecked())
                        holder.chechBox.setChecked(false);
                    else
                        holder.chechBox.setChecked(true);

                    if (holder.chechBox.isChecked()) {
                        Toast.makeText(ApplicationController.getInstance(), "" + holder.Course.getText() + "\n" + holder.Topic.getText(), Toast.LENGTH_LONG).show();
                    }
                }
            });*/


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