package recipeking.uw.tacoma.edu.recipeking.recipes.list;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import recipeking.uw.tacoma.edu.recipeking.MainActivity;
import recipeking.uw.tacoma.edu.recipeking.R;
import recipeking.uw.tacoma.edu.recipeking.RecipesListActivity;
import recipeking.uw.tacoma.edu.recipeking.data.RecipeDB;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;
import recipeking.uw.tacoma.edu.recipeking.utils.NetworkUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RecipeFragment extends Fragment {

    /** Constants for building the API Url. */
    private static final String BASE_API_URL = "https://api.edamam.com/search?", PARAM_QUERY =
            "q", APP_ID = "app_id", APP_ID_VALUE = "207c8c04", APP_KEY = "app_key", APP_KEY_VALUE
            = "045854ce8fa13ee1b1b2c482677ec902", FROM = "from", FROM_VALUE = "0", TO = "to",
            TO_VALUE = "30";

    /** String for the current resulting API. */
    private static String mResultAPIUrl;

    /** Keeps track in what mode this fragment is called. 1 is for API results. 2 for favorites.
     * 3 for MyRecipes */
    private static int fragmentMode;

    /** String message for counting the column for this Fragment (List). */
    private static final String ARG_COLUMN_COUNT = "column-count";

    /** Field for counting the columns for this Fragment(List). */
    private int mColumnCount = 1;

    /** Field for the RecyclerViewer for this Fragment (List). */
    private RecyclerView mRecyclerView;

    /** Listener field for interacting with this Fragment (List). */
    private OnListFragmentInteractionListener mListener;

    /** Local recipe database storing info when network connection is unavailable. */
    private RecipeDB mRecipeDB;

    /** A recipe list for storing the local recipe data from the local Recipe database. */
    private List<Recipe> mRecipeList;

    private ProgressBar mLoadingIndicator;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeFragment() {
    }


    /**
     * Method for constructing a new instance of this Fragment (List).
     *
     * @param contentSearch - is the message passed to this fragment, so it will build
     *                      the API URL based on the query (message).
     * @param mode - boolean for which mode this fragment was called. true is for Favorites.
     * @return - a new Fragment (List).
     */
    public static RecipeFragment newInstance(String contentSearch, int mode) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);

        fragmentMode = mode;

        mResultAPIUrl = buildUrl(contentSearch).toString();

        return fragment;
    }

    /**
     * onCrate() for this Fragment. Initializes the mColumnCount.
     * @param savedInstanceState - the saved arguments for this activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    /**
     * Inflates the view and the view elements for this Fragment.
     * If this Fragment was called in favorite mode, it bind the mRecyclerView to the static
     * favoriteList for the current user. If it was not in favorite mode, it uses the API Url
     * based on the query passed.
     *
     * @param inflater - the inflater for this Fragment (List).
     * @param container - the container for this Fragment (List).
     * @param savedInstanceState - the savedInstanceState for this Fragment (List).
     *
     * @return - a view object of this Fragment(List).
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);


        // Set the adapter
        if (view instanceof FrameLayout) {
            Context context = view.getContext();
            //mRecyclerView = (RecyclerView) view;
            mRecyclerView = (RecyclerView) view.getRootView().findViewById(R.id.list);
            mLoadingIndicator = (ProgressBar) view.getRootView().
                    findViewById(R.id.pb_loading_indicator);
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            switch (fragmentMode) {
                case 1:
                    if (NetworkUtils.networkConnectionAvailable(getActivity())) {
                        DownloadRecipesTask task = new DownloadRecipesTask();
                        task.execute(new String[] {mResultAPIUrl});
                    } else {
                        Toast.makeText(getActivity(), "No network connection available. " +
                                "Displaying locally stored data",
                                Toast.LENGTH_SHORT) .show();
                        //Fill the local list of recipe with data from the local Recipe database
//                        if(mRecipeDB == null) {
//                            mRecipeDB = new RecipeDB(getActivity());
//                        }
//                        if (mRecipeList == null) {
//                            mRecipeList = mRecipeDB.getRecipes();
//                        }
//                        mRecyclerView.setAdapter(new MyRecipeRecyclerViewAdapter(mRecipeList,
//                                mListener));
                    }
                    break;
                case 2:
                    mRecyclerView.setAdapter(new MyRecipeRecyclerViewAdapter(MainActivity.favoriteList,
                            mListener));
                    break;
                case 3:

                    break;
                default:
                    break;
            }

        }
        return view;
    }

    /**
     * onAttach() method for this Fragment (List). It sets the Listener based on the context.
     *
     * @param context - the current context.
     * @throws RuntimeException - if the calling activity didn't implement the listener.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     * onDetach() method for this Fragment (List).
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Recipe item);
    }

    /**
     *
     * Helper method to construct the API search URL based on certain keyword.
     *
     * @param searchQuery The query keyword for the API search
     * @return returns the API search URL
     */
    private static URL buildUrl(String searchQuery) {
        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, searchQuery)
                .appendQueryParameter(APP_ID, APP_ID_VALUE)
                .appendQueryParameter(APP_KEY, APP_KEY_VALUE)
                .appendQueryParameter(FROM, FROM_VALUE)
                .appendQueryParameter(TO, TO_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * AsyncTask for downloading the recipes from the built API URL. Sets the RecyclerView
     * based on the generated list of recipes.
     */
    private class DownloadRecipesTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        /**
         * doInBackground() method for this task. Downloads the recipes from the external API.
         * @param urls - GET Url for the external API.
         * @return - the resulting JSON data.
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
         * onPostExecute() method for this task. Fills a list of recipe objects based on the
         * retrieved JSON data.
         * @param result - the retrieved JSON data.
         */
        @Override
        protected void onPostExecute(String result) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            List<Recipe> recipeList = new ArrayList<Recipe>();
            result = Recipe.parseRecipesJSON(result, recipeList);

            if(result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (!recipeList.isEmpty()) {
//                if (mRecipeDB == null) {
//                    mRecipeDB = new RecipeDB(getActivity());
//                }

                // Delete old data so that you can refresh the local
                // database with the network data.
                //mRecipeDB.deleteRecipes();

                // Fetch the network data and update the local database
//                for (int i = 0; i < recipeList.size(); i++) {
//                    Recipe recipe = recipeList.get(i);
//                    String ingred = "";
//                    for (String s : recipe.getmIngredients()) {
//                        ingred += s + "\n";
//                    }
//                    mRecipeDB.insertRecipe(recipe.getmTitle(), recipe.getmImageUrl(),
//                            ingred, recipe.getmInstructionsUrl());
//                }

                mRecyclerView.setAdapter(new MyRecipeRecyclerViewAdapter(recipeList, mListener));
            }

        }

    }

}
