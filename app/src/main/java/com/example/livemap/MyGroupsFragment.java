package com.example.livemap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livemap.objects.Group;
import com.example.livemap.objects.User;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass that shows a question
 * with radio buttons for providing feedback. If the user
 * clicks "Yes" the text header changes to "Article: Like".
 * If the user clicks "No" the text header changes to "Thanks".
 */
public class MyGroupsFragment extends Fragment implements GroupListAdapter.OnItemClickListener {
    final static int VIEW_MODE = 1;
    final static int SELECT_MODE = 0;
    private RecyclerView mRecyclerView;
    private GroupListAdapter mAdapter;

    OnFragmentInteractionListener mListener;



    static User mUser;
    private List<Group> mGroupList;
    private Group mSelectedGroup;
    private int windowMode = VIEW_MODE;
    private Button viewGroupButton;
    private Button closeButton;
    private Button exitGroupButton;

    public MyGroupsFragment() {
        // Required empty public constructor
    }



    interface OnFragmentInteractionListener {
        // this function only returns to caller activity
        void myGroupsFragmentComplete();
        void myGroupsFragmentToGroupFragment(Group g);
    }

    /**
     * Creates the view for the fragment.
     *
     * @param inflater           LayoutInflater to inflate any views in the fragment
     * @param container          ViewGroup of parent view to attach fragment
     * @param savedInstanceState Bundle for previous state
     * @return rootView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                        Bundle savedInstanceState) {
        mGroupList = mUser.getGroups();
        Log.w("MyGroupsFrag", "user is: "+mUser);
        Log.w("MyGroupsFrag", "got groups: "+mGroupList);

        // Inflate the layout for this fragment.
        final View rootView = inflater.inflate(R.layout.fragment_my_groups,
                container, false);


        viewGroupButton = rootView.findViewById(R.id.view_group_button_my_groups);
        closeButton = rootView.findViewById(R.id.close_button_my_groups);
        exitGroupButton = rootView.findViewById(R.id.exit_group_button);

        // button only visible when some group is selected
        viewGroupButton.setVisibility(View.INVISIBLE);
        exitGroupButton.setVisibility(View.INVISIBLE);

        // Get a handle to the RecyclerView.
        mRecyclerView = rootView.findViewById(R.id.my_groups_list);
        // Create an adapter and supply the data to be displayed.

        mAdapter = new GroupListAdapter(getContext(), this, mUser, mGroupList);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        exitGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedGroup.removeUser(mUser);
                // keep group list synced
                mGroupList.remove(mSelectedGroup);
                mAdapter.notifyDataSetChanged();
                exitGroupButton.setVisibility(View.INVISIBLE);
                viewGroupButton.setVisibility(View.INVISIBLE);
            }
        });
        viewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSelectedGroup != null){
                    mListener.myGroupsFragmentToGroupFragment(mSelectedGroup);
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.myGroupsFragmentComplete();
            }

        });

        // Return the View for the fragment's UI.
        return rootView;
    }
    // marker can be null
    public static MyGroupsFragment newInstance(User u) {
        mUser = u;
        return new MyGroupsFragment();

    }

    /**
     * This method checks if the hosting activity has implemented
     * the OnFragmentInteractionListener interface. If it does not,
     * an exception is thrown.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + getResources().getString(R.string.exception_message));
        }
    }


    @Override
    public void onItemClick(Group group) {
        mSelectedGroup = group;
        exitGroupButton.setVisibility(View.VISIBLE);
        viewGroupButton.setVisibility(View.VISIBLE);
        Log.w("MyGroups", "got group: "+group.getName());
    }


}
