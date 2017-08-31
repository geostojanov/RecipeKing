package recipeking.uw.tacoma.edu.recipeking.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import recipeking.uw.tacoma.edu.recipeking.R;

/**
 * A Sign Up Fragment class. This fragment class represents sign up (registration)
 * for new users.
 *
 */
public class SignUpFragment extends Fragment {

    /** String constant for the base URL used for adding new users. */
    private static final String ADD_USER_URL =
            "https://recipeking.000webhostapp.com/adduser.php?";

    /** EditText for the new username. */
    private EditText mUsername;

    /** EditText for the new password. */
    private EditText mPassword;

    /** Sign up task. */
    private AddUserTask mAddUserTask;

    /**
     *  Required empty public constructor
     */
    public SignUpFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpFragment.
     */
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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
        if (getArguments() != null) {

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
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mUsername = (EditText) view.findViewById(R.id.username_signup);
        mPassword = (EditText) view.findViewById(R.id.password_signup);
        Button signUp = (Button) view.findViewById(R.id.button_signup);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        return view;
    }

    /**
     * Attempts to sign up the account specified by the sign up form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual sign up attempt is made.
     */
    private void attemptSignUp() {

        mUsername.setError(null);
        mPassword.setError(null);

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (password.isEmpty() || !isPasswordValid(password)) {
            if(password.isEmpty()) {
                mPassword.setError(getString(R.string.error_field_required));
                Toast.makeText(getActivity(), "Password field is required",
                        Toast.LENGTH_SHORT).show();
            } else {
                mPassword.setError(getString(R.string.error_invalid_password));
                Toast.makeText(getActivity(),
                        "This password is too short. Enter at least 6 characters",
                        Toast.LENGTH_SHORT).show();
            }


            focusView = mPassword;
            cancel = true;
        }

        // Check for a empty or invalid username field.
        if (username.isEmpty() || isUsernameInvalid(username)) {
            if(username.isEmpty()) {
                mUsername.setError(getString(R.string.error_field_required));
                Toast.makeText(getActivity(), "Username field is required",
                        Toast.LENGTH_SHORT).show();
            } else {
                mUsername.setError(getString(R.string.sign_up_username_invalid));
                Toast.makeText(getActivity(),
                        "Username cannot have space characters and must be less than 20 characters.",
                        Toast.LENGTH_SHORT).show();
            }

            focusView = mUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt sign up and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String url = buildAddUserUrl(username, password);
            mAddUserTask = new AddUserTask();
            mAddUserTask.execute(new String[]{url});
        }

    }

    /**
     * Checks if the username is invalid
     * @param username - the username to be checked.
     * @return - true if username is invalid, false otherwise.
     */
    private boolean isUsernameInvalid(String username) {
        return username.contains(" ") || username.length() > 20;
    }

    /**
     * Checks if the password is valid
     * @param password - the password to be checked.
     * @return - true if password is valid, false otherwise.
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /**
     * Builds the URL for adding a new user to the CSS gate server.
     *
     * @param username - the username to be added.
     * @param password - the password to be added.
     * @return - a String URL for submitting a new username and password to the CSS Gate server.
     */
    private String buildAddUserUrl(String username, String password) {
        StringBuilder sb = new StringBuilder(ADD_USER_URL);

        try {
            sb.append("username=");
            sb.append(username);

            sb.append("&password=");
            sb.append(password);

            Log.i("SignUpFragment", sb.toString());

        } catch(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return sb.toString();
    }


    /**
     * AsyncTask for adding a new user to the CSS Gate server.
     */
    private class AddUserTask extends AsyncTask<String, Void, String> {

        /**
         * doInBackground() method for this task. Adds a new user to CSS Gate server.
         * @param urls - the AddUser URL
         * @return - the resulting string 'success' or 'fail'.
         */
        @Override
        protected String doInBackground(String... urls) {
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
                    response = "Unable to add user, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * onPostExecute method for this task. If the sign up is successful, toast a message
         * and return to login activity. If not, displays appropriate message.
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject obj = new JSONObject(result);
                String r = obj.getString("result");

                if (r.equals("success")) {
                    Toast.makeText(getActivity().getApplicationContext(), "You have been " +
                            "registered " +
                            "successfully", Toast.LENGTH_LONG).show();

                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    String errorMsg = obj.getString("error");
                    Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast
                            .LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Something wrong with the " +
                        "data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
