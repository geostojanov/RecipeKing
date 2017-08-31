package recipeking.uw.tacoma.edu.recipeking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import recipeking.uw.tacoma.edu.recipeking.login.LoginActivity;
import recipeking.uw.tacoma.edu.recipeking.myrecipes.MyRecipesActivity;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;

/**
 *
 * This class represents the MainActivity, i.e. home activity for the user
 * after login.
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /** String constant for the base URL for getting favorites list from the server. */
    private static final String BASE_FAV_URL
            = "https://recipeking.000webhostapp.com/favorites_list.php?";

    /** String constant for the base URL for adding a recipe to the favorite list
     * on the server. */
    private static final String ADD_FAV_URL
            = "https://recipeking.000webhostapp.com/addFavorite.php?";

    /** String constant for the base URL for removing a recipe from the favorite list
     * on the server. */
    private static final String REMOVE_FAV_URL
            = "https://recipeking.000webhostapp.com/removeFavorite.php?";

    /** String constant for keeping track of the current logged in user by its username. */
    public static String currentUser;

    /** A list of favorite recipes for the current logged in user. */
    public static List<Recipe> favoriteList;

    /**
     * onCreate method for the current activity. Initializing the toolbar,
     * navigation drawer, and updating the greeting message based on the current user.
     *
     * @param savedInstanceState - the saved arguments for this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "onCreate()");
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.nav_username_greeting);
        String msg = getString(R.string.greeting_message) + " " + currentUser;
        textView.setText(msg);

    }

    /**
     * onStart() method for this activity. Retrieves the favorite list data
     * for the current user.
     *
     */
    @Override
    protected void onStart() {
        super.onStart();

        Log.i("MainActivity", "onStart()");

        DownloadFavoritesTask task = new DownloadFavoritesTask();
        task.execute(new String[]{buildFavUlr()});

    }

    /**
     * onPause() method for this activity. For debugging purposes.
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity", "onPause()");
    }

    /**
     * onRestart() method for this activity. For debugging purposes.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("MainActivity", "onRestart()");
    }

    /**
     * onResume() method for this activity. For debugging purposes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume()");

    }


    /**
     * onStop() method for this activity. For debugging purposes.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MainActivity", "onStop()");
    }

    /**
     * onDestroy() method for this activity. For debugging purposes.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "onDestroy()");
    }

    /**
     * onBackPressed() method for this activity. It enables switching back to this activity
     * when the navigation drawer is open.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Method for inflating menus.
     *
     * @param menu - the menu to be inflated.
     * @return - boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Method for selected menu item.
     *
     * @param item - the menu which has been selected.
     * @return - boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Opens the CategoriesActivity.
     *
     * @param view - the view object that has called this method.
     */
    public void showCategoriesActivity(View view) {
        Intent intent = new Intent(this, CategoriesActivity.class);
        startActivity(intent);
    }

    /**
     * Opens the FavoritesActivity.
     *
     * @param view - the view object that has called this method.
     */
    public void showFavoritesActivity(View view) {
        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
    }

    /**
     * Opens the MyRecipesActivity.
     *
     * @param view - the view object that has called this method.
     */
    public void showMyRecipesActivity(View view) {
        Intent intent = new Intent(this, MyRecipesActivity.class);
        startActivity(intent);
    }


    /**
     *
     * Method handler when an item has been selected on the navigation drawer.
     *
     * @param item - The item that has been selected.
     * @return - boolean
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_browse_categories) {
            showCategoriesActivity(null);
        } else if (id == R.id.nav_favorites) {
            showFavoritesActivity(null);
        } else if (id == R.id.nav_my_recipes) {
            showMyRecipesActivity(null);
        } else if (id == R.id.nav_logout) {
            currentUser = null;
            favoriteList = null;
            Log.i("MainActivity", "currentUser set to null. favoriteList set to null.");
            Log.i("MainActivity", "Logging out");

            SharedPreferences sharedPreferences =
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false)
                    .commit();

            sharedPreferences.edit().putString(getString(R.string.CURRENT_USER), null);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_pop_articles) {
            Intent intent = new Intent(this, CookingArticlesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_pop_videos) {
            Intent intent = new Intent(this, CookingVideosActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Static method for adding a recipe to the favorite list.
     *
     * @param recipe - The recipe to be added.
     * @param context - The context which is calling this method. Used for Toast messages.
     */
    public static void addToFavorites(Recipe recipe, Context context) {
        // Add to local list
        MainActivity.favoriteList.add(recipe);

        // Add to the Server
        AddToFavoritesList task = new AddToFavoritesList();
        task.execute(buildAddFavUlr(recipe, context));
        Log.i("MainActivity", "Recipe added to server");
    }

    /**
     * Static method for removing a recipe from the favorite list.
     *
     * @param recipe - The recipe to be removed.
     * @param context - The context which is calling this method. Used for Toast messages.
     */
    public static void removeFromFavorites(Recipe recipe, Context context) {
        // Remove from local list
        if (!MainActivity.favoriteList.isEmpty()) {
            MainActivity.favoriteList.remove(recipe);
        }

        // Remove from Server
        AddToFavoritesList task = new AddToFavoritesList();
        task.execute(buildRemoveFavUrl(recipe, context));
        Log.i("MainActivity", "Recipe removed from server");
    }

    /**
     * Method for building the favorite list URL.
     *
     * @return - the custom URL for the current user's favorite list.
     */
    private String buildFavUlr() {
        StringBuilder sb = new StringBuilder(BASE_FAV_URL);

        try {
            String user = MainActivity.currentUser;
            sb.append("usr=");
            sb.append(URLEncoder.encode(user, "UTF-8"));

            Log.i("Recipe(FavList)Fragment", sb.toString());

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Something wrong with the favlist url" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return sb.toString();
    }

    /**
     *
     * Method for building the add to favorites URL.
     *
     * @param recipe - The recipe that is to be added.
     * @param context - The context which is calling this method.
     * @return - The URL for adding a favorite recipe for the current user.
     */
    private static String buildAddFavUlr(Recipe recipe, Context context) {
        StringBuilder sb = new StringBuilder(ADD_FAV_URL);

        try {
            String title = recipe.getmTitle();
            sb.append("title=");
            sb.append(URLEncoder.encode(title, "UTF-8"));

            String user = MainActivity.currentUser;
            sb.append("&user=");
            sb.append((URLEncoder.encode(user, "UTF-8")));

            String url = recipe.getmImageUrl();
            sb.append("&image_url=");
            sb.append(URLEncoder.encode(url, "UTF-8"));

            String joinIngredients = "";
            for(String s : recipe.getmIngredients()) {
                joinIngredients += s + "\n";
            }
            sb.append("&ingredients=");
            sb.append(URLEncoder.encode(joinIngredients, "UTF-8"));

            String instructions_url = recipe.getmInstructionsUrl();
            sb.append("&instructions_url=");
            sb.append(URLEncoder.encode(instructions_url, "UTF-8"));

            Log.i("MainActivity AddFavURL", sb.toString());


        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(),
                    "Something wrong with the AddFavlist url" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return sb.toString();
    }


    /**
     *
     * Method for building the remove from favorites URL.
     *
     * @param recipe - The recipe that is to be removed.
     * @param context - The context which is calling this method.
     * @return - The URL for removing a favorite recipe for the current user.
     */
    private static String buildRemoveFavUrl(Recipe recipe, Context context) {
        StringBuilder sb = new StringBuilder(REMOVE_FAV_URL);

        try {
            String user = MainActivity.currentUser;
            sb.append("user=");
            sb.append((URLEncoder.encode(user, "UTF-8")));

            String title = recipe.getmTitle();
            sb.append("&title=");
            sb.append((URLEncoder.encode(title, "UTF-8")));

        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(),
                    "Something wrong with the RemoveFavlist url" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return sb.toString();
    }

    /**
     * AsynTask class for downloading the recipe list for the current user from the server.
     */
    private class DownloadFavoritesTask extends AsyncTask<String, Void, String> {

        /**
         * doInBackground() method for this task. Makes an URL connection to CSS Gate and gets
         * the resulting JSON data.
         * @param urls - the CSS Gate url(s).
         * @return - the resulting JSON data as a string.
         */
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        result.append(s);
                    }
                } catch (Exception e) {
                    result = new StringBuilder("Unable to download the list of recipes, Reason: " + e
                            .getMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
            return result.toString();
        }

        /**
         * onPostExecute() method for this task. Receive the JSON data and builds a recipe objects
         * and adds them to the favoriteList.
         * @param result - the resulting JSON data string.
         */
        @Override
        protected void onPostExecute(String result) {
            // List is empty
            if (result.startsWith("Unable to")) {

                return;
            }

            favoriteList = new ArrayList<>();

            Log.i("MainActivity", "new favList about to be filled");
            // Populate the favoriteList for the current user based on the JSON data received
            // from the server.
            result = Recipe.parseFavRecipesJSON(result, favoriteList);

            Log.i("MainActivity", "favList filled from server");

            if(result != null) {
                return;
            }

        }
    }

    /**
     * AsyncTask class for adding (or removing) a favorite recipe to the server.
     */
    private static class AddToFavoritesList extends AsyncTask<String, Void, String> {

        /**
         * doInBackground() method for this task. Makes an URL connection to CSS Gate and adds
         * a recipe object to the favorite table on the server.
         * @param urls - the CSS Gate url(s).
         * @return - the resulting JSON data 'success' or 'fail'.
         */
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        result.append(s);
                    }
                } catch (Exception e) {
                    result = new StringBuilder("Unable to add recipe to favorites list, Reason: "
                            + e
                            .getMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
            return result.toString();
        }

        /**
         * onPostExecute() method for this task.
         * @param result - the resulting JSON data string.
         */
        @Override
        protected void onPostExecute(String result) {
           super.onPostExecute(result);
        }


    }

}
