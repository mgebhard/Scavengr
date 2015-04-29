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
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TASK_TEXT = "Clue: The tallest building in Cambridge.";
    private static final String PROGRESS_TEXT = "Task 1 out of 15";

    private String taskText;
    private String progressText;

//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TaskFragment.
     */
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

}
