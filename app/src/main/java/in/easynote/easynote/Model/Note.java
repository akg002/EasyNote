package in.easynote.easynote.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by AKG002 on 12-02-2016.
 */
public class Note implements Parcelable{
    public String title,noteText,timestamp;
    public int uploadedToServer = 0;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    public Note(String title,String noteText,String timestamp){
        this.noteText = noteText;
        this.timestamp = timestamp;
        this.title = title;
    }
    public Note(){}
    public void setUploadedToServer(int x){
        this.uploadedToServer = x;
    }
    public Note(Parcel in){
        title = in.readString();
        noteText = in.readString();
        timestamp = in.readString();
    }

    public static final Creator<Note> CREATOR = new Parcelable.Creator<Note>(){
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(noteText);
        dest.writeString(timestamp);
    }
}
