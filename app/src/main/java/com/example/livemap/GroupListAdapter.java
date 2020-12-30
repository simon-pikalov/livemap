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

    public GroupListAdapter(Context context, List<Group> groupList) {
        mInflater = LayoutInflater.from(context);
        this.mGroupList = groupList;
    }

    class GroupViewHolder extends RecyclerView.ViewHolder{

        public final TextView groupItemView;
        final GroupListAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public GroupViewHolder(@NonNull View itemView, GroupListAdapter adapter) {
            super(itemView);
            groupItemView = itemView.findViewById(R.id.group_item_for_list);
            this.mAdapter = adapter;
        }

    }


    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to
     * represent an item.
     *
     * This new ViewHolder should be constructed with a new View that can
     * represent the items of the given type. You can either create a new View
     * manually or inflate it from an XML layout file.
     *
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be reused to
     * display different items in the data set, it is a good idea to cache
     * references to sub views of the View to avoid unnecessary findViewById()
     * calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after
     *                 it is bound to an adapter position.
     * @param viewType The view type of the new View. @return A new ViewHolder
     *                 that holds a View of the given view type.
     */
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
