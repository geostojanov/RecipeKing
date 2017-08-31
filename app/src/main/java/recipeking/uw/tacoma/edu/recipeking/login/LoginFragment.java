package recipeking.uw.tacoma.edu.recipeking.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import recipeking.uw.tacoma.edu.recipeking.MainActivity;
import recipeking.uw.tacoma.edu.recipeking.R;
import recipeking.uw.tacoma.edu.recipeking.utils.NetworkUtils;

/**
 * A login Fragment class. This fragment class represents login.
 *
 */
public class LoginFragment extends Fragment {

    /** String constant for the login URL */
    private static final String
            BASE_LOGIN_URL = "https://recipeking.000webhostapp.com/login.php?",
            PARAM_USERNAME = "username", PARAM_PASSWORD = "password";


    /** A EditText for the username field. */
    private AutoCompleteTextView mUsernameView;

    /** A EditText for the password field. */
    private EditText mPasswordView;

    /** A progress bar for logging in. */
    private View mProgressView;

    /** Field for storing the login view. */
    private View mLoginFormView;

    /** TextView for the sign up option. */
    private TextView mSignUpTextView;

    private SharedPreferences mSharedPreferences;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreate() method for this Fragment.
     * @param savedInstanceState - the savedInstanceState for this Fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        if (mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            MainActivity.currentUser = mSharedPreferences.getString(getString(R.string
                    .CURRENT_USER), "");
            lunchMainActivity();
            getActivity().finish();
        }

    }

    /**
     * onCreateView() method for this fragment. Binds the layout views with the class
     * fields, and sets up listener actions for button clicks.
     * @param inflater - the inflater of this Fragment.
     * @param container - the container of this Fragment.
     * @param savedInstanceState - the savedInstanceState of this Fragment.
     * @return - a View object.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) view.findViewById(R.id.username);

        mPasswordView = (EditText) view.findViewById(R.id.password);

        Button mLogInButton = (Button) view.findViewById(R.id.log_in_button);
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.networkConnectionAvailable(getActivity())) {
                    attemptLogin();
                } else {
                    Toast.makeText(getActivity(), "No network connection available.",
                            Toast.LENGTH_SHORT) .show();
                }

            }

        });

        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);

        mSignUpTextView = (TextView) view.findViewById(R.id.sign_up_tv);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment signUpFragment = new SignUpFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.login_fragment_container, signUpFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    /**
     *
     * Helper method to construct the login URL based on the user credentials.
     *
     * @param username The username string based on the EditTextView
     * @param password The password string based on the EditTextView
     * @return returns the login URL
     */
    private static URL buildUrl(String username, String password) {
        Uri builtUri = Uri.parse(BASE_LOGIN_URL).buildUpon()
                .appendQueryParameter(PARAM_USERNAME, username)
                .appendQueryParameter(PARAM_PASSWORD, password)
                .build();

        //Checks if it's a valid URL.
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    /**
     * Attempts to log in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for an empty password, if the user entered one.
        if (password.isEmpty()) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a empty username field.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask();
            /* The resulting URL built for the user attempting login. */
            String mResultLoginUrl = buildUrl(username, password).toString();
            mAuthTask.execute(new String[]{mResultLoginUrl});
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            mSignUpTextView.setVisibility(show ? View.GONE : View.VISIBLE);

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    private class UserLoginTask extends AsyncTask<String, Void, String> {

        /**
         * doInBackground method for this task. Tries to submit GET request to CSS Gate
         * to log in the current user.
         * @param urls - The login URL.
         * @return - 'success' or 'fail'.
         */
        @Override
        protected String doInBackground(String... urls) {
            try {
                // Make the progress spin a little bit longer.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return "";
            }

            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (Exception e) {
                    response = "Unable to connect to the server, Reason: " + e.getMessage();
                }
                finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
            return response;
        }

        /**
         * onPostExecute method for this task. If login was successful it opens MainActivity
         * and passes the user to it.
         * @param result
         */
        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);

            try{
                JSONObject obj = new JSONObject(result);
                String r = obj.getString("result");

                if (r.equals("success")) {
                    //Save the application state as logged in.
                    mSharedPreferences
                            .edit()
                            .putBoolean(getString(R.string.LOGGEDIN), true)
                            .commit();

                    //Save the current user as logged in.
                    mSharedPreferences.edit().putString(getString(R.string.CURRENT_USER),
                            mUsernameView.getText().toString()).commit();

                    //Open MAIN ACTIVITY and pass the USER to it
                    MainActivity.currentUser = mUsernameView.getText().toString();

                    Log.i("LoginFragment", MainActivity.currentUser);

                    lunchMainActivity();
                    getActivity().finish();

                } else {
                    String error = obj.getString("error");
                    if (error.equals("Incorrect username.")) {
                        mUsernameView.setError(getString(R.string.error_incorrect_username));
                        mUsernameView.requestFocus();
                    } else {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Unable to parse data, " +
                        "Reason: " + e
                        .getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void lunchMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }


}
