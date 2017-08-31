package recipeking.uw.tacoma.edu.recipeking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import recipeking.uw.tacoma.edu.recipeking.R;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;

/**
 * Class representing database functionality for storing Recipe objects to local database.
 *
 * @author Gjorgi Stojanov
 * @version March 02 2017
 */

public class RecipeDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Recipe.db";

    private static final String RECIPE_TABLE = "Recipe";

    private RecipeDBHelper mRecipeDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public RecipeDB(Context context) {
        mRecipeDBHelper = new RecipeDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mRecipeDBHelper.getWritableDatabase();
    }

    /**
     * Inserts the recipe into the local sqlite table. Returns true if successful, false otherwise.
     * @param title - the title of the recipe
     * @param recipeDetails - the image url of the recipe
     * @return true or false
     */
    public boolean insertRecipe(String title, String recipeDetails) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("recipe_details", recipeDetails);

        long rowId = mSQLiteDatabase.insert("Recipe", null, contentValues);
        return rowId != -1;
    }

    /**
     * Returns the list of recipes from the local Recipe table.
     * @return list
     */
    public List<Recipe> getRecipes() {

        String[] columns = {
                "title", "recipe_details"};

        Cursor c = mSQLiteDatabase.query(
                RECIPE_TABLE,  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<Recipe> list = new ArrayList<Recipe>();
        for (int i=0; i<c.getCount(); i++) {
            String title = c.getString(0);
            String recipe_details = c.getString(1);

            Recipe recipe = new Recipe(title, recipe_details);
            list.add(recipe);
            c.moveToNext();
        }

        return list;
    }

    /**
     * Delete all the data from the RECIPE_TABLE
     */
    public void deleteRecipes() {
        mSQLiteDatabase.delete(RECIPE_TABLE, null, null);
    }


    /**
     * Method for closing the database.
     */
    public void closeDB() {
        mSQLiteDatabase.close();
    }


    /**
     * Helper class for RecipeDB.
     */
    class RecipeDBHelper extends SQLiteOpenHelper {

        private final String CREATE_RECIPE_SQL;

        private final String DROP_RECIPE_SQL;

        public RecipeDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
            super(context, name, factory, version);

            CREATE_RECIPE_SQL = context.getString(R.string.CREATE_RECIPE_SQL);
            DROP_RECIPE_SQL = context.getString(R.string.DROP_RECIPE_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase mSQLiteDatabase) {
            mSQLiteDatabase.execSQL(CREATE_RECIPE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase mSQLiteDatabase, int oldVersion, int newVersion) {
            mSQLiteDatabase.execSQL(DROP_RECIPE_SQL);
            onCreate(mSQLiteDatabase);
        }

    }

}
