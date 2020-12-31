package com.example.livemap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livemap.objects.Group;
import com.example.livemap.objects.User;

import java.util.List;

/**
 * A simple {@link Fragment} subclass that shows a question
 * with radio buttons for providing feedback. If the user
 * clicks "Yes" the text header changes to "Article: Like".
 * If the user clicks "No" the text header changes to "Thanks".
 */
public class GroupFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private UserListAdapter mAdapter;

    static OnFragmentInteractionListener mListener;

    static Group mGroup;
    static User mUser;

    public GroupFragment() {
        // Required empty public constructor
    }

    interface OnFragmentInteractionListener {
        // this function only returns to caller activity
        void groupFragmentComplete();
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

        // Inflate the layout for this fragment.
        final View rootView = inflater.inflate(R.layout.fragment_group,
                container, false);

        // create a remove user button that will only be visible when a user is selected
        Button removeUserButton = rootView.findViewById(R.id.remove_button_group_fragment);
        TextView titleText = rootView.findViewById(R.id.group_title_text);
        titleText.setText(mGroup.getName());
        removeUserButton.setVisibility(View.INVISIBLE);

        // Get a handle to the RecyclerView.
        mRecyclerView = rootView.findViewById(R.id.users_recycler_view);
        // Create an adapter and supply the data to be displayed.
        mAdapter = new UserListAdapter(getContext(), mGroup,removeUserButton);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final Button closeButton = rootView.findViewById(R.id.close_button_group_fragment);



        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.groupFragmentComplete();
            }

        });

        // Return the View for the fragment's UI.
        return rootView;
    }

    public static GroupFragment newInstance(User u, Group g) {

        mGroup = g;
        mUser = u;
        return new GroupFragment();

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
