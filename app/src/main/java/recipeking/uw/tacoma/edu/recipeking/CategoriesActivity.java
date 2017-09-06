package recipeking.uw.tacoma.edu.recipeking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * This class represents the CategoriesActivity.
 *
 */
public class CategoriesActivity extends AppCompatActivity {

    /** String constant for passing a message from this Activity to RecipeListActivity */
    public static final String EXTRA_MESSAGE = "recipeking.uw.tacoma.edu.recipeking.MESSAGE";

    /**
     * onCreate method for the current activity. Initializing the toolbar, the floating
     * action button, and the up button for going back.
     *
     * @param savedInstanceState - the saved arguments for this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            onSearchRequested();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Method for opening the RecipeListActivity based on the view that has been clicked.
     * Passing a message so the RecipeListActivity would know which view has called it.

     * @param view - The view which is calling RecipeListActivity
     */
    public void showActivity(View view) {
        Intent intent = new Intent(this, RecipesListActivity.class);
        String message = (String) view.getContentDescription();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
