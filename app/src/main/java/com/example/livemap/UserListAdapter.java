package com.example.livemap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livemap.objects.Group;
import com.example.livemap.objects.User;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.GroupViewHolder> {
    private final List<User> mUserList;
    private LayoutInflater mInflater;
    private final Button mRemoveUserButton;
    private User mSelectedUser;
    private Group mGroup;

    public UserListAdapter(Context context, Group group, Button rub) {

        mInflater = LayoutInflater.from(context);
        mUserList = group.getUsers();
        mRemoveUserButton = rub;
        mGroup = group;
    }


    class GroupViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        public final TextView groupItemView;
        final UserListAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public GroupViewHolder(@NonNull View itemView, UserListAdapter adapter) {
            super(itemView);
            groupItemView = itemView.findViewById(R.id.user_item_group_list);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);

            mRemoveUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGroup.removeUser(mSelectedUser);
                    mUserList.remove(mSelectedUser);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();
            // Use that to access the affected item in mWordList.
            mSelectedUser = mUserList.get(mPosition);
            mRemoveUserButton.setVisibility(View.VISIBLE);

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
    public UserListAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.user_item, parent, false);
        return new GroupViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.GroupViewHolder holder, int position) {
        User mCurrent = mUserList.get(position);
        holder.groupItemView.setText(mCurrent.getName());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}
