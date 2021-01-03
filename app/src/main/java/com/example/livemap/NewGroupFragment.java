package com.example.livemap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.livemap.objects.Group;
import com.example.livemap.objects.User;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass that shows a question
 * with radio buttons for providing feedback. If the user
 * clicks "Yes" the text header changes to "Article: Like".
 * If the user clicks "No" the text header changes to "Thanks".
 */
public class NewGroupFragment extends Fragment {
    OnFragmentInteractionListener mListener;
    static User mUser;

    public NewGroupFragment() {
        // Required empty public constructor
    }

    interface OnFragmentInteractionListener {
        // this function only returns to caller activity

        void newGroupComplete();
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
        final View rootView = inflater.inflate(R.layout.fragment_new_group,
                container, false);

        final EditText inputName = rootView.findViewById(R.id.group_name);
        final EditText inputDescription = rootView.findViewById(R.id.input_new_marker_desc);
        final Button confirmButton = rootView.findViewById(R.id.button_new_marker_confirm);
        final Button removeButton = rootView.findViewById(R.id.button_new_marker_cancel);


        // do this when confirm is clicked
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = inputName.getText().toString();
                String description = inputDescription.getText().toString();
                Group newGroup = mUser.createGroup(title);
                //
                mUser.getFireFunc().addGroupToFirebase(newGroup);
                mListener.newGroupComplete();
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.newGroupComplete();
            }
        });

        // Return the View for the fragment's UI.
        return rootView;
    }
    // marker can be null
    public static NewGroupFragment newInstance(User u) {
        mUser = u;
        return new NewGroupFragment();

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
