package com.bhaimadadchahiye.club.Answers;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable{
    public int id;
    public String title;
    public String email;
    public String body;
    public int imageId;

    public Question(int id, String title, String email, String body, int imageId) {
        this.title = title;
        this.id = id;
        this.email = email;
        this.body = body;
        this.imageId = imageId;
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeInt(imageId);
    }

    public Question(Parcel source) {
        id = source.readInt();
        email = source.readString();
        title = source.readString();
        body = source.readString();
        imageId = source.readInt();
    }
}