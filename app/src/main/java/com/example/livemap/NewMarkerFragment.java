package com.example.livemap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.example.livemap.objects.MarkerLive;

/**
 * A simple {@link Fragment} subclass that shows a question
 * with radio buttons for providing feedback. If the user
 * clicks "Yes" the text header changes to "Article: Like".
 * If the user clicks "No" the text header changes to "Thanks".
 */
public class NewMarkerFragment extends Fragment {
    static MarkerLive markerLive;
    OnFragmentInteractionListener mListener;

    public NewMarkerFragment() {
        // Required empty public constructor
    }

    interface OnFragmentInteractionListener {
        // this function only returns to caller activity
        void newMarkerFragmentCreate(MarkerLive ml);
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

        // Inflate the layout for this fragment.
        final View rootView = inflater.inflate(R.layout.fragment_new_marker,
                container, false);

        final EditText inputMarkerName = rootView.findViewById(R.id.input_new_marker_name);
        final EditText inputMarkerDescription = rootView.findViewById(R.id.input_new_marker_desc);
        final Button confirmButton = rootView.findViewById(R.id.button_new_marker_confirm);
        final Button cancelButton = rootView.findViewById(R.id.button_new_marker_cancel);
        final Switch privateSwitch = rootView.findViewById(R.id.private_switch_create_window);

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
                mListener.newMarkerFragmentCreate(markerLive);
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
    public static NewMarkerFragment newInstance(MarkerLive ml) {
        markerLive = ml;
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
}
