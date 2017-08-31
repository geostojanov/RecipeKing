package recipeking.uw.tacoma.edu.recipeking.recipes.list;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import recipeking.uw.tacoma.edu.recipeking.MainActivity;
import recipeking.uw.tacoma.edu.recipeking.R;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;

/**
 * Class for RecipeDetailFragment. This class represents the details about a recipe.
 *
 */
public class RecipeDetailFragment extends Fragment {

    /** Constant for which recipe has been selected. */
    public final static String RECIPE_ITEM_SELECTED = "recipe_selected";

    /** String constant for the add comment URL */
    private final static String ADD_COMMENT_URL =
            "https://recipeking.000webhostapp.com/addComment.php?";

    /** String constant for the add rating URL */
    private final static String ADD_RATING_URL =
            "https://recipeking.000webhostapp.com/addRating.php?";

    /** String constant for the comments list URL */
    private final static String COMMENTS_LIST_URL =
            "https://recipeking.000webhostapp.com/comments_list.php?";

    /** String constant for the ratings get URL */
    private final static String GET_RATINGS_URL =
            "https://recipeking.000webhostapp.com/ratings.php?";

    /** TextView element for the recipe's title. */
    private TextView mRecipeTitle;

    /** ImageView element for the recipe's image. */
    private ImageView mRecipeImage;

    /** TextView element for the recipe's ingredients. */
    private TextView mRecipeIngredients;

    /** TextView element for the recipe's instructions URL. */
    private TextView mRecipeInstructionsUrl;

    /** Button element for adding or removing the recipe to the favorite list. */
    private Button mFavoriteButton;

    /** A text view for displaying comments for the current recipe. */
    private TextView mComments;

    /** A string field for holding the list of comments for the current recipe. */
    private String mCommentsString;

    /** An edit text for typing new comment. */
    private EditText mEditTextComment;

    /** A button for submitting a new comment. */
    private Button mAddCommentButton;

    private EditText mEditTextRating;

    private TextView mRatings;

    private Button mAddRatingButton;


    /**
     *  Required empty public constructor
     */
    public RecipeDetailFragment() {

    }

