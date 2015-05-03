package org.teamscavengr.scavengr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.ProfilePictureView;

import java.util.List;


/**
 * Fragment that represents the main selection screen for Scrumptious.
 */
public class SelectionFragment extends Fragment {

    public static ProfilePictureView profilePictureView;
    private TextView greeting;

//    private AccessTokenTracker accessTokenTracker;
    private User user;
    public static ImageView profileBanner;
    private static ProfileTracker profileTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile,
                                                       Profile currentProfile) {
//                updateWithToken(currentAccessToken);
                updateWithProfile(currentProfile);
                Log.d("AccessToken", "Updating current access token");
            }
        };

    }

    public void updateWithProfile(final Profile currentProfile) {
        if (currentProfile != null) {
            profilePictureView.setProfileId(currentProfile.getId());
            // Check to see if we need to create a new user
            User.findUserWithFacebookIdInBackground(currentProfile.getId(),
                    new User.FacebookLookupDoneCallback() {
                        @Override
                        public void usersFound(final List<String> ids) {
                            if(ids.size() > 0) {
                                // User already exists, take the 1st one.
                                setCurrentUserFromId(ids.get(0));
                            } else {
                                if (!MainActivity.waitingLogin) {
                                    // User not in db yet, add it
                                    MainActivity.waitingLogin = true;
                                    createAndSaveUser(currentProfile);

                                }
                            }
                        }

                        @Override
                        public void usersFailedToFind(final Exception ex) {
//                            Toast.makeText(SelectionFragment.this.getActivity(),
//                                    "Couldn't load users!", Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }
                    }, true);
            greeting.setText(getString(R.string.hello_user,
                    Profile.getCurrentProfile().getFirstName(),
                    Profile.getCurrentProfile().getLastName()));
        }
    }

//    private void updateWithToken(AccessToken currentAccessToken) {
//        if (currentAccessToken != null && Profile.getCurrentProfile() != null) {
//            profilePictureView.setProfileId(currentAccessToken.getUserId());
//            // Check to see if we need to create a new user
//            User.findUserWithFacebookIdInBackground(Profile.getCurrentProfile().getId(),
//                    new User.FacebookLookupDoneCallback() {
//                        @Override
//                        public void usersFound(final List<String> ids) {
//                            if(ids.size() > 0) {
//                                // User already exists, take the 1st one.
//                                setCurrentUserFromId(ids.get(0));
//                            } else {
//                                // User not in db yet, add it
//                                createAndSaveUser();
//                            }
//                        }
//
//                        @Override
//                        public void usersFailedToFind(final Exception ex) {
//                            Toast.makeText(SelectionFragment.this.getActivity(),
//                                    "Couldn't load users!", Toast.LENGTH_SHORT).show();
//                            ex.printStackTrace();
//                        }
//                    }, true);
//            greeting.setText(getString(R.string.hello_user, Profile.getCurrentProfile().getFirstName()));
//
//        } else {
//            // Don't know the user
//            // TODO(Gebhard): hide the my hunts button
//            profilePictureView.setProfileId(null);
//            greeting.setText("Welcome");
//
//        }
//    }

    private void setCurrentUserFromId(String id) {
        User.loadUserInBackground(id, new User.UserLoadedCallback() {
            @Override
            public void userLoaded(final User user) {
                SelectionFragment.this.user = user;
            }

            @Override
            public void userFailedToLoad(final Exception ex) {
//                Toast.makeText(SelectionFragment.this.getActivity(),
//                        "Couldn't load user!", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        }, true);
    }

    private void createAndSaveUser(Profile currentProfile) {
//        Profile fbProfile = currentProfile;
        user = new User(null, currentProfile.getName(),
                Optional.<String>empty(), Optional.of(currentProfile.getId()));
        user.saveUserInBackground(new User.UserSavedCallback() {
            @Override
            public void userSaved() {
                // HOORAY
                Toast.makeText(getActivity(), "User created", Toast.LENGTH_SHORT).show();
                MainActivity.userSaved(user);
            }

            @Override
            public void userFailedToSave(final Exception ex) {
                Toast.makeText(getActivity(), "Could not create new user", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
                getActivity().finish();
            }

        }, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.selection, container, false);
        profileBanner = (ImageView) view.findViewById(R.id.profileBannerImage);
        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.city_background, 100, 50);
//        profileBanner.setImageBitmap(bitmap);
        profileBanner.setBackground(new BitmapDrawable(bitmap));
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePicture);
        greeting = (TextView) view.findViewById(R.id.greeting);
        profilePictureView.setCropped(true);

        // TODO (gebhard): create a facebook log out system by options of picture click

//        updateWithToken(AccessToken.getCurrentAccessToken());

        updateWithProfile(Profile.getCurrentProfile());
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
        profileTracker.stopTracking();
    }
}