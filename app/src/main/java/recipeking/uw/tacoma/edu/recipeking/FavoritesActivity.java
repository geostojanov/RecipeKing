package recipeking.uw.tacoma.edu.recipeking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import recipeking.uw.tacoma.edu.recipeking.recipes.list.RecipeDetailFragment;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.RecipeFragment;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;

/**
 * This class represents the FavoritesActivity. Here it will display the user's favorite
 * recipes as a list.
 *
 */
public class FavoritesActivity extends AppCompatActivity implements
        RecipeFragment.OnListFragmentInteractionListener {

    /**
     * onCreate method for the current activity. It creates a recipe fragment (list)
     * only if the user's favoriteList is not empty.
     *
     * @param savedInstanceState - the saved arguments for this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if ((savedInstanceState == null)
                && (findViewById(R.id.activity_favorites_fragment_container) != null)) {

            if (!MainActivity.favoriteList.isEmpty()) {

                // Creates new instance of recipeFragment, passing it 2, indicating that
                // it is in favorite mode.
                RecipeFragment recipeFragment = RecipeFragment.newInstance("", 2);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_favorites_fragment_container, recipeFragment)
                        .commit();
            }

        }

    }

    /**
     * onStart() method for this activity. Checks if the favoriteList is empty, if it is
     * makes the illustration visible, if not it hides the illustration.
     */
    @Override
    protected void onStart() {
        super.onStart();
        ImageView img = (ImageView) findViewById(R.id.activity_favorites_nofav_image);
        if (!MainActivity.favoriteList.isEmpty()) {
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
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
                .replace(R.id.activity_favorites_fragment_container, recipeDetailFragment)
                .addToBackStack(null)
                .commit();
    }

}
