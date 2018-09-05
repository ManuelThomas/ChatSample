package co.technisupport.chat4saggic;

import android.net.Uri;

/**
 * Created by manuelthomas on 2/6/18.
 */

public class MessageObject {

    private String text;
    private String dateTime;
    private int userId = 0;
    private Uri mediaUri;


    public MessageObject(int userId, String text, String dateTime){
        this.userId = userId;
        this.text = text;
        this.dateTime = dateTime;
    }

    public MessageObject(int userId, String text, String dateTime, Uri mediaUri){
        this.userId = userId;
        this.text = text;
        this.dateTime = dateTime;
        this.mediaUri = mediaUri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Uri getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }
}
