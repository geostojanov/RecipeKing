package recipeking.uw.tacoma.edu.recipeking;


import org.junit.Test;

import java.util.ArrayList;

import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotEquals;

public class RecipeTest {

    @Test
    public void testRecipeConstructor() {
        assertNotNull(new Recipe("Title1", "Url1", new String[]{"Ing1", "Ing2"}, "Instructions1"));
    }

    @Test
    public void testRecipeConstructor2() {
        assertNotNull(new Recipe("Title1", "RecipeDetails1"));
    }

    @Test
    public void testRecipeGetTitle() {
        Recipe r = new Recipe("Title1", "Details1");
        assertEquals("", "Title1", r.getmTitle());
    }

    @Test
    public void testRecipeGetDetails() {
        Recipe r = new Recipe("Title1", "Details1");
        assertEquals("", "Details1", r.getmRecipeDetails());
    }

    @Test
    public void testRecipeGetImageUrl() {
        Recipe r = new Recipe("Title1", "Url1", new String[]{"Ing1", "Ing2"}, "Instructions1");
        assertEquals("", "Url1", r.getmImageUrl());
    }

    @Test
    public void testRecipeGetIngredients() {
        Recipe r = new Recipe("Title1", "Url1", new String[]{"Ing1", "Ing2"}, "Instructions1");
        assertEquals("", "Ing1", r.getmIngredients()[0]);
        assertEquals("", "Ing2", r.getmIngredients()[1]);
    }

    @Test
    public void testRecipeGetInstructions() {
        Recipe r = new Recipe("Title1", "Url1", new String[]{"Ing1", "Ing2"}, "Instructions1");
        assertEquals("", "Instructions1", r.getmInstructionsUrl());
    }

    @Test
    public void testRecipeEqualsOnSameTitle() {
        Recipe r1 = new Recipe("Title1", "Details");
        Recipe r2 = new Recipe("Title1", "Other details");

        assertEquals(r1, r2);

    }

    @Test
    public void testRecipeEqualsOnDifferentTitle() {
        Recipe r1 = new Recipe("Title1", "Details");
        Recipe r2 = new Recipe("Title2", "Other details");

        assertNotEquals(r1, r2);

    }

    @Test
    public void testRecipeParseMyRecipesJSONOnNullString() {
        String result = Recipe.parseMyRecipesJSON(null, new ArrayList<Recipe>());
        assertNull(result);
    }

    @Test
    public void testRecipeParseMyRecipesJSONOnFailString() {
        String result = Recipe.parseMyRecipesJSON("fail", new ArrayList<Recipe>());
        assertEquals("No recipes found.", result);
    }


    @Test
    public void testRecipeParseFavRecipesJSONOnNullString() {
        String result = Recipe.parseFavRecipesJSON(null, new ArrayList<Recipe>());
        assertNull(result);
    }

    @Test
    public void testRecipeParseRecipesJSONOnNullString() {
        String result = Recipe.parseFavRecipesJSON(null, new ArrayList<Recipe>());
        assertNull(result);
    }

}
