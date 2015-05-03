package org.teamscavengr.scavengr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by megangebhard on 4/10/15.
 */
public class LoginFragment extends Fragment {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private View view;
    public static ImageView backgroundImage;

    public interface SkipLoginCallback {
        void onSkipLoginPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login, container, false);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        backgroundImage = (ImageView) view.findViewById(R.id.backgroundImage);
        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.city_background, 100, 100);
        backgroundImage.setImageBitmap(bitmap);
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                MainActivity.loggedInSuccess = true;
                FragmentTransaction transaction = MainActivity.fm.beginTransaction();
                transaction.show(MainActivity.fragments[MainActivity.SELECTION]);
                transaction.hide(MainActivity.fragments[MainActivity.LOGIN]);
                transaction.commit();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "Login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getActivity(), "Login error", Toast.LENGTH_SHORT).show();
            }
        });

//        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.city_background, 100, 100);
//        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.city_background);


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (backgroundImage != null) {
//            backgroundImage.setImageBitmap(null);
//        }
//        getActivity().getWindow().setBackgroundDrawableResource(-1);
        onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
