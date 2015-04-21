package org.teamscavengr.scavengr.goonhunt;

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
 * {@link TaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TASK_TEXT = "Clue: The tallest building in Cambridge.";
    private static final String PROGRESS_TEXT = "Task 1 out of 15";

    // TODO: Rename and change types of parameters
    private String taskText;
    private String progressText;

//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskFragment newInstance(String task, String prog) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(TASK_TEXT, task);
        args.putString(PROGRESS_TEXT, prog);
        fragment.setArguments(args);
        return fragment;
    }

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskText = getArguments().getString(TASK_TEXT);
            progressText = getArguments().getString(PROGRESS_TEXT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_task, container,false);

        // Set the Text to try this out
        TextView task = (TextView) myInflatedView.findViewById(R.id.taskText);
        TextView progress= (TextView) myInflatedView.findViewById(R.id.progressText);

        task.setText(taskText);
        progress.setText(progressText);

        return myInflatedView;
    }
/*
    @Override
    public void onStart(){
        //update texts

    }

    @Override
    public void onResume() {
        super.onResume();
        TextView task = (TextView) getView().findViewById(R.id.taskText);
        TextView progress= (TextView) getView().findViewById(R.id.progressText);

        task.setText(taskText);
        progress.setText(progressText);
    }

    @Override
    public void onPause() {
        super.onPause();
        TextView task = (TextView) getView().findViewById(R.id.taskText);
        TextView progress= (TextView) getView().findViewById(R.id.progressText);

        task.setText(taskText);
        progress.setText(progressText);
    }

    @Override
    public void onStop() {
        Log.d("a;dfjkls", "App stopped");

        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d("as;dfjkl", "App destoryed");

        super.onDestroy();
    } */

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
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

}
