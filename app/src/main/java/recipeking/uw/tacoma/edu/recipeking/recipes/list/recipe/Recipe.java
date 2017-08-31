package recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a Recipe object.
 */
public class Recipe implements Serializable {

    /** The title of the recipe. */
    private String mTitle;

    /** The image url of the recipe. */
    private String mImageUrl;

    /** The ingredients array of the recipe. */
    private String[] mIngredients;

    /** The instructions url of the recipe. */
    private String mInstructionsUrl;

    /** This field is used for user defined recipes. */
    private String mRecipeDetails;

    /** Strings representing JSON data attributes for the external API. */
    private static final String HITS = "hits";
    private static final String RECIPE = "recipe";
    private static final String LABEL = "label";
    private static final String IMAGE = "image";
    private static final String INGREDIENT_LINES = "ingredientLines";
    private static final String URL = "url";

    /** Strings representing JSON data attributes for the favorite list. */
    private static final String TITLE = "title";
    private static final String IMAGE_URL = "image_url";
    private static final String INGREDIENTS = "ingredients";
    private static final String INSTRUCTIONS_URL = "instructions_url";
    private static final String RECIPE_DETAILS = "recipe_details";

    /**
     * Constructor for the class. Initializes the class' fields.
     * @param mTitle - the title of the recipe.
     * @param mImageUrl - the image url of the recipe.
     * @param mIngredients - the ingredients of the recipe.
     * @param mInstructionsUrl - the instructions url of the recipes.
     */
    public Recipe(String mTitle, String mImageUrl, String[] mIngredients, String mInstructionsUrl) {
        this.mTitle = mTitle;
        this.mImageUrl = mImageUrl;
        this.mIngredients = mIngredients;
        this.mInstructionsUrl = mInstructionsUrl;
    }

    public Recipe(String mTitle, String mRecipeDetails) {
        this.mTitle = mTitle;
        this.mRecipeDetails = mRecipeDetails;
    }

    /**
     * Gets the recipe's title.
     * @return - the title.
     */
    public String getmTitle() {
        return mTitle;
    }

    /**
     * Gets the recipe's iamge url.
     * @return - the image url.
     */
    public String getmImageUrl() {
        return mImageUrl;
    }

    /**
     * Gets the recipe's ingredients.
     * @return - string array of ingredients.
     */
    public String[] getmIngredients() {
        return mIngredients;
    }

    /**
     * Gets the instructions url.
     * @return - the instructions url.
     */
    public String getmInstructionsUrl() {
        return mInstructionsUrl;
    }


    /**
     * Gets the recipe details for user defined recipes.
     * @return - a string.
     */
    public String getmRecipeDetails() {
        return mRecipeDetails;
    }

    /**
     * Sets the recipe's title.
     * @param mTitle - the title of the recipe.
     */
    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    /**
     * Sets the recipe's image url.
     * @param mImageUrl - the image url of the recipe.
     */
    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    /**
     * Sets the recipe's instructions url.
     * @param mInstructionsUrl - the instructions url of the recipe.
     */
    public void setmInstructionsUrl(String mInstructionsUrl) {
        this.mInstructionsUrl = mInstructionsUrl;
    }

    /**
     * Sets the recipe's ingredients.
     * @param mIngredients - the ingredients of the recipe.
     */
    public void setmIngredients(String[] mIngredients) {
        this.mIngredients = mIngredients;
    }

    /**
     * Method for parsing JSON data from the external API. Parses the data and fills a list
     * of recipes based on the JSON data.
     * @param recipesJSON - the JSON data string retrieved from the server.
     * @param recipeList - the recipe list that needs to be filled with data.
     * @return - a String. If null than successful, else something went wrong.
     */
    public static String parseRecipesJSON(String recipesJSON, List<Recipe> recipeList) {
        String reason = null;
        if (recipesJSON != null) {
            try {
                JSONObject obj = new JSONObject(recipesJSON);
                JSONArray arr = obj.getJSONArray(Recipe.HITS);

                // Goes through all JSON data objects retrieved from external API,
                // and processes them accordingly.
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject rObj = arr.getJSONObject(i);
                    JSONObject recipeJson = rObj.getJSONObject(Recipe.RECIPE);

                    String label = recipeJson.getString(Recipe.LABEL);

                    String image = recipeJson.getString(Recipe.IMAGE);

                    JSONArray JSONingredients = recipeJson.getJSONArray(Recipe.INGREDIENT_LINES);
                    //Fills array based on JSON array elements.
                    String[] ingred = new String[JSONingredients.length()];
                    for (int j = 0; j < ingred.length; j++) {
                        ingred[j] = JSONingredients.getString(j);
                    }

                    String url = recipeJson.getString(Recipe.URL);

                    // Construct Recipe object based on the retrieved data.
                    Recipe recipe = new Recipe(label, image, ingred, url);

                    // Adds the Recipe object to the list.
                    recipeList.add(recipe);
                }

            } catch (JSONException e) {
                reason = "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

    /**
     * Method for parsing JSON data from the CSSGate Server
     * for the favorite list. Parses the data and fills a list of recipes based on
     * the JSON data.
     * @param recipesJSON - the JSON data string retrieved from the CSSGATE Server.
     * @param recipeList - the recipe list that needs to be filled with data.
     * @return - a String. If null than successful, else something went wrong.
     */
    public static String parseFavRecipesJSON(String recipesJSON, List<Recipe> recipeList) {
        String reason = null;
        if(recipesJSON != null) {
            try {
                JSONArray arr = new JSONArray(recipesJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    String title = obj.getString(Recipe.TITLE);
                    String img_url = obj.getString(Recipe.IMAGE_URL);
                    String ingredients = obj.getString(Recipe.INGREDIENTS);

                    String[] ingred = ingredients.split("\\n");

                    String instructions_url = obj.getString(Recipe.INSTRUCTIONS_URL);

                    Recipe recipe = new Recipe(title, img_url, ingred, instructions_url);
                    recipeList.add(recipe);

                }
            } catch (JSONException e) {
                reason = "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

    public static String parseMyRecipesJSON(String myRecipeJSON, List<Recipe> recipeList) {
        String reason = null;
        if(myRecipeJSON != null) {
            try {

                if(!myRecipeJSON.contains("fail")) {
                    JSONArray arr = new JSONArray(myRecipeJSON);

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        String title = obj.getString(Recipe.TITLE);
                        String recipe_details = obj.getString(Recipe.RECIPE_DETAILS);

                        Recipe recipe = new Recipe(title, recipe_details);
                        recipeList.add(recipe);
                    }
                } else {
                    reason = "No recipes found.";
                }

            } catch (JSONException e) {
                reason = "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

    /**
     * Compares two Recipe objects for equality.
     * @param theOther represents the other Recipe object.
     * @return the result of the comparison.
     */
    @Override
    public boolean equals(Object theOther) {
        boolean result = false;
        if(theOther instanceof Recipe) {
            Recipe other = (Recipe) theOther;
            if (this.mTitle.equals(other.mTitle)) {
                result = true;
            }
        }
        return result;
    }

}
