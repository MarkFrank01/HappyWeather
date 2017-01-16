package com.wjc.happyweather.modules.city.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Package_NAME : com.wjc.happyweather.modules.city.domain
 * File_NAME : City
 * Created by WJC on 2017/12/18 16:26
 * Describe : TODO
 */

public class City implements Parcelable {

    public String mCityName;
    public int mProID;
    public int mCitySort;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mCityName);
        dest.writeInt(this.mProID);
        dest.writeInt(this.mCitySort);
    }

    public City(){

    }

    protected City(Parcel in){
        this.mCityName = in.readString();
        this.mProID = in.readInt();
        this.mCitySort = in.readInt();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
