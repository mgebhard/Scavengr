package org.teamscavengr.scavengr;

import android.content.Intent;
import android.net.Uri;
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
    private MainActivity activity;

    private Uri photoUri;
    private ImageView photoThumbnail;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
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
//            tokenUpdated(currentAccessToken);
//            profilePictureView.setProfileId(currentAccessToken.getUserId());

        } else {
//            profilePictureView.setProfileId(null);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.selection, container, false);

//        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
//        profilePictureView.setCropped(true);
//        photoThumbnail = (ImageView) view.findViewById(R.id.selected_image);
//
//        profilePictureView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (AccessToken.getCurrentAccessToken() != null) {
//                    activity.showSettingsFragment();
//                } else {
//                    activity.showSplashFragment();
//                }
//            }
//        });

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
        activity = null;
    }
}



//    /**
//     * Notifies that the token has been updated.
//     */
//    private void tokenUpdated(AccessToken currentAccessToken) {
//        if (pendingAnnounce) {
//            Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
//            if (currentAccessToken == null
//                    || !currentAccessToken.getPermissions().contains(PERMISSION)) {
//                pendingAnnounce = false;
//                showRejectedPermissionError();
//                return;
//            }
//            handleAnnounce();
//        }
//