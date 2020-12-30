package com.example.livemap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livemap.objects.Group;

import java.util.LinkedList;
import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>{
    private final List<Group> mGroupList;
    private LayoutInflater mInflater;

    public GroupListAdapter(Context context,
                            List<Group> groupList) {
        mInflater = LayoutInflater.from(context);
        this.mGroupList = groupList;
    }

    class GroupViewHolder extends RecyclerView.ViewHolder{

        public final TextView groupItemView;
        final GroupListAdapter mAdapter;

        public GroupViewHolder(@NonNull View itemView, GroupListAdapter adapter) {
            super(itemView);
            groupItemView = itemView.findViewById(R.id.group_item_for_list);
            this.mAdapter = adapter;
        }

    }


    @NonNull
    @Override
    public GroupListAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.my_groups_item, parent, false);
        return new GroupViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupListAdapter.GroupViewHolder holder, int position) {
        Group mCurrent = mGroupList.get(position);
        holder.groupItemView.setText(mCurrent.getName());
    }

    @Override
    public int getItemCount() {
        return mGroupList.size();
    }
}
