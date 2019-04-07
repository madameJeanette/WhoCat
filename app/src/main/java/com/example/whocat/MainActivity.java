package com.example.whocat;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class MainActivity extends AppCompatActivity
        implements CatAdapter.ListItemClickListener {
   // private TextView mCatBreedsTextView;

    private EditText mSearchBoxEditText;

//    private TextView mUrlDisplayTextView;

    private TextView mSearchResultsTextView;
    //  Create a variable to store a reference to the error message TextView
    private TextView mErrorMessageDisplay;

    // Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar mLoadingIndicator;

    private static final int NUM_LIST_ITEMS = 100;
    private CatAdapter mAdapter;
    private RecyclerView mNumbersList;

    private Toast mToast;


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
   //     mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        // get a reference to mSearchResultsTextView
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_cat_search_results_json);
        // Get a reference to the error TextView using findViewById
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

         // Get a reference to the ProgressBar using findViewById
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


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
    public void onClickOpenWebpageGifButton(View v){
        // Create a String that contains a URL ( make sure it starts with http:// or https:// )
        String urlAsString = "https://randomcatgifs.com/";

        // Replace the Toast with a call to openWebPage, passing in the URL String from the previous step
        openWebPage(urlAsString);

    }  public void onClickOpenAdoptButton(View v){
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
            URL catSearchUrl = NetworkUtils.buildUrl(catQuery);
         //   mUrlDisplayTextView.setText(githubSearchUrl.toString());

            // Create a new CatQueryTask and call its execute method, passing in the url to query
            new CatQueryTask().execute(catSearchUrl);
        }
    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showJsonDataView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

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



    // Create a class called QueryTask that extends AsyncTask<URL, Void, String>
            public class CatQueryTask extends AsyncTask<URL, Void, String> {

                // Override onPreExecute to set the loading indicator to visible
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                }

                @Override
                protected String doInBackground(URL... params) {
                    URL searchUrl = params[0];
                    String searchResults = null;
                    try {
                        searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return searchResults;
                }

            //     Override onPostExecute to display the results in the TextView
                @Override
                protected void onPostExecute(String searchResults) {
                        //  As soon as the loading is complete, hide the loading indicator
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (searchResults != null && !searchResults.equals("[]")) {
                    //Call showJsonDataView if we have valid, non-null results
                    showJsonDataView();
                    mSearchResultsTextView.setText(searchResults);
                } else {
                    // Call showErrorMessage if the result is null in onPostExecute
                    showErrorMessage();
                }
            }
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

        // COMPLETED Show a Toast when an item is clicked, displaying that item number that was clicked
        /*
         * Create a Toast and store it in our Toast field.
         * The Toast that shows up will have a message similar to the following:
         *
         *                     Item #42 clicked.
//         */
//        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
//        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
//
//        mToast.show();
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
// COMPLETED (1) Create a method called showMap with a Uri as the single parameter
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
        // COMPLETED (2) Create an Intent with action type, Intent.ACTION_VIEW
        /*
         * Again, we create an Intent with the action, ACTION_VIEW because we want to VIEW the
         * contents of this Uri.
         */
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // COMPLETED (3) Set the data of the Intent to the Uri passed into this method
        /*
         * Using setData to set the Uri of this Intent has the exact same affect as passing it in
         * the Intent's constructor. This is simply an alternate way of doing this.
         */
        intent.setData(geoLocation);


        // COMPLETED (4) Verify that this Intent can be launched and then call startActivity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
}
}

