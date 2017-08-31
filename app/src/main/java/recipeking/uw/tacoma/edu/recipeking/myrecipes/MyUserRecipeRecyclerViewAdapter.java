package recipeking.uw.tacoma.edu.recipeking.myrecipes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import recipeking.uw.tacoma.edu.recipeking.R;
import recipeking.uw.tacoma.edu.recipeking.myrecipes.UserRecipeFragment.OnListFragmentInteractionListener;
import recipeking.uw.tacoma.edu.recipeking.recipes.list.recipe.Recipe;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Recipe} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyUserRecipeRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecipeRecyclerViewAdapter.ViewHolder> {

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
    public MyUserRecipeRecyclerViewAdapter(List<Recipe> items, OnListFragmentInteractionListener
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
                .inflate(R.layout.fragment_userrecipe, parent, false);
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
        holder.mTitleView.setText(mValues.get(position).getmTitle());

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
        public final TextView mTitleView;

        public Recipe mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.myrecipe_title);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
