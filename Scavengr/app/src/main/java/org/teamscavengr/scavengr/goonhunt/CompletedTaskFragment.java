package org.teamscavengr.scavengr.goonhunt;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;


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
     * @return A new instance of fragment CompletedTaskFragment.
     */
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
        Task task = (Task) getArguments().get("task");
        congratsText = "Congrats! You found: " + task.getAnswer();
        this.onStart();
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
