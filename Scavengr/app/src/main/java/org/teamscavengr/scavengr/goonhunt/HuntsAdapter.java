package org.teamscavengr.scavengr.goonhunt;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.User;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by megangebhard on 5/2/15.
 */
public class HuntsAdapter extends RecyclerView.Adapter<HuntsAdapter.ViewHolder> {
    private ArrayList<String> mDataset;
    private ArrayList<Hunt> mCurrentHunts;
    private User mCurrentUser;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTextView = (TextView)v.findViewById(R.id.title);
        }

        @Override
        public void onClick(View v) {
            Log.d("MEGAN", "CALLING ONCLICK SEND HUNT INTENT");
            Intent confirmGoingOnHunt = new Intent(mContext, ConfirmHuntActivity.class);
            confirmGoingOnHunt.putExtra("huntObject", (Parcelable) mCurrentHunts.get(getPosition()));
            confirmGoingOnHunt.putExtra("user", mCurrentUser);
            mContext.startActivity(confirmGoingOnHunt);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HuntsAdapter(Context context, ArrayList<String> myDataset, User currentUser, ArrayList<Hunt> currentHunts ) {
        mDataset = myDataset;
        mCurrentHunts = currentHunts;
        mCurrentUser = currentUser;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HuntsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_hunt, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}