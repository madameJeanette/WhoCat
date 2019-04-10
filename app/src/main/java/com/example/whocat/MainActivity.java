package com.example.whocat;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whocat.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
// Implement CatAdapter.ListItemClickListener from the MainActivity
// implement LoaderManager.LoaderCallbacks<String> on MainActivity
public class MainActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<String>, CatAdapter.ListItemClickListener {

    //  Create a static final key to store the search's raw JSON
    /* A constant to save and restore the JSON that is being displayed */
    private static final String SEARCH_QUERY_URL_EXTRA = "query";
   // private TextView mCatBreedsTextView;
   // Create a constant int to uniquely identify your loader. Call it CAT_SEARCH_LOADER
   /*
    * This number will uniquely identify our Loader and is chosen arbitrarily. You can change this
    * to any number you like, as long as you use the same variable name.
    */
    private static final int CAT_SEARCH_LOADER = 22;

    private EditText mSearchBoxEditText;

    private TextView mUrlDisplayTextView;

    private TextView mSearchResultsTextView;
    //  Create a variable to store a reference to the error message TextView
    private TextView mErrorMessageDisplay;

    // Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar mLoadingIndicator;

    private static final int NUM_LIST_ITEMS = 100;
    private CatAdapter mAdapter;
    private RecyclerView mNumbersList;

    private Toast mToast;
    // Create a static final key to store the query's URL
    /* A constant to save and restore the URL that is being displayed */





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mNumbersList = (RecyclerView) findViewById(R.id.rv_cats);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNumbersList.setLayoutManager(layoutManager);


        mNumbersList.setHasFixedSize(true);

        mAdapter = new CatAdapter(NUM_LIST_ITEMS, this);

        mNumbersList.setAdapter(mAdapter);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        //get a reference to mUrlDisplayTextView
       mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        // get a reference to mSearchResultsTextView
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_cat_search_results_json);
        // Get a reference to the error TextView using findViewById
      //  mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

         // Get a reference to the ProgressBar using findViewById
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // If the savedInstanceState bundle is not null, set the text of the URL and search results TextView respectively
        if (savedInstanceState != null) {
            String queryUrl = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);

            mUrlDisplayTextView.setText(queryUrl);

        }
        // Initialize the loader with GITHUB_SEARCH_LOADER as the ID, null for the bundle, and this for the callback
        /*
         * Initialize the loader
         */
        getSupportLoaderManager().initLoader(CAT_SEARCH_LOADER, null, this);
    }

//        mCatBreedsTextView = (TextView) findViewById(R.id.tv_cat_breeds);
//        /*
//         * This String array contains names of cat breeds.
//         */
//        String[] catBreeds = Cat.getCatBreeds();
//        /*
//         * Iterate through the array and append the Strings to the TextView.
//         * the "\n\n\n" after the String is to give visual separation between each String in the
//         * TextView.
//         */
//        for (String catBreed : catBreeds) {
//            mCatBreedsTextView.append(catBreed + "\n\n\n");

    /**
     * This method is called when the Open Website button is clicked. It will open the website
     * specified by the URL represented by the variable urlAsString using implicit Intents.
     *
     * @param v Button that was clicked.
     */


      public void onClickOpenAdoptButton(View v){
        // Create a String that contains a URL ( make sure it starts with http:// or https:// )
        String urlAsString = "https://ikzoekbaas.dierenbescherming.nl/";

        // Replace the Toast with a call to openWebPage, passing in the URL String from the previous step
        openWebPage(urlAsString);

    }


    /**
     * This method is called when the Open Location in Map button is clicked. It will open the
     * a map to the location represented by the variable addressString using implicit Intents.
     *
     * @param v Button that was clicked.
     */
    public void onClickOpenAddressButton(View v) {
        // Store an address in a String

        String addressString = "Groene Kruisweg 14a, 3281 KB Numansdorp";

        // Use Uri.Builder with the appropriate scheme and query to form the Uri for the address
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("geo")
                .path("0,0")
                .appendQueryParameter("q", addressString);
        Uri addressUri = builder.build();

        // Replace the Toast with a call to showMap, passing in the Uri from the previous step
        showMap(addressUri);
    }

        private void makeSearchQuery() {
            String catQuery = mSearchBoxEditText.getText().toString();
            // If no search was entered, indicate that there isn't anything to search for and return
            /*
             * If the user didn't enter anything, there's nothing to search for. In the case where no
             * search text was entered but the search button was clicked, we will display a message
             * stating that there is nothing to search for and we will not attempt to load anything.
             *
             * If there is text entered in the search box when the search button was clicked, we will
             * create the URL that will return our Github search results, display that URL, and then
             * pass that URL to the Loader. The reason we pass the URL as a String is simply a matter
             * of convenience. There are other ways of achieving this same result, but we felt this
             * was the simplest.
             */
            if (TextUtils.isEmpty(catQuery)) {
                mSearchResultsTextView.setText("No cats found :( please try again, you know how they are.");
                return;
            }

            URL catSearchUrl = NetworkUtils.buildUrl(catQuery);
            mUrlDisplayTextView.setText(catSearchUrl.toString());

            // Remove the call to execute the AsyncTask

            // Create a bundle called queryBundle
            Bundle queryBundle = new Bundle();
            // Use putString with SEARCH_QUERY_URL_EXTRA as the key and the String value of the URL as the value
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, catSearchUrl.toString());

            /*
             * Now that we've created our bundle that we will pass to our Loader, we need to decide
             * if we should restart the loader (if the loader already existed) or if we need to
             * initialize the loader (if the loader did NOT already exist).
             *
             * We do this by first store the support loader manager in the variable loaderManager.
             * All things related to the Loader go through through the LoaderManager. Once we have a
             * hold on the support loader manager, (loaderManager) we can attempt to access our
             * githubSearchLoader. To do this, we use LoaderManager's method, "getLoader", and pass in
             * the ID we assigned in its creation. You can think of this process similar to finding a
             * View by ID. We give the LoaderManager an ID and it returns a loader (if one exists). If
             * one doesn't exist, we tell the LoaderManager to create one. If one does exist, we tell
             * the LoaderManager to restart it.
             */
            //Call getSupportLoaderManager and store it in a LoaderManager variable
            LoaderManager loaderManager = getSupportLoaderManager();
            //  Get our Loader by calling getLoader and passing the ID we specified
            Loader<String> catSearchLoader = loaderManager.getLoader(CAT_SEARCH_LOADER);
            // f the Loader was null, initialize it. Else, restart it.
            if (catSearchLoader == null) {
                loaderManager.initLoader(CAT_SEARCH_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(CAT_SEARCH_LOADER, queryBundle, this);
            }
        }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
