package org.teamscavengr.scavengr.goonhunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.BitmapUtils;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class HuntRecapActivity extends BaseActivity implements View.OnClickListener {

    private Hunt hunt;
    private User user;
    private ArrayList<String> photoPaths;
    private ImageSwitcher imageSwitcher;
    private int photoIndex = 0;
    private ImageView defaultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_recap);
        defaultImage = (ImageView) findViewById(R.id.photoFromTask);
        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.map_filler_pic, 310 , 210);
        defaultImage.setImageBitmap(bitmap);
        imageSwitcher = (ImageSwitcher)findViewById(R.id.imageSwitcher);
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(GridLayout.LayoutParams.
                        FILL_PARENT, GridLayout.LayoutParams.FILL_PARENT));
                return myView;
            }

        });

        Log.d("HuntRecap", "Found hunt obj");
        hunt =  getIntent().getParcelableExtra("hunt");

        user = getIntent().getParcelableExtra("user");
        if (getIntent().hasExtra("photoPaths")) {
            photoPaths = getIntent().getStringArrayListExtra("photoPaths");
            Log.d("MEGAN", "GOT PHOTOS: " + photoPaths.toString());
            if (photoPaths.size() > 0) {
                setPic(photoPaths.get(photoIndex));
            }

        }

        String waypointText = "";
        for (Task task: hunt.getTasks()){
            waypointText += task.getAnswer() + "\n";
        }
        TextView waypoints = (TextView) findViewById(R.id.waypoints);
        waypoints.setText(waypointText);

    }

    private void setPic(String mCurrentPhotoPath) {
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        try {
            ImageView mImageView = (ImageView) findViewById(R.id.photoFromTask);
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            /* Figure out which way needs to be reduced less */
            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            }

            /* Set bitmap options to scale the image decode target */
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

//            /* Decode the JPEG file into a Bitmap */
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            /* Associate the Bitmap to the ImageView */
            mImageView.setImageBitmap(bitmap);

//            TODO(GEBHARD): LOOK INTO MEMORY MANAGEMENT
            imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), mCurrentPhotoPath));
            Log.d("MEGAN", "SETTING DRAWABLE");
        } catch (OutOfMemoryError e) {
            Log.d("MEGAN", "OUT OF MEMORY: " + e);
        }
    }

    public void next(View view){
        Toast.makeText(getApplicationContext(), "Next Image",
                Toast.LENGTH_LONG).show();
        if (photoIndex < photoPaths.size() - 1) {
            photoIndex++;
            setPic(photoPaths.get(photoIndex));
        }
    }
    public void previous(View view){
        Toast.makeText(getApplicationContext(), "previous Image",
                Toast.LENGTH_LONG).show();

        if (photoIndex > 0) {
            photoIndex--;
            setPic(photoPaths.get(photoIndex));
        }
    }

    public static int getCameraPhotoOrientation(String imagePath){
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("MEGAN", "ROTATE: " + rotate);
        return rotate;
    }

    @Override
    public void onTrimMemory(int trimLevel) {
        if (trimLevel == TRIM_MEMORY_COMPLETE || trimLevel == TRIM_MEMORY_MODERATE) {
            if (defaultImage != null) {
                defaultImage.setImageBitmap(null);
            }
        }
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.review:
                if (user == null) {
                    Toast.makeText(this, "You must be logged in to review a hunt", Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent review = new Intent(this, RateHuntActivity.class);
                review.putExtra("user", user);
                review.putExtra("hunt", (Parcelable) hunt);
                this.startActivity(review);
                break;

            case R.id.home:
                Intent createHuntIntent = new Intent(this, MainActivity.class);
                finish();
                createHuntIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(createHuntIntent);
                break;
            default:
                break;
        }

    }
}
