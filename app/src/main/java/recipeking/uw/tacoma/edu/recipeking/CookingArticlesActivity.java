package recipeking.uw.tacoma.edu.recipeking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * This class represents an activity for cooking articles.
 */
public class CookingArticlesActivity extends AppCompatActivity {

    /**
     * onCreate method for the this activity. Sets the layout.
     * @param savedInstanceState - the saved arguments for this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking_articles);

    }


    /**
     * Opens a web page based on what the user clicked.
     * @param view - The view object that called this method.
     */
    public void openArticle(View view) {
        String url = "";

        if (view.getId() == R.id.cooking_article_1) {
            url = "http://honestcooking.com/";
        } else if (view.getId() == R.id.cooking_article_2) {
            url = "http://www.cooksmarts.com/articles/";
        } else if (view.getId() == R.id.cooking_article_3) {
            url = "http://www.livescience.com/topics/food-cooking-science";
        } else if (view.getId() == R.id.cooking_article_4) {
            url = "http://www.chef2chef.net/articles/in-the-fire/";
        } else if (view.getId() == R.id.cooking_article_5) {
            url = "http://www.epicurious.com/";
        }

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        String title = getResources().getString(R.string.chooser_title);
        Intent chooser = Intent.createChooser(intent, title);

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }
}
