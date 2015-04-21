package org.teamscavengr.scavengr.goonhunt;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.teamscavengr.scavengr.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompletedTaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompletedTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedTaskFragment extends Fragment {
    private static final String CONGRATS_TEXT = "Congrats!";

    private String congratsText;
    //    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param task
     * @return A new instance of fragment CompletedTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompletedTaskFragment newInstance(String congrats) {
        CompletedTaskFragment fragment = new CompletedTaskFragment();
        Bundle args = new Bundle();
        args.putString(CONGRATS_TEXT, congrats);
        fragment.setArguments(args);
        return fragment;
    }

    public CompletedTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            congratsText = getArguments().getString(CONGRATS_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_completed_task, container,false);
        TextView congrats = (TextView) myInflatedView.findViewById(R.id.congrats);
        congrats.setText(congratsText);

        return myInflatedView;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
