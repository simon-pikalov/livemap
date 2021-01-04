package com.example.livemap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.example.livemap.objects.Group;
import com.example.livemap.objects.MarkerLive;
import com.example.livemap.objects.User;
import com.example.livemap.utils.MarkerOwner;

import java.lang.reflect.Array;
import java.util.List;

/**
 * A simple {@link Fragment} subclass that shows a question
 * with radio buttons for providing feedback. If the user
 * clicks "Yes" the text header changes to "Article: Like".
 * If the user clicks "No" the text header changes to "Thanks".
 */
public class NewMarkerFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    static MarkerLive markerLive;
    static User mUser;
    private List<Group> mGroups;
    private MarkerOwner ownerOfMarker;

    OnFragmentInteractionListener mListener;

    public NewMarkerFragment() {
        // Required empty public constructor
    }




    interface OnFragmentInteractionListener {
        // this function only returns to caller activity
        void newMarkerFragmentCreate(MarkerLive ml, String ownerId);
        void newMarkerFragmentCancel();
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
        // this user is the default owner
        ownerOfMarker = mUser;
        // Inflate the layout for this fragment.
        final View rootView = inflater.inflate(R.layout.fragment_new_marker,
                container, false);

        final EditText inputMarkerName = rootView.findViewById(R.id.input_new_marker_name);
        final EditText inputMarkerDescription = rootView.findViewById(R.id.input_new_marker_desc);
        final Button confirmButton = rootView.findViewById(R.id.button_new_marker_confirm);
        final Button cancelButton = rootView.findViewById(R.id.button_new_marker_cancel);
        final Switch privateSwitch = rootView.findViewById(R.id.private_switch_create_window);

        // creating spinner start
        final Spinner selectOwnerSpinner = rootView.findViewById(R.id.owner_select_spinner_new_marker);
        selectOwnerSpinner.setOnItemSelectedListener(this);
        mGroups = mUser.getGroups();
        String[] spinnerOptions = new String[mGroups.size()+1];
        spinnerOptions[0]=mUser.getName();
        for(int i=0; i<mGroups.size();++i){
            spinnerOptions[i+1]=mGroups.get(i).getName();
        }


        ArrayAdapter spinnerAdapter = new ArrayAdapter(getContext(),
                            android.R.layout.simple_dropdown_item_1line, spinnerOptions);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectOwnerSpinner.setAdapter(spinnerAdapter);

        // creating spinner end


        privateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    markerLive.setPublic(false);
                } else {
                    // The toggle is disabled
                    markerLive.setPublic(true);
                }
            }
        });
        // do this when confirm is clicked
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = inputMarkerName.getText().toString();
                String description = inputMarkerDescription.getText().toString();
                Log.w("NewMarkerFragment", "got title: "+title+" and description: "+ description);
                markerLive.setTitle(title);
                markerLive.setSnippet(description);
                markerLive.setOwnerId(ownerOfMarker.getId());
                mListener.newMarkerFragmentCreate(markerLive, ownerOfMarker.getId());
            }
        });
        // when cancel is clicked the customized markerLive is returned null
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markerLive = null;
                mListener.newMarkerFragmentCancel();
            }
        });

        // Return the View for the fragment's UI.
        return rootView;
    }
    // marker can be null
    public static NewMarkerFragment newInstance(MarkerLive ml, User user) {
        markerLive = ml;
        mUser = user;
        return new NewMarkerFragment();

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

    // methods for spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i==0){
            ownerOfMarker=mUser;
        }
        else{

            ownerOfMarker = mGroups.get(i-1);
        }
        Log.w("NewMarkerFrag", "selected: "+ownerOfMarker.getName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        ownerOfMarker=mUser;
    }
}
