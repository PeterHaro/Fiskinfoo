/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authentication;
import retrofit.http.HEAD;

public class User implements Parcelable{
    private static final String PREFS_NAME = "no.sintef.fiskinfo";
    private static final String PREFS_KEY = "user";
    private static final String EXISTS = "userSerialized";

    //TODO: DO ME SAFELY
    private String username;
    private String password;
    //TODO: END
    //TODO: THIS IS STUPID
    private long previousAuthenticationTimeStamp = 0;
    //TODO: END
    private Authentication authentication;
    private String filePathForExternalStorage;
    private List<String> activeLayers;
    private List<String> mySubscriptions;
    private List<String> availableSubscriptions;
    private boolean isAuthenticated; //False == anon user, I.E no permission
    private boolean isFishingFacilityAuthenticated;
    private ToolLog toolLog;
    private Map<String, SubscriptionEntry> subscriptionCache;
    private boolean offlineModeActive = false;
    private UserSettings settings;
    private boolean showToolExplanation = true;

    public User() {
        isAuthenticated = false;
        toolLog = new ToolLog();
        subscriptionCache = new HashMap<>();
    }

    protected User(Parcel in) {
        username = in.readString();
        password = in.readString();
        previousAuthenticationTimeStamp = in.readLong();
        authentication = in.readParcelable(Authentication.class.getClassLoader());
        filePathForExternalStorage = in.readString();
        activeLayers = in.createStringArrayList();
        mySubscriptions = in.createStringArrayList();
        availableSubscriptions = in.createStringArrayList();
        isAuthenticated = in.readByte() != 0;
        isFishingFacilityAuthenticated = in.readByte() != 0;
        toolLog = in.readParcelable(ToolLog.class.getClassLoader());
        settings = in.readParcelable(UserSettings.class.getClassLoader());
        Bundle bundle = in.readBundle();
        bundle.setClassLoader(SubscriptionEntry.class.getClassLoader());
        subscriptionCache = (Map<String, SubscriptionEntry>) bundle.getSerializable("cache");
        offlineModeActive = in.readByte() != 0;
        showToolExplanation = in.readByte() != 0;
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

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setPreviousAuthenticationTimeStamp(long authenticationTimeStamp) {
        previousAuthenticationTimeStamp = authenticationTimeStamp;
    }

    public boolean isTokenValid() {
        return authentication != null && (System.currentTimeMillis() / 1000L) - previousAuthenticationTimeStamp <= (authentication.expires_in + 600);
    }

    public ToolLog getToolLog() {
        return toolLog;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        if (authentication == null) {
            return null;
        } else {
            return authentication.access_token;
        }
    }

    public boolean getIsFishingFacilityAuthenticated() {
        return isFishingFacilityAuthenticated;
    }

    public void setIsFishingFacilityAuthenticated(boolean isAuthenticated) {
        isFishingFacilityAuthenticated = isAuthenticated;
    }

    public String getFilePathForExternalStorage() {
        return filePathForExternalStorage;
    }

    public void setFilePathForExternalStorage(String filePathForExternalStorage) {
        this.filePathForExternalStorage = filePathForExternalStorage;
    }

    public void setActiveLayers(List<String> layers) {
        this.activeLayers = layers;
    }

    public List<String> getActiveLayers() {
        return this.activeLayers;
    }


    @SuppressWarnings("unused")
    public Boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthentication(boolean authentication) {
        isAuthenticated = authentication;
    }

    public static boolean exists(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(EXISTS, false);
    }

    public static void rememberUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(EXISTS, true);
        editor.apply();
    }

    public static void forgetUser(Context context) {
        context.getSharedPreferences(PREFS_NAME, 0).edit().clear().commit();
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
        editor.apply();
    }

    static public User deSerialize(String serializedUser) {
        Gson gson = new Gson();
        return gson.fromJson(serializedUser, User.class);
    }

    public static User readFromSharedPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String serializedDataFromPreference = prefs.getString(PREFS_KEY, null);
        Log.d("User should be: ", serializedDataFromPreference);
        return deSerialize(serializedDataFromPreference);
    }

    @SuppressWarnings("unused")
    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public boolean getOfflineMode() {
        return offlineModeActive;
    }

    public void setOfflineMode(boolean active) {
        offlineModeActive = active;
    }

    public Collection<SubscriptionEntry> getSubscriptionCacheEntries() {
        return subscriptionCache.values();
    }

    public SubscriptionEntry getSubscriptionCacheEntry(String name) {
        return subscriptionCache.get(name);
    }

    public void setSubscriptionCacheEntry(String name, SubscriptionEntry entry) {
        subscriptionCache.put(name, entry);
    }

    public boolean getShowToolExplanation() {
        return this.showToolExplanation;
    }

    public void setShowToolExplanation(boolean showToolExplanation) {
        this.showToolExplanation = showToolExplanation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeLong(previousAuthenticationTimeStamp);
        dest.writeParcelable(authentication, flags);
        dest.writeString(filePathForExternalStorage);
        dest.writeStringList(activeLayers);
        dest.writeStringList(mySubscriptions);
        dest.writeStringList(availableSubscriptions);
        dest.writeByte((byte) (isAuthenticated ? 1 : 0));
        dest.writeByte((byte) (isFishingFacilityAuthenticated ? 1 : 0));
        dest.writeParcelable(toolLog, flags);
        dest.writeParcelable(settings, flags);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cache", (Serializable) subscriptionCache);
        dest.writeBundle(bundle);
        dest.writeByte((byte) (offlineModeActive ? 1 : 0));
        dest.writeByte((byte) (showToolExplanation ? 1 : 0));
    }
}