    /**
     * Method for constructing a new instance of this Fragment.
     *
     * @return A new instance of fragment RecipeDetailFragment.
     */
    public static RecipeDetailFragment newInstance() {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreate() method for this Fragment.
     *
     * @param savedInstanceState - the savedInstanceState for this Fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    /**
     *
     * Inflates the view and the view elements for this Fragment.
     *
     * @param inflater - the inflater for this Fragment.
     * @param container - the container for this Fragment.
     * @param savedInstanceState - the savedInstanceState for this Fragment.
     *
     * @return - a view object of this Fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        mRecipeTitle = (TextView) view.findViewById(R.id.recipe_title_tv);
        mRecipeImage = (ImageView) view.findViewById(R.id.recipe_image_iv);
        mRecipeIngredients = (TextView) view.findViewById(R.id.recipe_ingredients);
        mRecipeInstructionsUrl = (TextView) view.findViewById(R.id.recipe_instructions_url);
        mFavoriteButton = (Button) view.findViewById(R.id.favorite_button);

        mComments = (TextView) view.findViewById(R.id.recipe_comments_tv);
        mEditTextComment = (EditText) view.findViewById(R.id.add_new_comment_et);
        mAddCommentButton = (Button) view.findViewById(R.id.add_new_comment_button);

        mRatings = (TextView) view.findViewById(R.id.ratings_tv);
        mEditTextRating = (EditText) view.findViewById(R.id.add_new_rating_et);
        mAddRatingButton = (Button) view.findViewById(R.id.add_new_rating_button);

        return view;
    }

    /**
     * onStart() method for this Fragment. Sets article based on argument passed in.
     */
    @Override
    public void onStart() {
        super.onStart();
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            updateView((Recipe) args.getSerializable(RECIPE_ITEM_SELECTED));
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                shareIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Creates and starts a share intent, and passes the recipe url as data.
     */
    private void shareIntent() {
        Recipe recipe = (Recipe) getArguments().getSerializable(RECIPE_ITEM_SELECTED);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String shareTxt = "Check out this recipe: \n \n" + recipe.getmInstructionsUrl();
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareTxt);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share recipe to"));

    }

    /**
     * Updates the view for this Fragment based on which recipe has been selected.
     *
     * @param recipe - The recipe that has been selected.
     */
    public void updateView(final Recipe recipe) {
        if (recipe != null) {
            mRecipeTitle.setText(recipe.getmTitle());

            // Downloads the image for the recipe.
            MyRecipeRecyclerViewAdapter.DownloadImageTask task = new MyRecipeRecyclerViewAdapter
                    .DownloadImageTask(mRecipeImage);
            task.execute(recipe.getmImageUrl());


            // Parsing the ingredients from array to a single string with new line character.
            String joinIngredients = "";
            for(String s : recipe.getmIngredients()) {
                joinIngredients += s + "\n";
            }

            mRecipeIngredients.setText(joinIngredients);

            // Opens the recipe's website for instructions based on the URL.
            // Implicit intent for opening a webpage.
            mRecipeInstructionsUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = recipe.getmInstructionsUrl();
                    Uri webpage = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    String title = getResources().getString(R.string.chooser_title);
                    Intent chooser = Intent.createChooser(intent, title);

                    if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(chooser);
                    }
                }
            });

            // If the recipe is already in the favorite list, set the text accordingly.
            if (MainActivity.favoriteList.contains(recipe)) {
                mFavoriteButton.setText(R.string.remove_from_favorites);
            }

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mFavoriteButton.getText().toString().equals("Remove from Favorites")) {
                        // Removes the recipe from the static list.
                        MainActivity.removeFromFavorites(recipe, getContext());
                        mFavoriteButton.setText(R.string.add_to_favorites);
                    } else {
                        // Adds the recipe to the static list.
                        MainActivity.addToFavorites(recipe, getContext());
                        mFavoriteButton.setText(R.string.remove_from_favorites);
                    }

                }
            });

            // RETRIEVE COMMENTS FOR THIS RECIPE USING ASYNC TASK
            getCommentsForThisRecipe();

            // SET THE SUBMIT BUTTON LISTENER FOR ADDING NEW COMMENT
            mAddCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean invalid = false;
                    if (mEditTextComment.getText().toString().isEmpty()
                            || mEditTextComment.getText().toString().length() > 150) {
                        invalid = true;
                    }

                    if(invalid) {
                        mEditTextComment.setError("Try again");
                        mEditTextComment.requestFocus();
                    } else {
                        String url = buildAddCommentUrl();
                        AddCommentTask task = new AddCommentTask();
                        task.execute(new String[]{url});
                    }

                }
            });

            //RETRIEVE RATINGS FOR THIS RECIPE
            getRatingsForThisRecipe();

            //SET THE SUBMIT BUTTON LISTENER FOR ADDING NEW RATING
            mAddRatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean invalid = false;
                    if (mEditTextRating.getText().toString().isEmpty()
                            || !isRatingFieldValid()) {
                        invalid = true;
                    }

                    if(invalid) {
                        mEditTextRating.setError("Try again");
                        mEditTextRating.requestFocus();
                    } else {
                        //ASYNC for adding a rating
                        String url = buildAddRatingUrl();
                        AddRatingTask task = new AddRatingTask();
                        task.execute(new String[]{url});
                    }
                }
            });
        }
    }

    /**
     * Validates the EditText field for specifying a rating. Valid values (1,2,3,4,5)
     * @return - true if valid value is specified, false otherwise.
     */
    private boolean isRatingFieldValid() {
        boolean valid = false;
        String value = mEditTextRating.getText().toString();
        if (value.length() <= 1 && isValidInteger(value)) {
            int i = Integer.parseInt(value);
            if (i >= 1 && i <= 5) {
                valid = true;
            }
        }

        return valid;
    }

    /**
     * Checks if a string can represent an positive integer
     *
     * @param theInput the string to parse
     *
     * @return true if the string can represent a positive integer, false otherwise.
     */
    private boolean isValidInteger(String theInput) {
        boolean result = false;
        try {
            int val = Integer.parseInt(theInput);
            if (val > 0) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    /**
     * Downloads the comments for this recipe from CSS Gate.
     */
    private void getCommentsForThisRecipe() {
        String url = buildCommentsListUrl();
        DownloadCommentsForThisRecipe task = new DownloadCommentsForThisRecipe();
        task.execute(new String[]{url});
    }

    /**
     * Downloads the ratings for this recipe from CSS Gate.
     */
    private void getRatingsForThisRecipe() {
        String url = buildRatingsListUrl();
        DownloadRatingsForThisRecipe task = new DownloadRatingsForThisRecipe();
        task.execute(new String[]{url});
    }


    /**
     * Builds the AddCommentUrl for adding a comment to the server.
     * @return - a string.
     */
    private String buildAddCommentUrl() {
        StringBuilder sb = new StringBuilder(ADD_COMMENT_URL);

        try {
            String userComment = MainActivity.currentUser +
                    ": " + mEditTextComment.getText().toString();

            sb.append("usr_comment=");
            sb.append(URLEncoder.encode(userComment, "UTF-8"));

            String recipeTitle = mRecipeTitle.getText().toString();
            sb.append("&recipe=");
            sb.append(URLEncoder.encode(recipeTitle, "UTF-8"));


        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Something wrong with the AddComment url" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return sb.toString();
    }

    /**
     * Builds the AddRatingUrl for adding a rating of this recipe to the server.
     * @return - a string.
     */
    private String buildAddRatingUrl() {
        StringBuilder sb = new StringBuilder(ADD_RATING_URL);

        try {
            String title = mRecipeTitle.getText().toString();
            sb.append("title=");
            sb.append(URLEncoder.encode(title, "UTF-8"));

            sb.append("&user=");
            sb.append(URLEncoder.encode(MainActivity.currentUser, "UTF-8"));

            sb.append("&rating=");
            sb.append(URLEncoder.encode(mEditTextRating.getText().toString(), "UTF-8"));


        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Something wrong with the AddRating url" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return sb.toString();
    }

    /**
     * Builds the GET url for downloading recipe comments from the server.
     * @return - a string.
     */
    private String buildCommentsListUrl() {
        StringBuilder sb = new StringBuilder(COMMENTS_LIST_URL);

        try {
            String recipeTitle = mRecipeTitle.getText().toString();
            sb.append("recipe=");
            sb.append(URLEncoder.encode(recipeTitle, "UTF-8"));

        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Something wrong with the CommentsList url" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return sb.toString();
    }

    /**
     * Builds the GET url for downloading recipe ratings from the server.
     * @return - a string.
     */
    private String buildRatingsListUrl() {
        StringBuilder sb = new StringBuilder(GET_RATINGS_URL);

        try {
            String title = mRecipeTitle.getText().toString();
            sb.append("title=");
            sb.append(URLEncoder.encode(title, "UTF-8"));

        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Something wrong with the GetRatings url" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return sb.toString();
    }


    /**
     * Parses the JSON comments data from the CSS GATE server and fills the mCommentsString field
     * with comments separated by newline character.
     * @param commentsJSON - the retrieved JSON data.
     * @return - If null it is successful, else something went wrong.
     */
    private String parseCommentsJSON(String commentsJSON) {
        String reason = null;
        mCommentsString = "";

        if (commentsJSON != null) {
            try {
                JSONArray arr = new JSONArray(commentsJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String comment = obj.getString("usr_comment");

                    mCommentsString = mCommentsString + comment + "\n";
                }

            } catch (JSONException e) {
                reason = "Unable to parse data, Reason: " + e.getMessage();
            }
        }

        return reason;
    }

    /**
     * Parses the JSON rating data from the CSS GATE server and fills the ratings TextView field
     * with the ratings.
     * @param ratingsJSON - the retrieved JSON data.
     * @return - If null it is successful, else something went wrong.
     */
    private String parseRatingsJSON(String ratingsJSON) {
        String reason = null;
        List<Integer> ratingList = new ArrayList<Integer>();

        if (ratingsJSON != null) {
            try {
                JSONArray arr = new JSONArray(ratingsJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    int rating = Integer.parseInt(obj.getString("rating"));

                    ratingList.add(rating);
                }

                if (!ratingList.isEmpty()) {
                    double sum = 0;
                    for (int i : ratingList) {
                        sum += i;
                    }
                    double avg = sum / ratingList.size();
                    System.out.println(avg);
                    Log.i("parseRatingsJSON avg:", String.valueOf(avg));
                    Log.i("parseRatingsJSON sum:", String.valueOf(sum));
                    Log.i("parseRatingsJSON size:", String.valueOf(ratingList.size()));
                    String result = String.format("Ratings (%d): %.2f", ratingList.size(), avg);
                    mRatings.setText(result);
                }

            } catch (JSONException e) {
                reason = "Unable to parse data, Reason: " + e.getMessage();
            }
        }

        return reason;
    }



    /**
     * AsyncTask for downloading the comments for the current recipe. Sets the mCommentsString
     * field with the comments received.
     */
    private class DownloadCommentsForThisRecipe extends AsyncTask <String, Void, String> {

        /**
         * doInBackground() method for this task. Downloads the comments from the CSS Gate server.
         * @param urls - GET COMMENTS Url.
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
                    result = new StringBuilder("Unable to get comments for this recipe, Reason: "
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
         * onPostExecute() method for this task. Fills the mCommentsString field with the
         * comments retrieved.
         * @param result - the retrieved JSON data.
         */
        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (!result.contains("No comments for this recipe.")) {
                // Fills the mCommentsString with the comments parsed.
                result = parseCommentsJSON(result);

                if(result != null) {
                    Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                if (!mCommentsString.isEmpty() && mCommentsString != null) {
                    mComments.setText(mCommentsString);
                }

            }

        }
    }

    /**
     * AsyncTask for downloading the ratings for the current recipe. Sets the ratings TextView
     * field with the ratings received.
     */
    private class DownloadRatingsForThisRecipe extends AsyncTask <String, Void, String> {

        /**
         * doInBackground() method for this task. Downloads the ratings from the CSS Gate server.
         * @param urls - GET RATINGS Url.
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
                    result = new StringBuilder("Unable to get ratings for this recipe, Reason: "
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
         * onPostExecute() method for this task. Fills the ratings TextView field with the
         * ratings retrieved.
         * @param result - the retrieved JSON data.
         */
        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (!result.contains("No ratings found.")) {
                // Fills the mCommentsString with the comments parsed.
                result = parseRatingsJSON(result);

                if(result != null) {
                    Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                            .show();
                    return;
                }

            }

        }
    }


    /**
     * AsyncTask for adding a comment for the current recipe. Sends the user specified comment
     * to the server.
     */
    private class AddCommentTask extends AsyncTask<String, Void, String> {

        /**
         * doInBackground() method for this task.
         * @param urls
         * @return
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
                    result = new StringBuilder("Unable to add comment to server, Reason: "
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
         * onPostExecute method for this task.
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            if (result.contains("success")) {
                Toast.makeText(getActivity().getApplicationContext(), "Comment submitted.", Toast
                        .LENGTH_SHORT)
                        .show();

                mEditTextComment.setText("");

                // Refresh view for this fragment, so the comment will be visible immediately
                Bundle args = getArguments();
                if (args != null) {
                    updateView((Recipe) args.getSerializable(RECIPE_ITEM_SELECTED));
                }
            }
        }

    }

    /**
     * AsyncTask for adding a rating for the current recipe. Sends the user specified rating
     * to the server.
     */
    private class AddRatingTask extends AsyncTask<String, Void, String> {

        /**
         * doInBackground() method for this task.
         * @param urls
         * @return
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
                    result = new StringBuilder("Unable to add rating to server, Reason: "
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
         * onPostExecute method for this task.
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            if (result.contains("success")) {
                Toast.makeText(getActivity().getApplicationContext(), "Rating submitted.", Toast
                        .LENGTH_SHORT)
                        .show();

                mEditTextRating.setText("");

                // Refresh view for this fragment, so the comment will be visible immediately
                Bundle args = getArguments();
                if (args != null) {
                    updateView((Recipe) args.getSerializable(RECIPE_ITEM_SELECTED));
                }
            }
        }

    }


}
