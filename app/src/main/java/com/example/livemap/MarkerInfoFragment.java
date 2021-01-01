package com.example.livemap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.livemap.objects.MarkerLive;
import com.google.android.gms.maps.model.Marker;

/**
 * A simple {@link Fragment} subclass that shows a question
 * with radio buttons for providing feedback. If the user
 * clicks "Yes" the text header changes to "Article: Like".
 * If the user clicks "No" the text header changes to "Thanks".
 */
public class MarkerInfoFragment extends Fragment {
    final static int EDIT_MODE = 1;
    final static int VIEW_MODE = 0;
    private boolean changesMade = false;
    private int infoWindowMode = VIEW_MODE;
    static MarkerLive markerLive;
    static Marker marker;
    OnFragmentInteractionListener mListener;
    private String currentTitle;
    private String currentDescription;

    public MarkerInfoFragment() {
        // Required empty public constructor
    }

    interface OnFragmentInteractionListener {
        // this function only returns to caller activity
        void markerInfoCompleteNoChange();
        void markerInfoCompleteChange(MarkerLive ml);
        void markerInfoCompleteDelete(MarkerLive ml);
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
        final View rootView = inflater.inflate(R.layout.fragment_marker_info,
                container, false);

        final TextView titleText = rootView.findViewById(R.id.title_text);
        final TextView descriptionText = rootView.findViewById(R.id.description_text_view_info_window);
        final Button editSaveButton = rootView.findViewById(R.id.bottom_right_button_info_window);
        final Button closeCancelButton = rootView.findViewById(R.id.bottom_left_button_info_window);
        final Button removeButton = rootView.findViewById(R.id.middle_button_info_window);
        final EditText editTitle = rootView.findViewById(R.id.group_search_box);
        final EditText editDescription = rootView.findViewById(R.id.edit_marker_description_info_window);

        currentDescription = markerLive.getMarkerOptions().getSnippet();
        currentTitle = markerLive.getMarkerOptions().getTitle();


        titleText.setText(currentTitle);
        descriptionText.setText(currentDescription);
        editTitle.setText(currentDescription);
        editDescription.setText(currentDescription);

        // these are invisible in view mode, but visible in edit mode
        editTitle.setVisibility(View.INVISIBLE);
        editDescription.setVisibility(View.INVISIBLE);
        removeButton.setVisibility(View.INVISIBLE);


        // in the start the buttons are for close and edit, when in edit this will change
        // to cancel and save
        editSaveButton.setText("Edit");
        closeCancelButton.setText("Close");


        editSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // button has different function based on the fragment's mode
                // if in view mode switch to edit mode
                if(infoWindowMode == VIEW_MODE){
                    infoWindowMode = EDIT_MODE;
                    changesMade = true;

                    editSaveButton.setText("Save");
                    closeCancelButton.setText("Cancel");
                    editTitle.setVisibility(View.VISIBLE);
                    editDescription.setVisibility(View.VISIBLE);
                    removeButton.setVisibility(View.VISIBLE);
                    descriptionText.setVisibility(View.INVISIBLE);
                }
                // if in edit mode then save changes and update
                else{ // EDIT_MODE
                    infoWindowMode = VIEW_MODE;

                    currentTitle = editTitle.getText().toString();
                    currentDescription = editDescription.getText().toString();
                    descriptionText.setText(currentDescription);
                    titleText.setText(currentTitle);

                    editTitle.setVisibility(View.INVISIBLE);
                    editDescription.setVisibility(View.INVISIBLE);
                    descriptionText.setVisibility(View.VISIBLE);
                    removeButton.setVisibility(View.INVISIBLE);
                    editSaveButton.setText("Edit");
                    closeCancelButton.setText("Close");
                }
            }
        });
        closeCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // button has different function based on the fragment's mode
                // if in view mode then close window
                if(infoWindowMode == VIEW_MODE){
                    if(changesMade){
                        markerLive.setTitle(currentTitle);
                        markerLive.setSnippet(currentDescription);
                        markerLive.updateMarker();
                        mListener.markerInfoCompleteChange(markerLive);

                    }
                    else {
                        mListener.markerInfoCompleteNoChange();
                    }
                }
                // if in edit mode then discard changes and return to view mode
                else{ // EDIT_MODE
                    editDescription.setText(currentDescription);
                    editTitle.setText(currentTitle);

                    editTitle.setVisibility(View.INVISIBLE);
                    editDescription.setVisibility(View.INVISIBLE);
                    descriptionText.setVisibility(View.VISIBLE);
                    removeButton.setVisibility(View.INVISIBLE);
                    editSaveButton.setText("Edit");
                    closeCancelButton.setText("Close");
                }

            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.markerInfoCompleteDelete(markerLive);
            }
        });

        // Return the View for the fragment's UI.
        return rootView;
    }
    // marker can be null
    public static MarkerInfoFragment newInstance(MarkerLive ml, Marker m) {
        markerLive = ml;
        return new MarkerInfoFragment();

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
