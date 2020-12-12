package com.example.livemap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.Marker;

/**
 * A simple {@link Fragment} subclass that shows a question
 * with radio buttons for providing feedback. If the user
 * clicks "Yes" the text header changes to "Article: Like".
 * If the user clicks "No" the text header changes to "Thanks".
 */
public class CustomizeMarkerFragment extends Fragment {
    static Marker marker;
    OnFragmentInteractionListener mListener;

    public CustomizeMarkerFragment() {
        // Required empty public constructor
    }

    interface OnFragmentInteractionListener {
        // this function only returns to caller activity
        void customizeMarkerComplete();
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
        final View rootView = inflater.inflate(R.layout.fragment_customize_marker,
                container, false);
        final EditText inputMarkerName = rootView.findViewById(R.id.input_marker_name);
        final EditText inputMarkerDescription = rootView.findViewById(R.id.input_marker_description);

        final Button confirmButton = rootView.findViewById(R.id.confirm_button);
        final Button removeButton = rootView.findViewById(R.id.remove_button);

        // do this when confirm is clicked
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = inputMarkerName.getText().toString();
                String description = inputMarkerDescription.getText().toString();
                marker.setTitle(title);
                marker.setSnippet(description);
                mListener.customizeMarkerComplete();
            }
        });
        // do this when remove is clicked
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marker.remove();
                mListener.customizeMarkerComplete();

            }
        });

        // Return the View for the fragment's UI.
        return rootView;
    }
    // marker can be null
    public static CustomizeMarkerFragment newInstance(Marker m) {
        marker = m;
        return new CustomizeMarkerFragment();

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
