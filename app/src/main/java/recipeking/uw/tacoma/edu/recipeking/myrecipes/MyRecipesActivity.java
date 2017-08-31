package recipeking.uw.tacoma.edu.recipeking.myrecipes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import recipeking.uw.tacoma.edu.recipeking.R;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;

/**
 * This class represents the MyRecipesActivity.
 *
 */
public class MyRecipesActivity extends AppCompatActivity implements
UserRecipeFragment.OnListFragmentInteractionListener,
UserRecipeAddFragment.RecipeAddListener,
UserRecipeDetailFragment.RecipeDeleteListener {

    /**
     * onCreate method for the current activity. Prepares the UserRecipeFragment, and sets up
     * the floating action button click handler.
     *
     * @param savedInstanceState - the saved arguments for this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        findViewById(R.id.activity_my_recipes_no_recipe_image).setVisibility(View.GONE);

        if ((savedInstanceState == null) &&
                findViewById(R.id.content_my_recipes_fragment_container) != null) {

            UserRecipeFragment userRecipeFragment = new UserRecipeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_my_recipes_fragment_container, userRecipeFragment)
                    .commit();

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserRecipeAddFragment recipeAddFragment = UserRecipeAddFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_my_recipes_fragment_container, recipeAddFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

    }

    /**
     * Method for fragment interaction.
     * @param item - the recipe item clicked.
     */
    @Override
    public void onListFragmentInteraction(Recipe item) {
        UserRecipeDetailFragment userRecipeDetailFragment = UserRecipeDetailFragment.newInstance
                (item);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_my_recipes_fragment_container, userRecipeDetailFragment)
                .addToBackStack(null)
                .commit();
    }


    /**
     * Listener method for adding recipe.
     * @param url - the add recipe url.
     */
    @Override
    public void addRecipe(String url) {
        AddRecipeTask task = new AddRecipeTask();
        task.execute(new String[]{url});

        // Takes you back to the previous fragment by popping the current fragment out.
        getSupportFragmentManager().popBackStackImmediate();
    }


    /**
     * Listener method for deleting a recipe.
     * @param url - the delete recipe url.
     */
    @Override
    public void deleteRecipe(String url) {
        DeleteRecipeTask task = new DeleteRecipeTask();
        task.execute(new String[]{url});

        // Takes you back to the previous fragment by popping the current fragment out.
        getSupportFragmentManager().popBackStackImmediate();
    }


    /**
     * AsyncTask class for adding a recipe to the CSS gate server.
     */
    private class AddRecipeTask extends AsyncTask<String, Void, String> {

        /**
         * doInBackground method for this task.
         * @param urls - the url
         * @return - the GET result from the server.
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
                    response = "Unable to add course, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * onPostExecute method for this task. Informs whether the add was successful or not.
         * @param result - the GET result from the server.
         */
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Recipe successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * AsyncTask for deleting a recipe from the server.
     */
    private class DeleteRecipeTask extends AsyncTask<String, Void, String> {

        /**
         * doInBackground() method for this task.
         * @param urls - the delete recipe URL.
         * @return - a String.
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
                    response = "Unable to execute task, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
            return response;
        }


        /**
         * onPostExecute method for this task.
         * @param result - the result from the server.
         */
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Recipe deleted successfully!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
