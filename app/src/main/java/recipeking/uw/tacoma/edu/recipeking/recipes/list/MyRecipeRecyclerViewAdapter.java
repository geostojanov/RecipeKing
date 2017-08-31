package recipeking.uw.tacoma.edu.recipeking.recipes.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import recipeking.uw.tacoma.edu.recipeking.R;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.RecipeFragment.OnListFragmentInteractionListener;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Recipe} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyRecipeRecyclerViewAdapter extends RecyclerView.Adapter<MyRecipeRecyclerViewAdapter.ViewHolder> {

    /** The list of recipes for the RecyclerView */
    private final List<Recipe> mValues;

    /** Listener for interacting with the list of recipes. */
    private final OnListFragmentInteractionListener mListener;

    /**
     * Constructor for this class.
     *
     * @param items - the recipe items.
     * @param listener - the interaction listener.
     */
    public MyRecipeRecyclerViewAdapter(List<Recipe> items, OnListFragmentInteractionListener
            listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * onCreateViewHolder() method for this RecyclerViewer.
     *
     * @param parent - The ViewGroup parent object.
     * @param viewType - the viewType integer.
     * @return - a VIewHolder object.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipe, parent, false);
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder() method for this RecyclerViewer.
     *
     * @param holder - ViewHolder object.
     * @param position - the position of the item been clicked.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getmTitle());

        DownloadImageTask d = new DownloadImageTask(holder.mImageView);
        d.execute(mValues.get(position).getmImageUrl());
        
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /** Returns the number of items. */
    @Override
    public int getItemCount() {
        return mValues.size();
    }


    /**
     * Inner class representing the item of the list.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageView mImageView;
        public Recipe mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
            mImageView = (ImageView) view.findViewById(R.id.recipe_list_iv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    /**
     * AsyncTask for downloading the images for the recipes, which will be displayed in the list.
     */
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
