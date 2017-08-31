package recipeking.uw.tacoma.edu.recipeking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import recipeking.uw.tacoma.edu.recipeking.recipes.list.RecipeDetailFragment;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.RecipeFragment;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;

/**
 * This class represents the MyRecipesActivity. This is a holder activity for the
 * RecipeFragment and RecipeDetailFragment.
 *
 */
public class RecipesListActivity extends AppCompatActivity implements
        RecipeFragment.OnListFragmentInteractionListener {

    //public ProgressBar mLoadingIndicator;

    /**
     * onCreate method for the current activity. Initializes and commits the RecipeFragment
     * together with the message that has been passed to it.
     *
     * @param savedInstanceState - the saved arguments for this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_list);
        Intent intent = getIntent();

        // Message from the ImageView that called this activity. If it was 'Desserts'
        // it will build the API URL for desserts; if it was 'Italian' it will build it
        // for italian, etc.
        String message = intent.getStringExtra(CategoriesActivity.EXTRA_MESSAGE);

        //mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        Log.i("RecipesListActivity", "onCreate()");

        if ((savedInstanceState == null)
            && (findViewById(R.id.activity_recipes_list_fragment_container) != null)) {


            Log.i("RecipesListActivity", "recipeFragment about to be created");
            RecipeFragment recipeFragment = RecipeFragment.newInstance(message, 1);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_recipes_list_fragment_container, recipeFragment)
                    .commit();

            Log.i("RecipesListActivity", "fragment committed");
        }



    }

    /**
     * Listener method for interacting with a item on the fragment list. When an item is clicked
     * it opens the RecipeDetailFragment.
     * @param item - The recipe that has been clicked.
     */
    @Override
    public void onListFragmentInteraction(Recipe item) {
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(RecipeDetailFragment.RECIPE_ITEM_SELECTED, item);
        recipeDetailFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_recipes_list_fragment_container, recipeDetailFragment)
                .addToBackStack(null)
                .commit();
    }

}
