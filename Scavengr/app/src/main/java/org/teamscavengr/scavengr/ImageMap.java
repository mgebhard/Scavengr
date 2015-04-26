package org.teamscavengr.scavengr;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by erosales on 4/21/15.
 */
public class ImageMap implements Parcelable{
    protected Map<Task, Bitmap> images;


    public ImageMap(Parcel in) {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {

        // TODO: Pending photo recap.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO: Pending photo recap.
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ImageMap createFromParcel(Parcel in) {
            return new ImageMap(in);
        }

        public ImageMap[] newArray(int size) {
            return new ImageMap[size];
        }
    };
}
