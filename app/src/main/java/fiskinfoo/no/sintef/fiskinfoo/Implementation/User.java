package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.List;

public class User implements Parcelable{
    private static final String PREFS_NAME = "no.sintef.fiskinfo";
    private static final String PREFS_KEY = "user";

    //TODO: DO ME SAFELY
    private String username;
    private String password;
    //TODO: END
    private String token;
    private List<String> mySubscriptions;
    private List<String> availableSubscriptions;
    private Boolean isAuthenticated; //False == anon user, I.E no permission

    public User() {
        isAuthenticated = false;
    }

    public Boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthentication(boolean authentication) {
        isAuthenticated = authentication;
    }

    protected User(Parcel in) {
        username = in.readString();
        password = in.readString();
        token = in.readString();
        mySubscriptions = in.createStringArrayList();
        availableSubscriptions = in.createStringArrayList();
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void writeToSharedPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String serializedData = serialize();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_KEY, serializedData);
        editor.commit();
    }

    static public User deSerialize(String serializedUser) {
        Gson gson = new Gson();
        return gson.fromJson(serializedUser, User.class);
    }

    public User readFromSharedPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String serializedDataFromPreference = prefs.getString(PREFS_KEY, null);
        return deSerialize(serializedDataFromPreference);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(token);
        dest.writeStringList(mySubscriptions);
        dest.writeStringList(availableSubscriptions);
    }
}
