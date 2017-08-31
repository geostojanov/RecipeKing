package recipeking.uw.tacoma.edu.recipeking.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import recipeking.uw.tacoma.edu.recipeking.R;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * onCreate() method for this activity.
     * Loads the login fragment.
     * @param savedInstanceState - the savedInstanceState for this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            LoginFragment loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_fragment_container, loginFragment)
                    .commit();
        }

    }

}

