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

package fiskinfoo.no.sintef.fiskinfoo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authentication;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private static final String TAG = "LoginActivity::";
    private UserLoginTask mAuthTask = null;
    private User user;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mErrorTextView;
    private BarentswatchApi barentswatchApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mErrorTextView = (TextView) findViewById(R.id.login_error_text_field);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_sign_in_edit_text);
        mPasswordView = (EditText) findViewById(R.id.password_sign_in_edit_text);
        barentswatchApi = new BarentswatchApi();

        //Skip login form if the user requested it
        user = new User();

        loginUserIfStored();

        // Set up the login form.
        populateAutoComplete();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView forgotPasswordTextView = (TextView) findViewById(R.id.forgotten_password_text_view);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: recover password
            }
        });

        TextView signUpTextView = (TextView) findViewById(R.id.sign_up_text_view);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: create dialog to create new user
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if(!new FiskInfoUtility().isNetworkAvailable(getBaseContext())) {
            toggleNetworkErrorText(false);
        }
    }

    private void loginUserIfStored() {
        if (!User.exists(this)) {
            Log.d(TAG, "Could not find user");
            return;
        }

        user = User.readFromSharedPref(this);

        if(!user.isTokenValid()) {
            LoginAuthenticationUserTask loginTask = new LoginAuthenticationUserTask(user);
            loginTask.execute((Void) null);
        } else {
            Log.d(TAG, "Token valid");
        }
        changeActivity(MainActivity.class, user);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if(!(new FiskInfoUtility().isNetworkAvailable(this))) {
            toggleNetworkErrorText(true);
            return;
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } /*else if (!new FiskInfoUtility().isEmailValid(email)) {
            mEmailView.setError(getProperty(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    public void toggleNetworkErrorText(boolean networkAvailable) {
        if(networkAvailable) {
            mErrorTextView.setVisibility(View.GONE);
        } else {
            mErrorTextView.setText(R.string.no_internet_access);
            mErrorTextView.setTextColor(getResources().getColor(R.color.error_red));
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This function checks the passwords validity
     * @param password
     *  The password must be greater than 6, ensure compat with bw
     * @return
     *  Whether the password is valid or not
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 7;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[] { ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = { ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY, };

        int ADDRESS = 0;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        // Create adapter to tell the AutoCompleteTextView what to show in its
        // dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private final User user = new User();
        private final AtomicReference<Authentication> authenticationResponse = new AtomicReference<>();
        private final CheckBox storeUserToDisk = (CheckBox) findViewById(R.id.login_checkbox);
        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient mClient = new OkHttpClient();
                Response response = mClient.newCall(barentswatchApi.getRequestForAuthentication(mEmail, mPassword)).execute();
                if(response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    response = mClient.newCall(barentswatchApi.getRequestForAuthenticationClientCredentialsFlow(mEmail, mPassword)).execute();
                }

                Gson gson = new Gson();
                Authentication auth = gson.fromJson(response.body().charStream(), Authentication.class);
                authenticationResponse.set(auth);
                return auth.access_token != null;
            } catch (Exception e) {
                Log.d(TAG, "Exception occurred when trying to login to barentswatch: " + e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                user.setAuthentication(true);
                user.setUsername(mEmail);
                user.setPassword(mPassword);
                user.setAuthentication(authenticationResponse.get());
                user.setPreviousAuthenticationTimeStamp((System.currentTimeMillis() / 1000L));
                if(user.getActiveLayers() == null) {
                    List<String> activeLayers = new ArrayList<>();
                    activeLayers.add(getString(R.string.fishing_facility_name));
                    user.setActiveLayers(activeLayers);
                }
                if(storeUserToDisk.isChecked()) {
                    User.rememberUser(LoginActivity.this);
                    user.writeToSharedPref(LoginActivity.this);
                } else {
                    User.forgetUser(LoginActivity.this);
                }
                changeActivity(MainActivity.class, user);

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class LoginAuthenticationUserTask extends AsyncTask<Void, Void, Boolean> {
        User user;
        private final AtomicReference<Authentication> authenticationResponse = new AtomicReference<>();
        public LoginAuthenticationUserTask(User user) {
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient mClient = new OkHttpClient();
                Response response = mClient.newCall(barentswatchApi.getRequestForAuthentication(user.getUsername(), user.getPassword())).execute();
                if(response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    response = mClient.newCall(barentswatchApi.getRequestForAuthenticationClientCredentialsFlow(user.getUsername(), user.getPassword())).execute();
                }

                Gson gson = new Gson();
                Authentication auth = gson.fromJson(response.body().charStream(), Authentication.class);
                authenticationResponse.set(auth);
                return auth.access_token != null;
            } catch (Exception e) {
                Log.d(TAG, "Exception occurred when trying to login to barentswatch: " + e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                user.setAuthentication(authenticationResponse.get());
                user.setPreviousAuthenticationTimeStamp((System.currentTimeMillis() / 1000L));
                User.rememberUser((LoginActivity.this));
                user.writeToSharedPref(LoginActivity.this);
                changeActivity(MainActivity.class, user);

            } else {
                Log.d(TAG, "Something went amiss while reauthing user");
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    /**
     * Represents an asynchronous registration task used to register the user.
     * TODO: attempt to create a user through network service.
     */
    @SuppressWarnings("unused")
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mboatID;

        UserRegisterTask(String email, String password, String boatID) {
            mEmail = email;
            mPassword = password;
            mboatID = boatID;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // if(successfulAuthenticationWithServer) {
                // return true;
                // } else {
                return false;
                // }

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                // user successfully created
                changeActivity(MainActivity.class);

            } else {
                // TODO: inform user that account creation failed.
                Toast.makeText(getBaseContext(), getString(R.string.error_account_creation_failed), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous registration task used to register the user.
     * TODO: attempt to recover password
     */
    @SuppressWarnings("unused")
    public class recoverPasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;

        recoverPasswordTask(String email) {
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // if(successfulAuthenticationWithServer) {
                // return true;
                // } else {
                return false;
                // }

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                // password reset request successful
                changeActivity(MainActivity.class);

            } else {
                // failed to request password reset.
                // TODO: give user feedback that reset request failed
                Toast.makeText(getBaseContext(), getString(R.string.error_password_reset_failed), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void changeActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private void changeActivity(Class<?> activityClass, User mUser) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("user", mUser);
        startActivity(intent);
        finish();
    }

}
