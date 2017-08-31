package recipeking.uw.tacoma.edu.recipeking.myrecipes;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;

import recipeking.uw.tacoma.edu.recipeking.MainActivity;
import recipeking.uw.tacoma.edu.recipeking.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserRecipeAddFragment.RecipeAddListener} interface
 * to handle interaction events.
 * Use the {@link UserRecipeAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserRecipeAddFragment extends Fragment {

    /** String for representing the server ADD recipe url. */
    private final static String ADD_MYRECIPE_URL =
            "https://recipeking.000webhostapp.com/addRecipe.php?";

    /** EditText view representing the title of the recipe. */
    private EditText mRecipeTitleET;

    /** EditText view representing the details of the recipe. */
    private EditText mRecipeDetailsET;

    /** RecipeAddListener representing the listener for adding a recipe. */
    private RecipeAddListener mListener;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface RecipeAddListener {
        void addRecipe(String url);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserRecipeAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserRecipeAddFragment.
     */
    public static UserRecipeAddFragment newInstance() {
        UserRecipeAddFragment fragment = new UserRecipeAddFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreate method for this fragment.
     * @param savedInstanceState - the savedInstanceState for this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
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
        View view = inflater.inflate(R.layout.fragment_user_recipe_add, container, false);

        mRecipeTitleET = (EditText) view.findViewById(R.id.add_recipe_title);
        mRecipeDetailsET = (EditText) view.findViewById(R.id.add_recipe_details);
        Button addRecipeBtn = (Button) view.findViewById(R.id.add_recipe_button);

        addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean invalidT = false;
                boolean invalidD = false;
                String title = mRecipeTitleET.getText().toString();
                String details = mRecipeDetailsET.getText().toString();

                if(title.isEmpty() || title.length() > 100) {
                    invalidT = true;
                    mRecipeTitleET.setError("Try again");
                    mRecipeTitleET.requestFocus();
                    Toast.makeText(getActivity(), "Title is invalid."
                            , Toast.LENGTH_SHORT)
                            .show();
                }

                if(details.isEmpty()) {
                    invalidD = true;
                    mRecipeDetailsET.setError("Details cannot be empty");
                    mRecipeDetailsET.requestFocus();
                    Toast.makeText(getActivity(), "Details are invalid."
                            , Toast.LENGTH_SHORT)
                            .show();
                }

                if(!(invalidT || invalidD)) {
                    String url = buildRecipeAddUrl(v);
                    mListener.addRecipe(url);
                }

            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton)
                getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();

        return view;
    }

    /**
     * Builds the add url for adding a recipe to the server.
     * @param v - the view element calling.
     * @return - a String.
     */
    private String buildRecipeAddUrl(View v) {
        StringBuilder sb = new StringBuilder(ADD_MYRECIPE_URL);
        try {
            String recipeTitle = mRecipeTitleET.getText().toString();
            sb.append("title=");
            sb.append(URLEncoder.encode(recipeTitle, "UTF-8"));

            String currUser = MainActivity.currentUser;
            sb.append("&user=");
            sb.append(URLEncoder.encode(currUser, "UTF-8"));

            String details = mRecipeDetailsET.getText().toString();
            sb.append("&recipe_details=");
            sb.append(URLEncoder.encode(details, "UTF-8"));

            Log.i("UserRecipeAddFragment", sb.toString());

        } catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        return sb.toString();
    }


    /**
     * onAttach method for this fragment. Sets up the listener.
     * @param context - the context.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecipeAddListener) {
            mListener = (RecipeAddListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * onDetach method for this fragment.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
