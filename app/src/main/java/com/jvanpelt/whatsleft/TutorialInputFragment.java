package com.jvanpelt.whatsleft;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TutorialInputFragment.OnTutorialInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TutorialInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialInputFragment extends Fragment {
    private String TAG = "TutorialInputFragment";
    private OnTutorialInteractionListener mListener;
    private View view;
    private TransactionDbHelper dbHelper;
    public int ViewNum = 1;
    private final String ARG_PARAM1 = "ViewNum";

    public TutorialInputFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TutorialInputFragment.
     */
    public TutorialInputFragment newInstance(int val) {
        TutorialInputFragment fragment = new TutorialInputFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, val);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ViewNum = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (ViewNum == 1) {
            view = inflater.inflate(R.layout.fragment_tutorial_input, container, false);
        }
        else if (ViewNum == 2) {
            view = inflater.inflate(R.layout.fragment_tutorial_edit, container, false);
        }
        else if (ViewNum == 3) {
            view = inflater.inflate(R.layout.fragment_tutorial_review, container, false);
        }
        else {
            view = inflater.inflate(R.layout.fragment_tutorial_whatsleft, container, false);
        }

        try {
            dbHelper = new TransactionDbHelper(this.getContext());
            Button disable = (Button) view.findViewById(R.id.btnDisable);
            disable.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dbHelper.UpdateTutorial(view.getContext(), false);
                    Toast.makeText(view.getContext(), "Tutorial Disabled",
                            Toast.LENGTH_LONG).show();
                }
            });
            Button got_it = (Button) view.findViewById(R.id.btnOK);
            got_it.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onOKButtonPressed();
                }
            });
        }
        catch (Exception e) {
            Log.e(TAG, "Error", e);
            Toast.makeText(this.getContext(), "Error: " + e,
                    Toast.LENGTH_LONG).show();
        }

        return view;
    }

    public void onOKButtonPressed() {
        if (mListener != null) {
            mListener.onTutorialInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTutorialInteractionListener) {
            mListener = (OnTutorialInteractionListener) context;
        } else {
            Toast.makeText(this.getContext(), "no OnTutorialInteractionListener",
                    Toast.LENGTH_LONG).show();
            //throw new RuntimeException(context.toString()
            //        + " must implement OnTutorialInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTutorialInteractionListener {
        void onTutorialInteraction();
    }
}
