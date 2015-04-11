package org.teamscavengr.scavengr;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.facebook.*;
import com.facebook.login.widget.ProfilePictureView;


/**
 * Fragment that represents the main selection screen for Scrumptious.
 */
public class SelectionFragment extends Fragment {

    private static final String TAG = "SelectionFragment";

    private ProfilePictureView profilePictureView;
    private TextView greeting;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                updateWithToken(currentAccessToken);
            }
        };
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            profilePictureView.setProfileId(currentAccessToken.getUserId());
            greeting.setText(getString(R.string.hello_user, Profile.getCurrentProfile().getFirstName()));

        } else {
            // Don't know the user
            // TODO(Gebhard): hide the my hunts button
            profilePictureView.setProfileId(null);
            greeting.setText("Welcome");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.selection, container, false);

        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePicture);
        greeting = (TextView) view.findViewById(R.id.greeting);
        profilePictureView.setCropped(true);

        // TODO (gebhard): create a facebook log out system by options of picture click

        updateWithToken(AccessToken.getCurrentAccessToken());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}