//    private void showJsonDataView() {
//        // First, make sure the error is invisible
//        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
//        // Then, make sure the JSON data is visible
//        mSearchResultsTextView.setVisibility(View.VISIBLE);
//    }

    // Create a method called showErrorMessage to show the error and hide the data
    /**
     * This method will make the error message visible and hide the JSON
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        // Then, show the error


    }
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        //  Return a new AsyncTaskLoader<String> as an anonymous inner class with this as the constructor's parameter
        return new AsyncTaskLoader<String>(this) {
            //Create a String member variable called mGithubJson that will store the raw JSON
            /* This String will contain the raw JSON from the results of our Cat api search */
            String mCatsJson;

            // Override onStartLoading
            @Override
            protected void onStartLoading() {

                // If args is null, return.
                /* If no arguments were passed, we don't have a query to perform. Simply return. */
                if (args == null) {

                    return;
                }


                // If mGithubJson is not null, deliver that result. Otherwise, force a load
                /*
                 * If we already have cached results, just deliver them now. If we don't have any
                 * cached results, force a load.
                 */
                if (mCatsJson != null) {
                    deliverResult(mCatsJson);
                } else {

                // Show the loading indicator
                /*
                 * When we initially begin loading in the background, we want to display the
                 * loading indicator to the user
                 */
                mLoadingIndicator.setVisibility(View.VISIBLE);

                // Force a load
                forceLoad();
            }}

            // Override loadInBackground
            @Override
            public String loadInBackground() {

                //  Get the String for our URL from the bundle passed to onCreateLoader
                /* Extract the search query from the args using our constant */
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);

                // If the URL is null or empty, return null
                /* If the user didn't enter anything, there's nothing to search for */
                if (TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }

                //  Copy the try / catch block from the AsyncTask's doInBackground method
                /* Parse the URL from the passed in String and perform the search */
                try {
                    URL catUrl = new URL(searchQueryUrlString);
                    String catSearchResults = NetworkUtils.getResponseFromHttpUrl(catUrl);
                    return catSearchResults;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

                // Override deliverResult and store the data in mGithubJson
                //Call super.deliverResult after storing the data
                @Override
                public void deliverResult(String catJson) {
                    mCatsJson = catJson;
                    super.deliverResult(catJson);

            }
        };
    }  //  Override onLoadFinished
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        // Hide the loading indicator
        /* When we finish loading, we want to hide the loading indicator from the user. */
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        //  Use the same logic used in onPostExecute to show the data or the error message
        /*
         * If the results are null, we assume an error has occurred. There are much more robust
         * methods for checking errors, but we wanted to keep this particular example simple.
         */
        if (null == data) {
            showErrorMessage();
        } else {
            mSearchResultsTextView.setText(data);
        //    showJsonDataView();
        }
    }

    // Override onLoaderReset as it is part of the interface we implement, but don't do anything in this method
    @Override
    public void onLoaderReset(Loader<String> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // onCreateOptionsMenu, use to inflate the menu
            getMenuInflater().inflate(R.menu.main, menu);
            // Return true to display  menu
            return true;
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
            switch (itemId) {

                case R.id.action_refresh:
                    //  Pass in this as the ListItemClickListener to the CatAdapter constructor
                    mAdapter = new CatAdapter(NUM_LIST_ITEMS, this);
                    mNumbersList.setAdapter(mAdapter);
                    return true;

            case R.id.action_search:

                makeSearchQuery(); //Call makeSearchQuery when the search menu item is clicked.
                return true;
            }
            return super.onOptionsItemSelected(item);

        }
    // Override ListItemClickListener's onListItemClick method
    // This is where we receive our callback from
     // {@link

     // callback is invoked when you click on an item in the list.

     // @param clickedItemIndex Index in the list of the item that was clicked.

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // In the beginning of the method, cancel the Toast if it isn't null
        /*
         * Even if a Toast isn't showing, it's okay to cancel it. Doing so
         * ensures that our new Toast will show immediately, rather than
         * being delayed while other pending Toasts are shown.
         *
         * Comment out these three lines, run the app, and click on a bunch of
         * different items if you're not sure what I'm talking about.
         */
        if (mToast != null) {
            mToast.cancel();
        }


        // Retrieve the text from the EditText and store it in a variable
        /* We'll first get the text entered by the user in the EditText */
        String textEntered = mSearchResultsTextView.getText().toString();

        Context context = MainActivity.this;
        // Store ChildActivity.class in a Class object called destinationActivity
        /* This is the class that we want to start (and open) when the button is clicked. */
        Class destinationActivity = ChildActivity.class;


        //  Create an Intent to start ChildActivity
        /*
         * Here, we create the Intent that will start the Activity we specified above in
         * the destinationActivity variable. The constructor for an Intent also requires a
         * context, which we stored in the variable named "context".
         */
        Intent startChildActivityIntent = new Intent(context, destinationActivity);

        // Use the putExtra method to put the String from the EditText in the Intent
        /*
         * We use the putExtra method of the Intent class to pass some extra stuff to the
         * Activity that we are starting. Generally, this data is quite simple, such as
         * a String or a number. However, there are ways to pass more complex objects.
         */
        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, textEntered);


        //  Replace the Toast with code to start ChildActivity
        /*
         * Once the Intent has been created, we can use Activity's method, "startActivity"
         * to start the ChildActivity.
         */
        startActivity(startChildActivityIntent);

    }

    //  Create a method called openWebPage that accepts a String as a parameter
    /**
     * This method fires off an implicit Intent to open a webpage.
     *
     * @param url Url of webpage to open. Should start with http:// or https:// as that is the
     *            scheme of the URI expected with this Intent according to the Common Intents page
     */
    private void openWebPage(String url) {
        // Use Uri.parse to parse the String into a Uri
        /*
         * We wanted to demonstrate the Uri.parse method because its usage occurs frequently. You
         * could have just as easily passed in a Uri as the parameter of this method.
         */
        Uri webpage = Uri.parse(url);

        //  Create an Intent with Intent.ACTION_VIEW and the webpage Uri as parameters
        /*
         * Here, we create the Intent with the action of ACTION_VIEW. This action allows the user
         * to view particular content. In this case, our webpage URL.
         */
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Verify that this Intent can be launched and then call startActivity
        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
// Create a method called showMap with a Uri as the single parameter
    /**
     * This method will fire off an implicit Intent to view a location on a map.
     *
     * When constructing implicit Intents, you can use either the setData method or specify the
     * URI as the second parameter of the Intent's constructor,
     * as I do in {@link #openWebPage(String)}
     *
     * @param geoLocation The Uri representing the location that will be opened in the map
     */
    private void showMap(Uri geoLocation) {
        //  Create an Intent with action type, Intent.ACTION_VIEW
        /*
         * Again, we create an Intent with the action, ACTION_VIEW because we want to VIEW the
         * contents of this Uri.
         */
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Set the data of the Intent to the Uri passed into this method
        /*
         * Using setData to set the Uri of this Intent has the exact same affect as passing it in
         * the Intent's constructor. This is simply an alternate way of doing this.
         */
        intent.setData(geoLocation);


        // Verify that this Intent can be launched and then call startActivity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    // Override onSaveInstanceState to persist data across Activity recreation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //  Make sure super.onSaveInstanceState is called before doing anything else
        super.onSaveInstanceState(outState);

        // Put the contents of the TextView that contains our URL into a variable
        String queryUrl = mUrlDisplayTextView.getText().toString();

        // Using the key for the query URL, put the string in the outState Bundle
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl);


    }
}

