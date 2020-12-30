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
public class MyGroupsFragment extends Fragment {
    final static int VIEW_MODE = 1;
    final static int SELECT_MODE = 0;
    private RecyclerView mRecyclerView;
    private GroupListAdapter mAdapter;

    OnFragmentInteractionListener mListener;


    static User mUser;
    private int windowMode = VIEW_MODE;

    public MyGroupsFragment() {
        // Required empty public constructor
    }

    interface OnFragmentInteractionListener {
        // this function only returns to caller activity
        void myGroupsFragmentComplete();
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

        final List<Group> groups = mUser.getGroups();
        Log.w("MyGroupsFragment", "size of user's groups is: "+groups.size());
        // Inflate the layout for this fragment.
        final View rootView = inflater.inflate(R.layout.fragment_my_groups,
                container, false);

        // Get a handle to the RecyclerView.
        mRecyclerView = rootView.findViewById(R.id.my_groups_list);
        // Create an adapter and supply the data to be displayed.
        mAdapter = new GroupListAdapter(getContext(), groups);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final Button viewGroupButton = rootView.findViewById(R.id.bottom_right_button_info_window);
        final Button closeButton = rootView.findViewById(R.id.bottom_left_button_info_window);

        // button only visible when some group is selected
        viewGroupButton.setVisibility(View.INVISIBLE);




        // in the start the buttons are for close and edit, when in edit this will change
        // to cancel and save
        viewGroupButton.setText("View");
        closeButton.setText("Close");


        viewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


}
