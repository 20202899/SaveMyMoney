package br.com.subsavecoins.savemymoney.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Search implements Parcelable {
    public Data data;

    protected Search(Parcel in) {
    }

    public static final Creator<Search> CREATOR = new Creator<Search>() {
        @Override
        public Search createFromParcel(Parcel in) {
            return new Search(in);
        }

        @Override
        public Search[] newArray(int size) {
            return new Search[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
